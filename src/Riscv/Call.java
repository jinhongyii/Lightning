package Riscv;

import java.util.HashSet;
import java.util.Set;

public class Call extends MachineInstruction {
    MachineFunction function;

    public MachineFunction getFunction() {
        return function;
    }

    public Call(MachineFunction function) {
        this.function = function;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitCall(this);
    }

    @Override
    public Set<VirtualRegister> getUse() {
        HashSet<VirtualRegister> gens=new HashSet<>();
        for (int i = 0; i < Integer.min(function.argNum,8); i++) {
            gens.add(TargetInfo.vPhysicalReg.get(TargetInfo.argumentRegister[i]));
        }
        return gens;
    }

    @Override
    public Set<VirtualRegister> getDef() {
        HashSet<VirtualRegister> kills=new HashSet<>();
        for (var caller_saved : TargetInfo.callerSavedRegister) {
            kills.add(TargetInfo.vPhysicalReg.get(caller_saved));
        }
        return kills;
    }
}
