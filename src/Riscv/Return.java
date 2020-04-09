package Riscv;

import java.util.Set;

public class Return extends MachineInstruction{
    @Override
    public void accept(Visitor visitor) {
        visitor.visitReturn(this);
    }

    @Override
    public Set<VirtualRegister> getUse() {
        return Set.of(TargetInfo.vPhysicalReg.get("ra"));
    }
}
