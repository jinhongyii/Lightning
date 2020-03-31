package Riscv;

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
}
