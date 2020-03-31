package Riscv;

public class Load extends MachineInstruction{
    public boolean isGlobal;
    public int size;
    public MachineOperand src;
    public Register rd;

    public boolean isGlobal() {
        return isGlobal;
    }

    public int getSize() {
        return size;
    }

    public MachineOperand getSrc() {
        return src;
    }

    public Register getRd() {
        return rd;
    }

    public Load(MachineOperand src, Register rd) {
        this.isGlobal = src instanceof GlobalVar;
        this.size = src.getSize();
        this.src = src;
        this.rd = rd;
    }
    public Load(MachineOperand src, Register rd,int size) {
        this.isGlobal = src instanceof GlobalVar;
        this.size = size;
        this.src = src;
        this.rd = rd;
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visitLoad(this);
    }
}
