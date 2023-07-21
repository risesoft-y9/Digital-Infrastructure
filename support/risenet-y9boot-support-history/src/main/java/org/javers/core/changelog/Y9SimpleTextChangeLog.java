package org.javers.core.changelog;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;

/**
 * Sample text changeLog, renders text log like that:
 * 
 * <pre>
 * commit 3.0, author:another author, 2014-12-06 13:22:51
 *   changed object: org.javers.core.model.DummyUser/bob
 *     value changed on 'sex' property: 'null' -> 'FEMALE'
 *     set changed on 'stringSet' property: [removed:'groovy', added:'java', added:'scala']
 *     list changed on 'integerList' property: [(0).added:'22', (1).added:'23']
 * commit 2.0, author:some author, 2014-12-06 13:22:51
 *     value changed on 'age' property: '0' -> '18'
 *     value changed on 'surname' property: 'Dijk' -> 'van Dijk'
 *     reference changed on 'supervisor' property: 'null' -> 'org.javers.core.model.DummyUser/New Supervisor'
 * </pre>
 *
 * @author bartosz walacik dzj
 */
public class Y9SimpleTextChangeLog extends AbstractTextChangeLog {

    public Y9SimpleTextChangeLog() {}

    @Override
    public void onAffectedObject(GlobalId globalId) {
        appendln("  对象： " + globalId.value());
    }

    @Override
    public void onArrayChange(ArrayChange arrayChange) {
        appendln("    数组属性 '" + arrayChange.getPropertyName() + "': " + arrayChange.getChanges());
    }

    @Override
    public void onCommit(CommitMetadata commitMetadata) {
        appendln("提交序号 " + commitMetadata.getId() + ", 提交者： " + commitMetadata.getAuthor() + ", " + PrettyValuePrinter.getDefault().format(commitMetadata.getCommitDate()));
    }

    @Override
    public void onListChange(ListChange listChange) {
        appendln("    列表属性 '" + listChange.getPropertyName() + "': " + listChange.getChanges());
    }

    @Override
    public void onMapChange(MapChange mapChange) {
        appendln("    map属性 '" + mapChange.getPropertyName() + "': " + mapChange.getEntryChanges());
    }

    @Override
    public void onNewObject(NewObject newObject) {
        appendln("    新对象: " + newObject.getAffectedGlobalId());
    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {
        appendln("    删除的对象: '" + objectRemoved.getAffectedGlobalId());
    }

    @Override
    public void onReferenceChange(ReferenceChange referenceChange) {
        appendln("    引用属性  '" + referenceChange.getPropertyName() + "': '" + referenceChange.getLeft() + "' -> '" + referenceChange.getRight() + "'");
    }

    @Override
    public void onSetChange(SetChange setChange) {
        appendln("    集合属性 '" + setChange.getPropertyName() + "': " + setChange.getChanges());
    }

    @Override
    public void onValueChange(ValueChange valueChange) {
        appendln("    值属性 '" + valueChange.getPropertyName() + "': '" + PrettyValuePrinter.getDefault().format(valueChange.getLeft()) + "' -> '" + PrettyValuePrinter.getDefault().format(valueChange.getRight()) + "'");
    }

}
