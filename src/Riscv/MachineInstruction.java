package Riscv;

import java.util.HashSet;
import java.util.Set;

public class MachineInstruction {
    public void accept(Visitor visitor){
    }
    public Set<VirtualRegister> getUse(){
        return new HashSet<>();
    }
    public Set<VirtualRegister> getDef(){
        return new HashSet<>();
    }
}
