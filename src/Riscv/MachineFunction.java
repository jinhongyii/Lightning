package Riscv;

import java.util.LinkedList;

public class MachineFunction {
    private  String name;
    boolean externalLinkage;
    private LinkedList<MachineBasicBlock> basicBlocks=new LinkedList<>();
    int stackSize;
    int argNum;

    public String getName() {
        return name;
    }

    public LinkedList<MachineBasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public boolean isExternalLinkage() {
        return externalLinkage;
    }

    public MachineFunction(String name, boolean isExternalLinkage, int argNum) {
        this.name=name;
        this.externalLinkage=isExternalLinkage;
        this.argNum=argNum;
    }
    public void addBB(MachineBasicBlock basicBlock){
        this.basicBlocks.addLast(basicBlock);
    }
    public MachineBasicBlock addBB(String name){
        var newBB=new MachineBasicBlock(name);
        this.basicBlocks.addLast(newBB);
        return newBB;
    }

    @Override
    public String toString() {
        return name;
    }
}
