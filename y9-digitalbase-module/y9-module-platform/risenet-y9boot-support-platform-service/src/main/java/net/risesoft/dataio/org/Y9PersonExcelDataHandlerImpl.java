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
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.enums.platform.org.SexEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.Job;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Person;
import net.risesoft.pojo.Person4Excel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.util.Y9OrgUtil;
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
        FastExcel.write(outputStream, Person4Excel.class).sheet().doWrite(getPersonList(orgBaseId));
    }

    private List<Person4Excel> getPersonList(String orgBaseId) {
        List<Person> persons = compositeOrgBaseService.listAllDescendantPersons(orgBaseId);
        List<Person4Excel> personList = new ArrayList<>();
        for (Person person : persons) {
            Person4Excel person4Excel = new Person4Excel();

            person4Excel.setName(person.getName());
            person4Excel.setDepartmentNamePath(getDepartmentNamePath(person));
            person4Excel.setEmail(person.getEmail());
            person4Excel.setLoginName(person.getLoginName());
            person4Excel.setMobile(person.getMobile());
            person4Excel.setSex(person.getSex().getDescription());
            List<Job> y9JobList = y9JobService.findByPersonId(person.getId());
            person4Excel.setJobs(y9JobList.stream().map(Job::getName).collect(Collectors.joining(SPLITTER)));
            personList.add(person4Excel);
        }
        return personList;
    }

    private String getDepartmentNamePath(Person person) {
        String namePath = Y9OrgUtil.dnToNamePath(person.getDn(), OrgLevelConsts.SEPARATOR);
        namePath = StringUtils.substringAfter(namePath, OrgLevelConsts.SEPARATOR);
        if (StringUtils.contains(namePath, OrgLevelConsts.SEPARATOR)) {
            return StringUtils.substringBeforeLast(namePath, OrgLevelConsts.SEPARATOR);
        } else {
            return null;
        }
    }

    /**
     * 导入XLS组织架构到数据库
     *
     */
    @Override
    public Y9Result<Object> importPerson(InputStream dataInputStream, String orgId) {
        List<ExcelImportError> excelImportErrorList = new ArrayList<>();
        FastExcel.read(dataInputStream, Person4Excel.class, new ReadListener<Person4Excel>() {
            @Override
            public void invoke(Person4Excel data, AnalysisContext context) {
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

    private void impData2Db(Person4Excel person4Excel, String orgId) {
        String personName = StringUtils.trim(person4Excel.getName());
        String personLoginName = StringUtils.trim(person4Excel.getLoginName());
        String personMobile = StringUtils.trim(person4Excel.getMobile());

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

        Optional<Person> y9PersonOptional = y9PersonService.findByLoginName(personLoginName);
        if (y9PersonOptional.isPresent()) {
            throw new IllegalArgumentException("该登录名已被使用");
        }

        String fullPath = Optional.ofNullable(person4Excel.getDepartmentNamePath()).orElse("");
        String[] departments = fullPath.split(SPLITTER);

        OrgUnit orgUnit = compositeOrgBaseService.getOrgUnit(orgId);
        String dn = orgUnit.getDn();
        String parentId = orgUnit.getId();

        for (int i = 0, length = departments.length; i < length; i++) {
            dn = OrgLevelConsts.UNIT + departments[i] + SPLITTER + dn;
            List<Department> departmentList = y9DepartmentService.listByDn(dn, false);
            if (!departmentList.isEmpty()) {
                parentId = departmentList.get(0).getId();
            } else {
                // 不存在的部门则创建
                Department department = new Department();
                department.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                department.setTenantId(Y9LoginUserHolder.getTenantId());
                department.setName(StringUtils.trim(departments[i]));
                department.setOrgType(OrgTypeEnum.DEPARTMENT);
                department.setParentId(parentId);
                Department dept = y9DepartmentService.saveOrUpdate(department);
                parentId = dept.getId();
            }
        }

        Person y9Person = new Person();
        y9Person.setName(personName);
        y9Person.setEmail(person4Excel.getEmail());
        y9Person.setMobile(personMobile);
        y9Person.setLoginName(personLoginName);
        y9Person.setSex(SexEnum.MALE.getDescription().equals(person4Excel.getSex()) ? SexEnum.MALE : SexEnum.FEMALE);
        y9Person.setParentId(parentId);

        String jobs = person4Excel.getJobs();
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

}
