package Riscv;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return Stream.of((VirtualRegister)rs1,(VirtualRegister)rs2).collect(Collectors.toSet());
    }

    @Override
    public Set<VirtualRegister> getDef() {
        return Stream.of((VirtualRegister)rd).collect(Collectors.toSet());
    }

    public void setRs1(Register rs1) {
        this.rs1 = rs1;
    }

    public void setRs2(Register rs2) {
        this.rs2 = rs2;
    }

    @Override
    public void color() {
        rs1= ((VirtualRegister) rs1).color;
        rs2= ((VirtualRegister) rs2).color;
        rd= ((VirtualRegister) rd).color;
    }
}
