package Riscv;

import IR.Function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class MachineFunction {
    private  String name;
    boolean externalLinkage;
    private LinkedList<MachineBasicBlock> basicBlocks=new LinkedList<>();
    MachineBasicBlock head;
    int stackSize;
    int argNum;
    Function IRfunction;
    public String getName() {
        return name;
    }

    public LinkedList<MachineBasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public boolean isExternalLinkage() {
        return externalLinkage;
    }

    public MachineFunction(String name, boolean isExternalLinkage, int argNum,Function IRfunction) {
        this.name=name;
        this.externalLinkage=isExternalLinkage;
        this.argNum=argNum;
        this.IRfunction=IRfunction;
    }
    public void addBB(MachineBasicBlock basicBlock){
        this.basicBlocks.addLast(basicBlock);
    }


    @Override
    public String toString() {
        return name;
    }

    public Function getIRfunction() {
        return IRfunction;
    }

    public int getStackSize() {
        return stackSize;
    }

    public int getRealStackSize(){
        return getStackSize()*4+(16-getStackSize()*4%16);
    }

    public void cleanJump(){
        head=basicBlocks.getFirst();
        MachineBasicBlock tail = null;
        HashSet<MachineBasicBlock> startBB=new HashSet<>();
        for (var bb : basicBlocks) {
            if (bb.getPrev() == null) {
                startBB.add(bb);
            }
        }
        for(var start:basicBlocks) {
            if(startBB.contains(start)) {
                if (tail != null) {
                    tail.setNextBasicBlock(start);
                }
                for (var bb = start; bb != null; bb = bb.getNext()) {
                    tail=bb;
                    if (bb.tail instanceof Jump && ((Jump) bb.tail).getTarget() == bb.getNext()) {
                        bb.tail.delete();
                    }
                }
            }
        }
        for (var bb : getBasicBlocks()) {
            for (var suc : bb.getSuccessor()) {
                suc.merged=false;
            }
        }
    }

    public MachineBasicBlock getHead() {
        return head;
    }
}
