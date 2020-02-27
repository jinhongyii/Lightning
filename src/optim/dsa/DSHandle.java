package optim.dsa;

import IR.Type;

import java.util.ArrayList;

public class DSHandle {
    private DSNode node;
    int field;
    public DSHandle(){
        this.node=null;
        this.field=0;
    }
    public DSHandle(DSNode node, int field) {
        this.node = node;
        this.field = field;
    }
    //return true if cell2 is collapsed after this operation
    private static boolean mergeTypeInfo(DSHandle cell1, DSHandle cell2){
        if (cell1.field != cell2.field) {
            cell1.getNode().collapse();
            cell2.getNode().collapse();
            return true;
        }
        return mergeTypeInfo(cell1.getNode(), cell2.getNode());
    }

    public DSNode getNode(){
        if (node == null) {
            return null;
        }
        return node=getNode(node);
    }

    private DSNode getNode(DSNode node) {
        if (node.forwardNode == null) {
            return node;
        }
        return node.forwardNode=getNode(node.forwardNode);
    }
    private static boolean mergeTypeInfo(DSNode node1, DSNode node2) {
        var t1=node1.type;
        var t2=node2.type;
        if (node2.isCollapsed()) {
            node1.collapse();
            return true;
        }

        if (node1.isCollapsed()) {
            node2.collapse();
            return true;
        } else if (node1.type.equals(Type.theVoidType)) {
            return false;
        }
        if (t2.equals( Type.theVoidType)) {
            node2.setType(node1.type);
            return false;
        }
        if (!t1.equals(t2)) {
            node1.collapse();
            node2.collapse();
            return true;
        }
        return false;
    }

    public static DSHandle mergeCells(DSHandle cell1, DSHandle cell2){
        assert cell1!=null && cell2!=null;
        if (cell1.getNode() == null) {
            cell1.node = cell2.getNode();
            return cell2;
        } else if(cell2.getNode()==null){
            cell2.node=cell1.getNode();
            return cell1;
        }
        mergeTypeInfo(cell1,cell2);
        var node1=cell1.getNode();
        var node2=cell2.getNode();
        if(node1!=node2) {
            node2.flag |= node1.flag;
            node2.globalValue.addAll(node1.globalValue);
            assert node1.outGoingEdge.size() == node2.outGoingEdge.size();
            ArrayList<DSHandle> newEdges = new ArrayList<>();
            for (int i = 0; i < node1.outGoingEdge.size(); i++) {
                newEdges.add(mergeCells(node1.getOutEdge(i), node2.getOutEdge(i)));
            }
            node2.outGoingEdge = newEdges;
            node1.forwardNode = node2;
        }
        if (node2.isCollapsed()) {
            return new DSHandle(node2, 0);
        } else {
            return cell2;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DSHandle) {
            return ((DSHandle) obj).getNode()==getNode() && ((DSHandle) obj).field==field;
        }
        return false;
    }
    void setField(int field){
        this.field=field;
    }

    public void setNode(DSNode node) {
        this.node = node;
    }
}
