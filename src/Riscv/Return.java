package Riscv;

public class Return extends MachineInstruction{
    @Override
    public void accept(Visitor visitor) {
        visitor.visitReturn(this);
    }
}
