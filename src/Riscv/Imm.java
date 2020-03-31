package Riscv;

public class Imm extends MachineOperand {
    private int val;

    public Imm(int val) {
        assert val<(1<<12);
        this.val = val;
    }

    @Override
    public String toString() {
        return Integer.toString(val);
    }
}
