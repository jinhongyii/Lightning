package Riscv;

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
}
