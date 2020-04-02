package Riscv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

public class MachineBasicBlock {
    LinkedList<MachineInstruction> instructions=new LinkedList<>();
    public String name;
    public HashSet<VirtualRegister> liveOut=new HashSet<>();
    public HashSet<VirtualRegister> liveIn=new HashSet<>();
    public HashSet<VirtualRegister> gen=new HashSet<>();
    public HashSet<VirtualRegister> kill=new HashSet<>();
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
    public ArrayList<MachineBasicBlock> getSuccessor(){
        var sucs=new ArrayList<MachineBasicBlock>();
        var iter=instructions.descendingIterator();
        var nextInst=iter.next();
        if ( nextInst instanceof Jump) {
            sucs.add(((Jump) nextInst).getTarget());
        }
        if (iter.hasNext()) {
            nextInst=iter.next();
            if (nextInst instanceof Branch) {
                sucs.add(((Branch) nextInst).getTarget());
            }
        }
        return sucs;
    }
}
