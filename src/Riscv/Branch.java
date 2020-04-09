package Riscv;

import java.util.Set;

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

    @Override
    public Set<VirtualRegister> getUse() {
        return Set.of((VirtualRegister)rs,(VirtualRegister)rt);
    }

    public void setRs(Register rs) {
        this.rs = rs;
    }

    public void setRt(Register rt) {
        this.rt = rt;
    }

    @Override
    public void color() {
        rs= ((VirtualRegister) rs).color;
        rt= ((VirtualRegister) rt).color;
    }
}
