package Riscv;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//only used to store global
public class LUI extends MachineInstruction{
    Register rt;
    GlobalVar symbol;

    public GlobalVar getSymbol() {
        return symbol;
    }

    public Register getRt() {
        return rt;
    }

    public LUI(Register rt, GlobalVar symbol){
        this.rt=rt;
        this.symbol=symbol;
    }
    public void accept(Visitor visitor) {
        visitor.visitLUI(this);
    }

    @Override
    public Set<VirtualRegister> getDef() {
        return Stream.of(((VirtualRegister) rt)).collect(Collectors.toSet());
    }

    @Override
    public void color() {
        this.rt= ((VirtualRegister) rt).color;
    }
}
