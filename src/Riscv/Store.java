package Riscv;



import java.util.HashSet;
import java.util.Set;

public class Store extends MachineInstruction {
    public boolean isGlobal;
    public int size;
    public MachineOperand ptr;
    public Register src;

    public Store(  MachineOperand ptr, Register src) {
        this.isGlobal = ptr instanceof GlobalVar;
        this.size = ptr.getSize();
        this.ptr = ptr;
        this.src = src;
    }
    public Store(  MachineOperand ptr, Register src,int size) {
        this.isGlobal = ptr instanceof GlobalVar;
        this.size =size;
        this.ptr = ptr;
        this.src = src;
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visitStore(this);
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public int getSize() {
        return size;
    }

    public MachineOperand getPtr() {
        return ptr;
    }

    public Register getSrc() {
        return src;
    }

    @Override
    public Set<VirtualRegister> getUse() {
        HashSet<VirtualRegister> uses=new HashSet<>();
        if (ptr instanceof VirtualRegister) {
            uses.add((VirtualRegister) ptr);
        }
        uses.add((VirtualRegister)src);
        return uses;
    }
}
