package net.risesoft.dataio.org;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.dataio.ExcelImportError;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Job;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.enums.platform.SexEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.PersonInformation;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.y9.Y9LoginUserHolder;

import cn.hutool.core.lang.Validator;
import cn.idev.excel.FastExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;

/**
 * 人员 Excel 数据导入导出
 *
 * @author shidaobang
 * @date 2025/02/10
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class Y9PersonExcelDataHandlerImpl implements Y9PersonDataHandler {

    private static final String SPLITTER = ",";

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9DepartmentService y9DepartmentService;
    private final Y9PersonService y9PersonService;
    private final Y9JobService y9JobService;

    @Override
    public void exportPerson(OutputStream outputStream, String orgBaseId) {
        FastExcel.write(outputStream, PersonInformation.class).sheet().doWrite(getPersonList(orgBaseId));
    }

    private List<PersonInformation> getPersonList(String orgBaseId) {
        List<Y9Person> persons = compositeOrgBaseService.listAllDescendantPersons(orgBaseId);
        List<PersonInformation> personList = new ArrayList<>();
        for (Y9Person person : persons) {
            PersonInformation personInformation = new PersonInformation();
            String fullPath = person.getDn().replace("cn=", "").replace(",ou=", SPLITTER).replace(",o=", SPLITTER);
            String path = fullPath.substring(0, fullPath.lastIndexOf(SPLITTER));
            if (path.contains(SPLITTER) && fullPath.lastIndexOf(SPLITTER) == fullPath.lastIndexOf(SPLITTER)) {
                path = path.substring(fullPath.indexOf(SPLITTER) + 1);
                personInformation.setFullPath(reverseSplit(path));
            } else if (!path.contains(SPLITTER)) {
                personInformation.setFullPath(null);
            } else {
                path = path.substring(fullPath.indexOf(SPLITTER) + 1, fullPath.lastIndexOf(SPLITTER));
                personInformation.setFullPath(reverseSplit(path));
            }
            personInformation.setName(fullPath.substring(0, fullPath.indexOf(SPLITTER)));
            personInformation.setEmail(person.getEmail());
            personInformation.setLoginName(person.getLoginName());
            personInformation.setMobile(person.getMobile());
            personInformation.setSex(person.getSex().getDescription());
            List<Y9Job> y9JobList = y9JobService.findByPersonId(person.getId());
            personInformation.setJobs(y9JobList.stream().map(Y9Job::getName).collect(Collectors.joining(SPLITTER)));
            personList.add(personInformation);
        }
        return personList;
    }

    /**
     * 导入XLS组织架构到数据库
     *
     */
    @Override
    public Y9Result<Object> importPerson(InputStream dataInputStream, String orgId) {
        List<ExcelImportError> excelImportErrorList = new ArrayList<>();
        FastExcel.read(dataInputStream, PersonInformation.class, new ReadListener<PersonInformation>() {
            @Override
            public void invoke(PersonInformation data, AnalysisContext context) {
                try {
                    impData2Db(data, orgId);
                } catch (Exception e) {
                    Integer rowNumber = context.readRowHolder().getRowIndex();
                    excelImportErrorList.add(new ExcelImportError(rowNumber, e.getMessage()));
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {

            }
        }).sheet().doRead();

        if (excelImportErrorList.isEmpty()) {
            return Y9Result.success();
        } else {
            return Y9Result.failure(excelImportErrorList, "导入有错误");
        }
    }

    private void impData2Db(PersonInformation pi, String orgId) {
        String personName = StringUtils.trim(pi.getName());
        String personLoginName = StringUtils.trim(pi.getLoginName());
        String personMobile = StringUtils.trim(pi.getMobile());

        if (StringUtils.isBlank(personName)) {
            throw new IllegalArgumentException("人员中文名称不能为空");
        }

        if (StringUtils.isBlank(personLoginName)) {
            throw new IllegalArgumentException("登录名称不能为空");
        }

        if (StringUtils.isBlank(personMobile)) {
            throw new IllegalArgumentException("手机号不能为空");
        }

        if (!Validator.isMobile(personMobile)) {
            throw new IllegalArgumentException("手机号不合法");
        }

        Optional<Y9Person> y9PersonOptional = y9PersonService.findByLoginName(personLoginName);
        if (y9PersonOptional.isPresent()) {
            throw new IllegalArgumentException("该登录名已被使用");
        }

        String fullPath = Optional.ofNullable(pi.getFullPath()).orElse("");
        String[] departments = fullPath.split(SPLITTER);

        Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(orgId);
        String dn = y9OrgBase.getDn();
        String parentId = y9OrgBase.getId();

        for (int i = 0, length = departments.length; i < length; i++) {
            dn = OrgLevelConsts.UNIT + departments[i] + SPLITTER + dn;
            List<Y9Department> departmentList = y9DepartmentService.listByDn(dn, false);
            if (!departmentList.isEmpty()) {
                parentId = departmentList.get(0).getId();
            } else {
                // 不存在的部门则创建
                Y9Department department = new Y9Department();
                department.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                department.setTenantId(Y9LoginUserHolder.getTenantId());
                department.setName(StringUtils.trim(departments[i]));
                department.setOrgType(OrgTypeEnum.DEPARTMENT);
                department.setParentId(parentId);
                Y9Department dept = y9DepartmentService.saveOrUpdate(department);
                parentId = dept.getId();
            }
        }

        Y9Person y9Person = new Y9Person();
        y9Person.setName(personName);
        y9Person.setEmail(pi.getEmail());
        y9Person.setMobile(personMobile);
        y9Person.setLoginName(personLoginName);
        y9Person.setSex(SexEnum.MALE.getDescription().equals(pi.getSex()) ? SexEnum.MALE : SexEnum.FEMALE);
        y9Person.setParentId(parentId);

        String jobs = pi.getJobs();
        if (StringUtils.isNotBlank(jobs)) {
            String[] jobArray = jobs.split(SPLITTER);
            List<String> y9JobIdList = new ArrayList<>();
            for (String job : jobArray) {
                y9JobIdList.add(y9JobService.create(job, job).getId());
            }
            y9PersonService.saveOrUpdate(y9Person, null, null, y9JobIdList);
        } else {
            y9PersonService.saveOrUpdate(y9Person, null);
        }
    }

    private String reverseSplit(String path) {
        if (!path.contains(SPLITTER)) {
            return path;
        }
        String[] oldString = path.split(SPLITTER);
        StringBuilder strBuffer = new StringBuilder();
        for (int length = oldString.length; length > 0; length--) {
            strBuffer.append(oldString[length - 1]);
            strBuffer.append(SPLITTER);
        }
        String newString = strBuffer.toString();
        return newString.substring(0, newString.lastIndexOf(SPLITTER));
    }

}
