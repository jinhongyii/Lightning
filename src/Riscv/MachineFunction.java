package Riscv;

import IR.Function;

import java.util.LinkedList;

public class MachineFunction {
    private  String name;
    boolean externalLinkage;
    private LinkedList<MachineBasicBlock> basicBlocks=new LinkedList<>();
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
}
