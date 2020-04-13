package Riscv;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Return extends MachineInstruction{
    @Override
    public void accept(Visitor visitor) {
        visitor.visitReturn(this);
    }

    @Override
    public Set<VirtualRegister> getUse() {
        return Stream.of(TargetInfo.vPhysicalReg.get("ra")).collect(Collectors.toSet());
    }
}
