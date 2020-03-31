package Riscv;

import java.util.LinkedList;

public class MachineBasicBlock {
    LinkedList<MachineInstruction> instructions=new LinkedList<>();
    public String name;

    public LinkedList<MachineInstruction> getInstructions() {
        return instructions;
    }

    public String getName() {
        return name;
    }

    public MachineBasicBlock(String name){
        this.name=name;
    }
    public void addInst(MachineInstruction instruction){
        instructions.addLast(instruction);
    }

    @Override
    public String toString() {
        return name;
    }
}
