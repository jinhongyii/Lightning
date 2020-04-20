package Riscv;

public class Jump extends MachineInstruction {
    MachineBasicBlock target;

    public Jump(MachineBasicBlock target) {
        this.target = target;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitJump(this);
    }

    public MachineBasicBlock getTarget() {
        return target;
    }

    public void setTarget(MachineBasicBlock target) {
        this.target = target;
    }
}
