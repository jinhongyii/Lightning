package Riscv;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LA extends MachineInstruction{
    Register rd;
    GlobalVar symbol;

    public LA(Register rd, GlobalVar symbol) {
        this.rd = rd;
        this.symbol = symbol;
    }

    public Register getRd() {
        return rd;
    }

    public GlobalVar getSymbol() {
        return symbol;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitLA(this);
    }

    @Override
    public Set<VirtualRegister> getDef() {
        return Stream.of((VirtualRegister)rd).collect(Collectors.toSet());
    }

    @Override
    public void color() {
        rd= ((VirtualRegister) rd).color;
    }
}
