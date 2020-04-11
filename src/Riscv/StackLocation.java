package Riscv;

public class StackLocation extends MachineOperand {
    int idx;

    public StackLocation(MachineFunction function){
        idx=function.stackSize;
        function.stackSize++;
    }
    public StackLocation(int idx){
        this.idx=idx;
    }
    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public String toString() {
        return 4*idx+"(sp)";
    }
}
