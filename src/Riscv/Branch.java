package Riscv;

public class Branch extends MachineInstruction{
    public enum Opcode{beq,bne,ble,bge,blt,bgt}
    Register rs;
    Register rt;

    public Register getRs() {
        return rs;
    }

    public Register getRt() {
        return rt;
    }

    public MachineBasicBlock getTarget() {
        return target;
    }

    public Opcode getOpcode() {
        return opcode;
    }

    MachineBasicBlock target;
    Opcode opcode;

    public Branch(Register rs, Register rt, Opcode opcode,MachineBasicBlock target) {
        this.rs = rs;
        this.rt = rt;
        this.opcode = opcode;
        this.target=target;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitBranch(this);
    }
}
