package IR;

import IR.instructions.BranchInst;
import IR.instructions.ReturnInst;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.LinkedList;

public class BasicBlock extends  Value {
    Function parent;
    Instruction head=null;
    Instruction tail=null;
    BasicBlock prev=null;
    BasicBlock next=null;
    public int dfsnum=0;
    public BasicBlock(String name) {
        super(name, Type.TheLabelType,ValueType.BasicBlockVal);

    }
    public void addInst(Instruction inst){
        if (tail == null) {
            head=tail=inst;
        }else if(!(inst.isTerminator() && tail.isTerminator())) {
//            instructionList.add(inst);
            tail.setNextInstruction(inst);
            tail = inst;
        }
        if(parent!=null) {
            parent.symtab.put(inst.getName(), inst);
        }
        inst.parent=this;

    }
    public void addInstToFirst(Instruction inst){
        if(tail==null){
            head=tail=inst;
        }else {
            inst.setNextInstruction(head);
            head = inst;
        }
//            instructionList.addFirst(inst);
        parent.symtab.put(inst.getName(), inst);
        inst.parent = this;

    }
    public void setParent(Function parent) {
        this.parent=parent;
    }
    public Instruction getTerminator(){

        if (tail.isTerminator()) {
            return tail;
        } else {
            return null;
        }
    }
    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitBasicBlock(this);
    }
    public void setNextBB(BasicBlock next){
        next.prev=this;
        this.next=next;
    }
    public Function getParent() {
        return parent;
    }

    public Instruction getHead() {
        return head;
    }

    public Instruction getTail() {
        return tail;
    }

    public BasicBlock getPrev() {
        return prev;
    }

    public BasicBlock getNext() {
        return next;
    }


    public ArrayList<BasicBlock> getPredecessors(){
        ArrayList<BasicBlock> bbs=new ArrayList<>();
        for (var use : uses) {
            bbs.add(((Instruction)use.user).parent);
        }
        return bbs;
    }
    public ArrayList<BasicBlock> getSuccessors(){
        ArrayList<BasicBlock> bbs=new ArrayList<>();
        var terminator=getTerminator();
        if (terminator == null || terminator instanceof ReturnInst) {
            return bbs;
        } else {
            var br=(BranchInst)terminator;
            if (br.isConditional()) {
                bbs.add((BasicBlock) br.operands.get(0).val);
                bbs.add((BasicBlock) br.operands.get(1).val);
            } else {
                bbs.add((BasicBlock) br.operands.get(0).val);
            }
            return bbs;
        }
    }
}
