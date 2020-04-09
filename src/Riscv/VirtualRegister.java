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

    HashSet<VirtualRegister> adjList=new HashSet<>();
    public int degree=0;
    HashSet<Move> moveList=new HashSet<>();
    VirtualRegister alias=null;
    PhysicalRegister color=null;

    public HashSet<VirtualRegister> getAdjList() {
        return adjList;
    }


    public HashSet<Move> getMoveList() {
        return moveList;
    }

    public VirtualRegister getAlias() {
        return alias;
    }

    public PhysicalRegister getColor() {
        return color;
    }

    public void setColor(PhysicalRegister color) {
        this.color = color;
    }

    public void setAlias(VirtualRegister alias) {
        this.alias = alias;
    }
    public double spillCost=0;
    public double getRealSpillCost(){
        return spillCost/degree;
    }
    public boolean spillTemporary=false;
}
