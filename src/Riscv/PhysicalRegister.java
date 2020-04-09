package Riscv;

import java.util.Objects;

public class PhysicalRegister extends Register {
    String regName;

    public PhysicalRegister(String regName) {
        this.regName = regName;
    }

    @Override
    public boolean equals(Object obj) {
        return this.regName.equals(((PhysicalRegister)obj).regName);
    }

    public String getRegName() {
        return regName;
    }

    @Override
    public String toString() {
        return regName ;
    }

    @Override
    public int hashCode() {
        return regName.hashCode();
    }
}
