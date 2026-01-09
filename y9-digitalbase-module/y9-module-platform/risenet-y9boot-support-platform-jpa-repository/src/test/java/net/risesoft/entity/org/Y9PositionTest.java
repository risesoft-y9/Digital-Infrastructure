package net.risesoft.entity.org;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.org.Position;

class Y9PositionTest {

    private Y9Position position;
    private Y9Job mockJob;

    // 为不同测试场景预定义组织架构数据
    private Y9Organization org123;
    private Y9Department dept123;
    private Y9Organization newOrg123;
    private Y9Department newDept123;

    @BeforeEach
    void setUp() {
        position = new Y9Position();
        mockJob = new Y9Job();
        mockJob.setId("job123");
        mockJob.setName("软件工程师");

        // 创建通用的组织架构数据
        org123 = new Y9Organization();
        org123.setId("org123");
        org123.setName("公司");
        org123.setDn("o=公司");
        org123.setGuidPath("org123");
        org123.setTabIndex(1); // 设置tabIndex

        dept123 = new Y9Department();
        dept123.setId("parent123");
        dept123.setParentId("org123"); // 部门的父节点是组织机构
        dept123.setDn("ou=研发部,o=公司");
        dept123.setGuidPath("org123,parent123"); // 部门的guidPath是"组织机构ID,部门ID"
        dept123.setName("研发部");
        dept123.setTabIndex(2); // 设置tabIndex

        newOrg123 = new Y9Organization();
        newOrg123.setId("newOrg123");
        newOrg123.setName("新公司");
        newOrg123.setDn("o=新公司");
        newOrg123.setGuidPath("newOrg123");
        newOrg123.setTabIndex(1); // 设置tabIndex

        newDept123 = new Y9Department();
        newDept123.setId("newParent123");
        newDept123.setParentId("newOrg123"); // 新父节点部门的父节点是新的组织机构
        newDept123.setDn("ou=新部门,o=新公司");
        newDept123.setGuidPath("newOrg123,newParent123"); // 部门的guidPath应该是"组织机构ID,部门ID"
        newDept123.setName("新部门");
        newDept123.setTabIndex(3); // 设置tabIndex
    }

    @Test
    void testConstructorWithParameters() {
        // 准备测试数据
        Position positionModel = new Position();
        positionModel.setId("pos123");
        positionModel.setName("软件工程师岗位");
        positionModel.setCapacity(2); // 设置容量为2

        // 使用预定义的数据
        List<Y9OrgBase> ancestors = Arrays.asList(dept123, org123);

        // 创建Y9Position实例
        Y9Position newPosition = new Y9Position(positionModel, mockJob, "#jobName", dept123, ancestors, 4); // tabIndex=4

        // 验证基本属性被正确设置
        assertEquals("pos123", newPosition.getId());
        assertEquals("parent123", newPosition.getParentId());
        assertEquals("job123", newPosition.getJobId());
        assertEquals("软件工程师", newPosition.getJobName());
        assertEquals("软件工程师", newPosition.getName()); // 因为模板是#jobName
        assertEquals("cn=软件工程师,ou=研发部,o=公司", newPosition.getDn());
        assertEquals("org123,parent123,pos123", newPosition.getGuidPath()); // 修正：组织机构ID,部门ID,岗位ID
        assertNotNull(newPosition.getOrderedPath());
        // orderedPath格式应该是：祖先节点的tabIndex倒序连接 + 当前节点tabIndex
        assertEquals("00001,00002,00004", newPosition.getOrderedPath()); // org tabIndex=1, dept tabIndex=2, pos
                                                                         // tabIndex=4
        assertEquals(Integer.valueOf(4), newPosition.getTabIndex());
        assertEquals(OrgTypeEnum.POSITION, newPosition.getOrgType());
    }

    @Test
    void testChangeDisabled() {
        // 初始状态应该是启用的（disabled=false）
        assertFalse(position.getDisabled());

        // 第一次调用changeDisabled应该变为true
        Boolean result = position.changeDisabled();
        assertTrue(result);
        assertTrue(position.getDisabled());

        // 再次调用应该变为false
        result = position.changeDisabled();
        assertFalse(result);
        assertFalse(position.getDisabled());
    }

    @Test
    void testChangeParent() {
        List<Y9OrgBase> newAncestors = Arrays.asList(newDept123, newOrg123);

        position.changeParent(newDept123, 5, newAncestors);

        assertEquals("newParent123", position.getParentId());
        assertEquals(Integer.valueOf(5), position.getTabIndex());
        assertEquals("cn=null,ou=新部门,o=新公司", position.getDn()); // 注意这里会是 null，因为name还没设置
        assertEquals("newOrg123,newParent123," + position.getId(), position.getGuidPath()); // 修正：组织机构ID,部门ID,岗位ID
        assertNotNull(position.getOrderedPath());
        assertEquals("00001,00003,00005", position.getOrderedPath()); // org tabIndex=1, dept tabIndex=3, pos tabIndex=5
    }

    @Test
    void testUpdateWithCapacityValidation() {
        // 设置初始容量为2
        position.setCapacity(2);

        // 准备人员列表，只有1人
        Y9Person person1 = new Y9Person();
        person1.setId("person1");
        person1.setName("张三");
        List<Y9Person> persons = Arrays.asList(person1);

        // 准备职位模型
        Position positionModel = new Position();
        positionModel.setCapacity(2);

        // 准备工作
        Y9Job job = new Y9Job();
        job.setId("job123");
        job.setName("高级工程师");

        List<Y9OrgBase> ancestors = Arrays.asList(dept123, org123);

        // 更新职位信息
        position.update(positionModel, job, "#jobName", dept123, ancestors, persons);

        // 验证更新后的值
        assertEquals(Integer.valueOf(2), position.getCapacity());
        assertEquals(Integer.valueOf(1), position.getHeadCount()); // 应该等于人员数量
        assertEquals("高级工程师", position.getName());
        assertNotNull(position.getOrderedPath());
        // tabIndex通常不会在update方法中改变，所以保持原来的值
        // orderedPath格式是: 祖先tabIndex倒序 + 当前节点tabIndex
        String expectedOrderedPath =
            String.format("0000%d,0000%d,0000%d", org123.getTabIndex(), dept123.getTabIndex(), position.getTabIndex());
        assertEquals(expectedOrderedPath, position.getOrderedPath());
    }

    @Test
    void testUpdateCapacityLessThanCurrentHeadCountShouldThrowException() {
        // 设置初始容量为1，并且有1个人员
        position.setCapacity(1);

        // 准备超过容量的人员列表
        Y9Person person1 = new Y9Person();
        person1.setId("person1");
        person1.setName("张三");
        Y9Person person2 = new Y9Person();
        person2.setId("person2");
        person2.setName("李四");
        List<Y9Person> persons = Arrays.asList(person1, person2); // 2个人

        // 准备职位模型，尝试将容量设置为小于当前人员数
        Position positionModel = new Position();
        positionModel.setCapacity(1); // 容量仍为1，但有2个人

        Y9Job job = new Y9Job();
        job.setId("job123");
        job.setName("高级工程师");

        List<Y9OrgBase> ancestors = Arrays.asList(dept123, org123);

        // 验证当容量小于当前人员数时抛出异常
        assertThrows(net.risesoft.y9.exception.Y9BusinessException.class, () -> {
            position.update(positionModel, job, "#jobName", dept123, ancestors, persons);
        });
    }

    @Test
    void testChangeJob() {
        Y9Job newJob = new Y9Job();
        newJob.setId("newJob123");
        newJob.setName("项目经理");

        Y9Department tempDept = new Y9Department();
        tempDept.setDn("ou=临时部门,o=公司");
        tempDept.setId(Y9IdGenerator.genId());
        tempDept.setParentId("org123");
        tempDept.setGuidPath("org123," + tempDept.getId()); // 部门的guidPath是"组织机构ID,部门ID"
        tempDept.setName("临时部门");
        tempDept.setTabIndex(5); // 设置tabIndex

        Y9Organization tempOrg = new Y9Organization();
        tempOrg.setId("org123");
        tempOrg.setName("公司");
        tempOrg.setDn("o=公司");
        tempOrg.setGuidPath("org123");
        tempOrg.setTabIndex(1); // 设置tabIndex

        Y9Person person1 = new Y9Person();
        person1.setId("person1");
        person1.setName("张三");
        List<Y9Person> persons = Arrays.asList(person1);

        position.setJobId("oldJob123");
        position.setJobName("软件工程师");

        // 更改职位
        position.changeJob(newJob, "#jobName", tempDept, persons);

        assertEquals("项目经理", position.getJobName());
        // 注意：这里name不会改变，因为changeJob方法只改变了JobName和部分属性
        assertEquals("项目经理", position.getName());
    }

    @Test
    void testChangeName() {
        Y9Job job = new Y9Job();
        job.setName("技术总监");

        Y9Department tempDept = new Y9Department();
        tempDept.setDn("ou=临时部门,o=公司");
        tempDept.setId(Y9IdGenerator.genId());
        tempDept.setParentId("org123");
        tempDept.setGuidPath("org123," + tempDept.getId()); // 部门的guidPath是"组织机构ID,部门ID"
        tempDept.setName("临时部门");
        tempDept.setTabIndex(5); // 设置tabIndex

        Y9Organization tempOrg = new Y9Organization();
        tempOrg.setId("org123");
        tempOrg.setName("公司");
        tempOrg.setDn("o=公司");
        tempOrg.setGuidPath("org123");
        tempOrg.setTabIndex(1); // 设置tabIndex

        Y9Person person1 = new Y9Person();
        person1.setName("王五");
        List<Y9Person> persons = Arrays.asList(person1);

        // 更改名称
        position.changeName("#jobName + ' - ' + #personNames", job, tempDept, persons);

        assertEquals("技术总监 - 王五", position.getName());
        assertEquals("cn=技术总监 - 王五,ou=临时部门,o=公司", position.getDn());
    }

    @Test
    void testBuildNameWithEmptyPersonList() {
        // 测试buildName私有方法的效果通过changeName方法间接测试
        Y9Job job = new Y9Job();
        job.setName("技术总监");

        Y9Department tempDept = new Y9Department();
        tempDept.setDn("ou=临时部门,o=公司");
        tempDept.setId(Y9IdGenerator.genId());
        tempDept.setParentId("org123");
        tempDept.setGuidPath("org123," + tempDept.getId()); // 部门的guidPath是"组织机构ID,部门ID"
        tempDept.setName("临时部门");
        tempDept.setTabIndex(5); // 设置tabIndex

        Y9Organization tempOrg = new Y9Organization();
        tempOrg.setId("org123");
        tempOrg.setName("公司");
        tempOrg.setDn("o=公司");
        tempOrg.setGuidPath("org123");
        tempOrg.setTabIndex(1); // 设置tabIndex

        List<Y9Person> emptyPersons = Collections.emptyList();

        // 更改名称，当没有人时应显示"空缺"
        position.changeName("#jobName + '（' + #personNames + '）'", job, tempDept, emptyPersons);

        assertEquals("技术总监（空缺）", position.getName());
    }

    @Test
    void testChangeTabIndex() {
        List<Y9OrgBase> ancestors = Arrays.asList(dept123, org123);

        position.changeTabIndex(10, ancestors);

        assertEquals(Integer.valueOf(10), position.getTabIndex());
        assertNotNull(position.getOrderedPath());
        assertEquals("00001,00002,00010", position.getOrderedPath()); // org tabIndex=1, dept tabIndex=2, pos
                                                                      // tabIndex=10
    }

    @Test
    void testChangeProperties() {
        String newProperties = "{\"color\":\"blue\",\"icon\":\"user\"}";

        position.changeProperties(newProperties);

        assertEquals(newProperties, position.getProperties());
    }

    @Test
    void testInitialValues() {
        // 验证默认值
        assertEquals(Integer.valueOf(1), position.getCapacity());
        assertEquals(Integer.valueOf(0), position.getHeadCount());
        assertEquals(OrgTypeEnum.POSITION, position.getOrgType());
    }
}