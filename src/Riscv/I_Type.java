package Riscv;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class I_Type extends MachineInstruction {
    public enum Opcode {
        addi,slti,xori,ori,andi,slli,srai,sltiu
    }
    Register rs1;
    Register rd;
    Imm imm;
    Opcode op;
    public I_Type(Opcode opcode,Register rs1,Register rd,Imm imm) {
        this.rs1=rs1;
        this.rd=rd;
        this.op=opcode;
        this.imm=imm;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitI_type(this);
    }

    public Register getRs1() {
        return rs1;
    }

    public Register getRd() {
        return rd;
    }

    public Imm getImm() {
        return imm;
    }

    public Opcode getOp() {
        return op;
    }

    @Override
    public Set<VirtualRegister> getUse() {
        return Stream.of((VirtualRegister)rs1).collect(Collectors.toSet());
    }

    @Override
    public Set<VirtualRegister> getDef() {
        return Stream.of((VirtualRegister)rd).collect(Collectors.toSet());
    }

    public void setRs1(Register rs1) {
        this.rs1 = rs1;
    }

    public void setRd(Register rd) {
        this.rd = rd;
    }

    @Override
    public void color() {
        this.rs1= ((VirtualRegister) rs1).color;
        this.rd= ((VirtualRegister) rd).color;
    }
}
