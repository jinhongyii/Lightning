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
    public String name;
    public HashSet<VirtualRegister> liveOut=new HashSet<>();
    public HashSet<VirtualRegister> liveIn=new HashSet<>();
    public HashSet<VirtualRegister> gen=new HashSet<>();
    public HashSet<VirtualRegister> kill=new HashSet<>();


    public String getName() {
        return name;
    }
    public MachineBasicBlock(BasicBlock bb){
        this.basicBlock=bb;
        this.name=bb.getName();
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
        if ( inst instanceof Jump) {
            sucs.add(((Jump) inst).getTarget());
        }
        if (tail!=head) {
            inst=tail.prev;
            if (inst instanceof Branch) {
                sucs.add(((Branch) inst).getTarget());
            }
        }
        return sucs;
    }

    public BasicBlock getIRBasicBlock() {
        return basicBlock;
    }
}
