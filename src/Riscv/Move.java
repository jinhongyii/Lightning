package Riscv;

import java.util.Set;

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
        return Set.of((VirtualRegister)rs);
    }

    @Override
    public Set<VirtualRegister> getDef() {
        return Set.of((VirtualRegister)rd);
    }
}
