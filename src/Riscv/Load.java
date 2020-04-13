package Riscv;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    public Set<VirtualRegister> getDef() {
        return Stream.of((VirtualRegister)rd).collect(Collectors.toSet());
    }

    @Override
    public Set<VirtualRegister> getUse() {
        if (src instanceof VirtualRegister) {
            return Stream.of((VirtualRegister) src).collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }

    public void setSrc(MachineOperand src) {
        this.src = src;
    }

    @Override
    public void color() {
        if (src instanceof VirtualRegister) {
            src=((VirtualRegister) src).color;
        }
        rd= ((VirtualRegister) rd).color;
    }
}
