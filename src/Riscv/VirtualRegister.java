package Riscv;

import java.util.HashSet;

public class VirtualRegister extends Register{
    String name;
    private static int cnt=0;
    public VirtualRegister(String name) {
        this.name = name+ "_"+cnt;
        cnt++;
    }

    @Override
    public String toString() {
        return name;
    }
}
