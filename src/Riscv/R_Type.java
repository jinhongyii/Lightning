package Riscv;

import java.util.Set;

public class R_Type extends MachineInstruction {
    Register rs1;

    Register rs2;
    Register rd;
    public enum Opcode{
        add,sub,sll,slt,xor,sra,or,and,mul,div,rem,sltu
    }
    Opcode opcode;

    public R_Type(Opcode opcode, Register rs1, Register rs2, Register rd) {
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.rd = rd;
        this.opcode = opcode;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitR_type(this);
    }

    public Register getRs1() {
        return rs1;
    }

    public Register getRs2() {
        return rs2;
    }

    public Register getRd() {
        return rd;
    }

    public Opcode getOpcode() {
        return opcode;
    }

    @Override
    public Set<VirtualRegister> getUse() {
        return Set.of((VirtualRegister)rs1,(VirtualRegister)rs2);
    }

    @Override
    public Set<VirtualRegister> getDef() {
        return Set.of((VirtualRegister)rd);
    }
}
