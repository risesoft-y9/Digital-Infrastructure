package net.risesoft;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
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
        Y9Organization organization1 = Y9Organization.builder().id(organization1Id).name("组织1").tabIndex(0).build();
        Y9Organization organization2 = Y9Organization.builder().id(organization2Id).name("组织2").tabIndex(1).build();

        List<String> correctIdList = Arrays.asList(organization1Id, organization2Id);
        List<String> sortedIdList =
            Stream.of(organization1, organization2).sorted().map(Y9OrgBase::getId).collect(Collectors.toList());
        Assertions.assertEquals(correctIdList, sortedIdList);

        // 测试其他组织节点排序
        Y9Department organization1Department1 = Y9Department.builder().id(organization1Department1Id)
            .parentId(organization1Id).name("组织1部门1").tabIndex(4).build();
        Y9Person organization1Department1Person =
            Y9Person.builder().id(organization1PersonId).parentId(organization1Id).name("组织1人员").tabIndex(3).build();
        Y9Position organization1Department1Position = Y9Position.builder().id(organization1PositionId)
            .parentId(organization1Id).name("组织1岗位").tabIndex(2).build();
        Y9Group organization1Department1Group =
            Y9Group.builder().id(organization1GroupId).parentId(organization1Id).name("组织1用户组").tabIndex(1).build();

        List<String> correctIdList2 = Arrays.asList(organization1GroupId, organization1PositionId,
            organization1PersonId, organization1Department1Id);
        List<String> sortedIdList2 =
            Stream.of(organization1Department1, organization1Department1Person, organization1Department1Position,
                organization1Department1Group).sorted().map(Y9OrgBase::getId).collect(Collectors.toList());
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
        Y9App system1App1 = Y9App.builder().id(system1App1Id).systemId(system1Id).name("系统1应用1").tabIndex(3).build();
        Y9App system1App2 = Y9App.builder().id(system1App2Id).systemId(system1Id).name("系统1应用2").tabIndex(2).build();
        Y9App system2App1 = Y9App.builder().id(system2App1Id).systemId(system2Id).name("系统2应用1").tabIndex(1).build();
        List<String> correctAppIdList = Arrays.asList(system1App2Id, system1App1Id, system2App1Id);

        List<String> sortedAppIdList = Stream.of(system1App1, system1App2, system2App1).sorted()
            .map(Y9ResourceBase::getId).collect(Collectors.toList());
        Assertions.assertEquals(correctAppIdList, sortedAppIdList);

        // 测试菜单排序
        Y9Menu system1App1Menu1 = Y9Menu.builder().id(system1App1Menu1Id).parentId(system1App1Id).systemId(system1Id)
            .name("系统1应用1菜单1").tabIndex(2).build();
        Y9Menu system1App1Menu2 = Y9Menu.builder().id(system1App1Menu2Id).parentId(system1App1Id).systemId(system1Id)
            .name("系统1应用1菜单2").tabIndex(1).build();

        List<String> correctMenuIdList = Arrays.asList(system1App1Menu2Id, system1App1Menu1Id);
        List<String> sortedMenuIdList = Stream.of(system1App1Menu1, system1App1Menu2).sorted()
            .map(Y9ResourceBase::getId).collect(Collectors.toList());
        Assertions.assertEquals(correctMenuIdList, sortedMenuIdList);

        // 测试操作排序
        Y9Operation system1App1Menu1Operation1 = Y9Operation.builder().id(system1App1Menu1Operation1Id)
            .parentId(system1App1Menu1Id).systemId(system1Id).name("系统1应用1菜单1操作1").tabIndex(0).build();
        Y9Operation system1App1Menu1Operation2 = Y9Operation.builder().id(system1App1Menu1Operation2Id)
            .parentId(system1App1Menu1Id).systemId(system1Id).name("系统1应用1菜单1操作2").tabIndex(1).build();

        List<String> correctOperationIdList = Arrays.asList(system1App1Menu1Operation1Id, system1App1Menu1Operation2Id);
        List<String> sortedOperationIdList = Stream.of(system1App1Menu1Operation1, system1App1Menu1Operation2).sorted()
            .map(Y9ResourceBase::getId).collect(Collectors.toList());
        Assertions.assertEquals(correctOperationIdList, sortedOperationIdList);
    }
}
