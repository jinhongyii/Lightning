package Riscv;

public class Call extends MachineInstruction {
    MachineFunction function;

    public MachineFunction getFunction() {
        return function;
    }

    public Call(MachineFunction function) {
        this.function = function;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitCall(this);
    }
}
