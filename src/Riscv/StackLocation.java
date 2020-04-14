package Riscv;

public class StackLocation extends MachineOperand {
    int idx;
    boolean fromCaller=false;
    MachineFunction function;
    public StackLocation(MachineFunction function){
        this.function=function;
        idx=function.stackSize;
        function.stackSize++;
    }
    public StackLocation(MachineFunction function,int idx,boolean fromCaller){
        this.function=function;
        this.idx=idx;
        this.fromCaller=fromCaller;
    }
    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public String toString() {
        if(fromCaller){
            return (4*idx+function.getRealStackSize())+"(sp)";
        }
        return 4*idx+"(sp)";
    }
}
