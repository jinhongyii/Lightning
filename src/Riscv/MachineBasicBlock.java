package Riscv;

import IR.BasicBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

public class MachineBasicBlock {
    MachineInstruction head;
    MachineInstruction tail;
    BasicBlock basicBlock;
    MachineBasicBlock next;
    MachineBasicBlock prev;
    public boolean merged=true;
    public String name;
    public HashSet<VirtualRegister> liveOut=new HashSet<>();
    public HashSet<VirtualRegister> liveIn=new HashSet<>();
    public HashSet<VirtualRegister> gen=new HashSet<>();
    public HashSet<VirtualRegister> kill=new HashSet<>();

    static  int cnt=0;
    public String getName() {
        return name;
    }
    public MachineBasicBlock(BasicBlock bb){
        this.basicBlock=bb;
        this.name=bb.getName()+"_"+cnt;
        cnt++;
    }
    public void addInst(MachineInstruction inst){
        if (tail == null) {
            head=tail=inst;
            inst.setPrev(null);
            inst.setNext(null);
        }else {
            tail.setNextInstruction(inst);
            inst.setNext(null);
            tail = inst;
        }
        inst.parent=this;
    }

    public MachineInstruction getHead() {
        return head;
    }

    public MachineInstruction getTail() {
        return tail;
    }

    @Override
    public String toString() {
        return name;
    }
    public ArrayList<MachineBasicBlock> getSuccessor(){
        var sucs=new ArrayList<MachineBasicBlock>();

        var inst=tail;
        if (inst instanceof Jump) {
            sucs.add(((Jump) inst).getTarget());
            if (tail != head) {
                inst = tail.prev;
                if (inst instanceof Branch) {
                    sucs.add(((Branch) inst).getTarget());
                }
            }
        } else if(inst instanceof Branch){
            sucs.add(((Branch) inst).getTarget());
        }
        return sucs;
    }

    public BasicBlock getIRBasicBlock() {
        return basicBlock;
    }

    public void setNextBasicBlock(MachineBasicBlock basicBlock){
        this.next=basicBlock;
        basicBlock.prev=this;
    }

    public MachineBasicBlock getNext() {
        return next;
    }

    public MachineBasicBlock getPrev() {
        return prev;
    }

    public void flipTerminator(){
        var sucs=getSuccessor();
        assert getSuccessor().size()==2;
        var jmpTarget=sucs.get(0);
        var branchTarget=sucs.get(1);
        ((Branch) tail.prev).flip(jmpTarget);
        ((Jump) tail).setTarget(branchTarget);
    }
    public void addInstBeforeTerminator(MachineInstruction inst){
        if (!(tail instanceof Jump) && !(tail instanceof Branch)) {
            addInst(inst);
        }else {
            if (tail.prev != null && tail.prev instanceof Branch) {
                tail.prev.addInstBefore(inst);
            } else {
                tail.addInstBefore(inst);
            }
        }
    }
}
