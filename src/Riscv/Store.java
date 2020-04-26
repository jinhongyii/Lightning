package Riscv;



import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Store extends MachineInstruction {
    public boolean isGlobal;
    public int size;
    public MachineOperand ptr;
    public Register src;
    public Register helperReg;
    public int offset=0;
    public Store(  MachineOperand ptr, Register src,int size,Register helperReg) {
        this.isGlobal = ptr instanceof GlobalVar;
        this.size =size;
        this.ptr = ptr;
        this.src = src;
        if(isGlobal) {
            this.helperReg = helperReg;
        }
    }

    public Store(int size, MachineOperand ptr, Register src, int offset,Register helperReg) {
        this.isGlobal = ptr instanceof GlobalVar;
        this.size = size;
        this.ptr = ptr;
        this.src = src;
        this.offset = offset;
        if(isGlobal) {
            this.helperReg = helperReg;
        }
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
        if (helperReg != null) {
            uses.add((VirtualRegister) helperReg);
        }
        return uses;
    }



    public void setPtr(MachineOperand ptr) {
        this.ptr = ptr;
    }

    public void setSrc(Register src) {
        this.src = src;
    }

    @Override
    public void color() {
        if (ptr instanceof VirtualRegister) {
            ptr=((VirtualRegister) ptr).color;
        }
        src= ((VirtualRegister) src).color;
        if (helperReg != null) {
            helperReg= ((VirtualRegister) helperReg).color;
        }
    }

    public int getOffset() {
        return offset;
    }
}
