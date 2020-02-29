package optim.dsa;

import IR.Type;
import IR.Types.StructType;
import IR.Value;

import java.util.ArrayList;
import java.util.HashSet;

public class DSNode {
    Type type;
    int flag;
    DSNode forwardNode;
    HashSet<Value> globalValue=new HashSet<>();
    ArrayList<DSHandle> outGoingEdge=new ArrayList<>();
    DSGraph parent;
    public DSHandle getOutEdge(int field){
        var edge=outGoingEdge.get(field);
        return edge;
    }
    public DSHandle getNonNullOutEdge(int field){
        var edge=outGoingEdge.get(field);
        if (edge.getNode() == null) {
            edge.setNode(new DSNode(parent));
        }
        return edge;
    }
    DSNode(DSNode other,DSGraph parent) {
        this.parent=parent;
        parent.nodes.add(this);
        this.type=other.type;
        this.flag=other.flag;
        this.globalValue.addAll(other.globalValue);
        for (var outEdge : other.outGoingEdge) {
            this.outGoingEdge.add(new DSHandle(outEdge));
        }
    }
    DSNode(DSGraph parent){
        type=Type.theVoidType;
        outGoingEdge.add(new DSHandle());
        this.parent=parent;
        parent.nodes.add(this);
    }
    DSNode(Type type,DSGraph parent){
        this.type=type;
        if (type instanceof StructType) {
            for (int i = 0; i < ((StructType) type).getRecordTypes().size(); i++) {
                outGoingEdge.add(new DSHandle());
            }
        } else {
            outGoingEdge.add(new DSHandle());
        }
        this.parent=parent;
        parent.nodes.add(this);
    }
    boolean isHeap() {
        return (flag & (1)) != 0;
    }

    boolean isStack() {
        return (flag & (1 << 1)) != 0;
    }

    boolean isGlobal() {
        return (flag & (1 << 2)) != 0;
    }

    boolean isUnknown() {
        return (flag & (1 << 3)) != 0;
    }

    boolean isMod() {
        return (flag & (1 << 4)) != 0;
    }

    boolean isRef() {
        return (flag & (1 << 5)) != 0;
    }

    boolean isInComplete() {
        return (flag & (1 << 6)) != 0;
    }
    boolean isComplete(){
        return !isInComplete();
    }
    boolean isCollapsed() {
        return (flag & (1 << 7)) != 0;
    }

    boolean isArray() {
        return (flag & (1 << 8)) != 0;
    }

    boolean isDeleted(){
        return (flag & (1 << 9)) != 0;
    }

    void setHeap() {
        flag |= 1;
    }

    void setStack() {
        flag |= (1 << 1);
    }

    void setGlobal() {
        flag |= (1 << 2);
    }

    void setUnknown() {
        flag |= (1 << 3);
    }

    void setMod() {
        flag |= (1 << 4);
    }

    void setRef() {
        flag |= (1 << 5);
    }

    void setInComplete() {
        flag |= (1 << 6);
    }

    void setCollapsed() {
        flag |= (1 << 7);
    }

    void setArray() {
        flag |= (1 << 8);
    }

    void setDeleted(){
        flag |= (1 << 9);
    }
    void collapse(){
        DSHandle theOnlyCell=getOutEdge(0);
        for (int i=1;i<outGoingEdge.size();i++) {
            theOnlyCell= DSHandle.mergeCells(theOnlyCell,getOutEdge(i));
        }
        outGoingEdge.clear();
        outGoingEdge.add(theOnlyCell);
        setCollapsed();
        setArray();
        type=Type.theVoidType;
    }

    void setType(Type type){
        assert this.type.equals(Type.theVoidType);
        this.type=type;
        if (type instanceof StructType) {
            for (int i = 1; i < ((StructType) type).getRecordTypes().size(); i++) {
                outGoingEdge.add(new DSHandle());
            }
        }
    }
}
