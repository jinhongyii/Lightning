package Riscv;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return Stream.of((VirtualRegister)rs,(VirtualRegister)rt).collect(Collectors.toSet());
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
