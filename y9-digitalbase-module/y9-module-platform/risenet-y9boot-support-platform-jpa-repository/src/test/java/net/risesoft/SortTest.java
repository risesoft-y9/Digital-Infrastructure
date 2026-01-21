package net.risesoft;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9Group;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Organization;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;

public class SortTest {

    /**
     * 测试组织节点排序 <br>
     * {@link Y9OrgBase#compareTo(Y9OrgBase)}
     */
    @Test
    public void testOrgUnitsSort() {
        String organization1Id = "organization1";
        String organization2Id = "organization2";
        String organization1Department1Id = "organization1Department1";
        String organization1PersonId = "organization1Person";
        String organization1PositionId = "organization1Position";
        String organization1GroupId = "organization1Group";

        // 测试组织机构排序
        Y9Organization organization1 = new Y9Organization();
        organization1.setId(organization1Id);
        organization1.setName("组织1");
        organization1.setTabIndex(0);

        Y9Organization organization2 = new Y9Organization();
        organization2.setId(organization2Id);
        organization2.setName("组织2");
        organization2.setTabIndex(1);

        List<String> correctIdList = Arrays.asList(organization1Id, organization2Id);
        List<String> sortedIdList =
            Stream.of(organization1, organization2).sorted().map(Y9OrgBase::getId).collect(Collectors.toList());
        Assertions.assertEquals(correctIdList, sortedIdList);

        // 测试其他组织节点排序
        Y9Department organization1Department1 = new Y9Department();
        organization1Department1.setId(organization1Department1Id);
        organization1Department1.setParentId(organization1Id);
        organization1Department1.setName("组织1部门1");
        organization1Department1.setTabIndex(4);

        Y9Person organization1Department1Person = new Y9Person();
        organization1Department1Person.setId(organization1PersonId);
        organization1Department1Person.setParentId(organization1Id);
        organization1Department1Person.setName("组织1人员");
        organization1Department1Person.setTabIndex(3);

        Y9Position organization1Department1Position = new Y9Position();
        organization1Department1Position.setId(organization1PositionId);
        organization1Department1Position.setParentId(organization1Id);
        organization1Department1Position.setName("组织1岗位");
        organization1Department1Position.setTabIndex(2);

        Y9Group organization1Department1Group = new Y9Group();
        organization1Department1Group.setId(organization1GroupId);
        organization1Department1Group.setParentId(organization1Id);
        organization1Department1Group.setName("组织1用户组");
        organization1Department1Group.setTabIndex(1);

        List<String> correctIdList2 = Arrays.asList(organization1GroupId, organization1PositionId,
            organization1PersonId, organization1Department1Id);
        List<String> sortedIdList2 =
            Stream
                .of(organization1Department1, organization1Department1Person, organization1Department1Position,
                    organization1Department1Group)
                .sorted()
                .map(Y9OrgBase::getId)
                .collect(Collectors.toList());
        Assertions.assertEquals(correctIdList2, sortedIdList2);
    }

    /**
     * 测试资源排序 <br>
     * {@link Y9ResourceBase#compareTo(Y9ResourceBase)}
     */
    @Test
    public void testResourcesSort() {
        String system1Id = "system1";
        String system2Id = "system2";
        String system2App1Id = "system2App1";

        String system1App1Id = "system1App1";
        String system1App2Id = "system1App2";
        String system1App1Menu1Id = "system1App1Menu1";
        String system1App1Menu2Id = "system1App1Menu2";
        String system1App1Menu1Operation1Id = "system1App1Menu1Operation1";
        String system1App1Menu1Operation2Id = "system1App1Menu1Operation2";

        // 测试应用排序
        Y9App system1App1 = new Y9App();
        system1App1.setId(system1App1Id);
        system1App1.setSystemId(system1Id);
        system1App1.setName("系统1应用1");
        system1App1.setTabIndex(3);

        Y9App system1App2 = new Y9App();
        system1App2.setId(system1App2Id);
        system1App2.setSystemId(system1Id);
        system1App2.setName("系统1应用2");
        system1App2.setTabIndex(2);

        Y9App system2App1 = new Y9App();
        system2App1.setId(system2App1Id);
        system2App1.setSystemId(system2Id);
        system2App1.setName("系统2应用1");
        system2App1.setTabIndex(1);
        List<String> correctAppIdList = Arrays.asList(system1App2Id, system1App1Id, system2App1Id);

        List<String> sortedAppIdList = Stream.of(system1App1, system1App2, system2App1)
            .sorted()
            .map(Y9ResourceBase::getId)
            .collect(Collectors.toList());
        Assertions.assertEquals(correctAppIdList, sortedAppIdList);

        // 测试菜单排序
        Y9Menu system1App1Menu1 = new Y9Menu();
        system1App1Menu1.setId(system1App1Menu1Id);
        system1App1Menu1.setParentId(system1App1Id);
        system1App1Menu1.setSystemId(system1Id);
        system1App1Menu1.setName("系统1应用1菜单1");
        system1App1Menu1.setTabIndex(2);

        Y9Menu system1App1Menu2 = new Y9Menu();
        system1App1Menu2.setId(system1App1Menu2Id);
        system1App1Menu2.setParentId(system1App1Id);
        system1App1Menu2.setSystemId(system1Id);
        system1App1Menu2.setName("系统1应用1菜单2");
        system1App1Menu2.setTabIndex(1);

        List<String> correctMenuIdList = Arrays.asList(system1App1Menu2Id, system1App1Menu1Id);
        List<String> sortedMenuIdList = Stream.of(system1App1Menu1, system1App1Menu2)
            .sorted()
            .map(Y9ResourceBase::getId)
            .collect(Collectors.toList());
        Assertions.assertEquals(correctMenuIdList, sortedMenuIdList);

        // 测试操作排序
        Y9Operation system1App1Menu1Operation1 = new Y9Operation();
        system1App1Menu1Operation1.setId(system1App1Menu1Operation1Id);
        system1App1Menu1Operation1.setParentId(system1App1Menu1Id);
        system1App1Menu1Operation1.setSystemId(system1Id);
        system1App1Menu1Operation1.setName("系统1应用1菜单1操作1");
        system1App1Menu1Operation1.setTabIndex(0);

        Y9Operation system1App1Menu1Operation2 = new Y9Operation();
        system1App1Menu1Operation2.setId(system1App1Menu1Operation2Id);
        system1App1Menu1Operation2.setParentId(system1App1Menu1Id);
        system1App1Menu1Operation2.setSystemId(system1Id);
        system1App1Menu1Operation2.setName("系统1应用1菜单1操作2");
        system1App1Menu1Operation2.setTabIndex(1);

        List<String> correctOperationIdList = Arrays.asList(system1App1Menu1Operation1Id, system1App1Menu1Operation2Id);
        List<String> sortedOperationIdList = Stream.of(system1App1Menu1Operation1, system1App1Menu1Operation2)
            .sorted()
            .map(Y9ResourceBase::getId)
            .collect(Collectors.toList());
        Assertions.assertEquals(correctOperationIdList, sortedOperationIdList);
    }
}