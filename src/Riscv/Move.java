package Riscv;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Move extends MachineInstruction {
    Register rs;
    Register rd;

    public Register getRs() {
        return rs;
    }

    public Register getRd() {
        return rd;
    }

    public Move(Register rs, Register rd) {
        this.rs = rs;
        this.rd = rd;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitMove(this);
    }

    @Override
    public Set<VirtualRegister> getUse() {
        return Stream.of((VirtualRegister)rs).collect(Collectors.toSet());
    }

    @Override
    public Set<VirtualRegister> getDef() {
        return Stream.of((VirtualRegister)rd).collect(Collectors.toSet());
    }

    public void setRs(Register rs) {
        this.rs = rs;
    }

    @Override
    public void color() {
        rs= ((VirtualRegister) rs).color;
        rd= ((VirtualRegister) rd).color;
    }
}
