package net.risesoft.entity.org;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.consts.DefaultConsts;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.org.Position;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.util.Y9AssertUtil;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 岗位实体
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_POSITION")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "岗位表", appliesTo = "Y9_ORG_POSITION")
@Data
@NoArgsConstructor
public class Y9Position extends Y9OrgBase {

    private static final long serialVersionUID = -5753173583033676828L;

    {
        super.setOrgType(OrgTypeEnum.POSITION);
    }

    /** 父节点id */
    @Column(name = "PARENT_ID", length = 38, nullable = false)
    @Comment("父节点id")
    private String parentId;

    /** 职位id */
    @Column(name = "JOB_ID", length = 38, nullable = false)
    @Comment("职位id")
    private String jobId;

    /** 职位名称 */
    @Column(name = "JOB_NAME", length = 255, nullable = false)
    @Comment("职位名称")
    private String JobName;

    /** 排序序列号 */
    @Column(name = "ORDERED_PATH", length = 500)
    @Comment("排序序列号")
    private String orderedPath;

    /** 互斥的岗位Id列表，之间用逗号分割 */
    @Column(name = "EXCLUSIVE_IDS", length = 500)
    @Comment("互斥的岗位Id列表，之间用逗号分割")
    private String exclusiveIds;

    /** 岗位容量，默认容量为1，即一人一岗 */
    @ColumnDefault("1")
    @Column(name = "CAPACITY", nullable = false)
    @Comment("岗位容量，默认容量为1，即一人一岗")
    private Integer capacity = 1;

    /** 当前岗位人数，小于或等于岗位容量 */
    @ColumnDefault("0")
    @Column(name = "HEAD_COUNT", nullable = false)
    @Comment("岗位当前人数，小于或等于岗位容量")
    private Integer headCount = 0;

    public Y9Position(
        Position position,
        Y9Job y9Job,
        String positionNameTemplate,
        Y9OrgBase parent,
        List<Y9OrgBase> ancestorList,
        Integer nextSubTabIndex) {
        Y9BeanUtil.copyProperties(position, this);

        if (StringUtils.isBlank(this.id)) {
            this.id = Y9IdGenerator.genId();
        }
        this.parentId = parent.getId();
        this.jobId = y9Job.getId();
        this.JobName = y9Job.getName();
        this.name = buildName(positionNameTemplate, y9Job.getName(), Collections.emptyList());
        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.POSITION, this.name, parent.getDn());
        this.guidPath = Y9OrgUtil.buildGuidPath(parent.getGuidPath(), this.id);
        if (DefaultConsts.TAB_INDEX.equals(this.tabIndex)) {
            this.tabIndex = nextSubTabIndex;
        }
        this.orderedPath = Y9OrgUtil.buildOrderedPath(this, ancestorList);
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    public Boolean changeDisabled() {
        boolean targetStatus = !this.disabled;
        this.disabled = targetStatus;
        return targetStatus;
    }

    public void changeParent(Y9OrgBase parent, Integer nextSubTabIndex, List<Y9OrgBase> ancestorList) {
        this.parentId = parent.getId();
        this.tabIndex = nextSubTabIndex;
        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.POSITION, this.name, parent.getDn());
        this.guidPath = Y9OrgUtil.buildGuidPath(parent.getGuidPath(), this.id);
        this.orderedPath = Y9OrgUtil.buildOrderedPath(this, ancestorList);
    }

    public void update(Position position, Y9Job y9Job, String positionNameTemplate, Y9OrgBase parent,
        List<Y9OrgBase> ancestorList, List<Y9Person> y9PersonList) {
        Y9BeanUtil.copyProperties(position, this);

        // 修改的岗位容量不能小于当前岗位人数
        Y9AssertUtil.lessThanOrEqualTo(y9PersonList.size(), this.capacity, OrgUnitErrorCodeEnum.POSITION_IS_FULL,
            this.name);
        
        this.name = buildName(positionNameTemplate, y9Job.getName(), y9PersonList);
        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.POSITION, this.name, parent.getDn());
        this.guidPath = Y9OrgUtil.buildGuidPath(parent.getGuidPath(), this.id);
        this.orderedPath = Y9OrgUtil.buildOrderedPath(this, ancestorList);
        this.headCount = y9PersonList.size();
    }

    public void changeProperties(String properties) {
        this.properties = properties;
    }

    public void changeTabIndex(int tabIndex, List<Y9OrgBase> ancestorList) {
        this.tabIndex = tabIndex;
        this.orderedPath = Y9OrgUtil.buildOrderedPath(this, ancestorList);
    }

    public void changeJob(Y9Job y9Job, String positionNameTemplate, Y9OrgBase parent, List<Y9Person> y9PersonList) {
        this.JobName = y9Job.getName();
        this.name = buildName(positionNameTemplate, y9Job.getName(), y9PersonList);
        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.POSITION, this.name, parent.getDn());
    }
    
    public void changeName(String positionNameTemplate, Y9Job y9Job, Y9OrgBase parent, List<Y9Person> y9PersonList) {
        this.name = buildName(positionNameTemplate, y9Job.getName(), y9PersonList);
        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.POSITION, this.name, parent.getDn());
    }

    private String buildName(String positionNameTemplate, String jobName, List<Y9Person> personList) {
        String personNames;

        if (personList.isEmpty()) {
            personNames = "空缺";
        } else {
            personNames = personList.stream()
                .sorted((Comparator.comparing(Y9Person::getOrderedPath)))
                .map(Y9OrgBase::getName)
                .collect(Collectors.joining("，"));
        }

        return parseSpringEl(positionNameTemplate, jobName, personNames);
    }

    /**
     * springEL 支持，用于更灵活的岗位名称 <br>
     * 例如：positionNameTemplate 为 "#jobName.equals('无') ? #personNames : #jobName + '（' + #personNames + '）'" <br>
     *
     * @param positionNameTemplate 有 springEL 表达式的职位名称模板
     * @param jobName 职位名
     * @param personNames 人员名称
     * @return {@code String } 计算后的岗位名称
     */
    private String parseSpringEl(String positionNameTemplate, String jobName, String personNames) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(positionNameTemplate);
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("jobName", jobName);
        context.setVariable("personNames", personNames);
        return expression.getValue(context, String.class);
    }
}