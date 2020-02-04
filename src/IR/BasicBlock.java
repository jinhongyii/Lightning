package IR;

import IR.instructions.BranchInst;
import IR.instructions.PhiNode;
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
        }else {
            inst.delete();;
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
        for (var use =use_head;use!=null;use=use.next) {
            if(((Instruction)use.user).isTerminator()) {
                bbs.add(((Instruction) use.user).parent);
            }
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
    public void delete(){
        if (this.prev != null) {
            this.prev.next = this.next;
        } else {
            parent.head=this.next;
        }
        if (this.next != null) {
            this.next.prev = this.prev;
        } else {
            parent.tail=this.prev;
        }
        for (var suc : getSuccessors()) {
            suc.notifyMissingPredecessor(this);
        }
        for (var use = use_head; use != null; use = use.next) {
            use.delete();
        }
        for (var inst = head; inst != null; inst = inst.next) {
            inst.transferUses(Type.getNull(inst.getType()));
            inst.delete();
        }

    }
    public void notifyMissingPredecessor(BasicBlock pred){
        var preds=getPredecessors();
        int predNum=preds.size();
        if (predNum == 2) {
            BasicBlock bbKept=null;
            if (preds.get(0) == pred) {
                bbKept=preds.get(1);
            }else if(preds.get(1)==pred){
                bbKept=preds.get(0);
            }
            if (bbKept == this) {
                predNum++;
            }
        }
        if (predNum <= 2) {
            while (head instanceof PhiNode) {
                ((PhiNode) head).removeIncoming(pred);
                if (predNum == 2) {
                    head.transferUses(((PhiNode) head).getValue(0));
                    head.delete();
                }
            }
        } else {
            for (var phi = head; phi instanceof PhiNode; phi = phi.next) {
                ((PhiNode) phi).removeIncoming(pred);
            }
        }
    }
}
