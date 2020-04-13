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
    public Store(  MachineOperand ptr, Register src) {
        this.isGlobal = ptr instanceof GlobalVar;
        this.size = ptr.getSize();
        this.ptr = ptr;
        this.src = src;
        if(isGlobal) {
            helperReg = new VirtualRegister("helper");
        }
    }
    public Store(  MachineOperand ptr, Register src,int size) {
        this.isGlobal = ptr instanceof GlobalVar;
        this.size =size;
        this.ptr = ptr;
        this.src = src;
        if(isGlobal) {
            helperReg = new VirtualRegister("helper");
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
        return uses;
    }

    @Override
    public Set<VirtualRegister> getDef() {
        if (helperReg != null) {
            return Stream.of(((VirtualRegister) helperReg)).collect(Collectors.toSet());
        } else {
            return super.getDef();
        }
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
}
