package Riscv;

import java.util.HashSet;

public class VirtualRegister extends Register{
    private static int cnt=0;
    public int degree=0;
    public double spillCost=0;
    public boolean spillTemporary=false;
    public int splitLoads=0;
    public int splitStores=0;
    String name;
    HashSet<VirtualRegister> adjList=new HashSet<>();
    HashSet<Move> moveList=new HashSet<>();
    VirtualRegister alias=null;
    PhysicalRegister color=null;
    HashSet<VirtualRegister> containList=new HashSet<>();
    HashSet<VirtualRegister> splitAround=new HashSet<>();
    private boolean rematerializable=false;
    private int rematerializeVal=0;
    public StackLocation splitAddr;
    public VirtualRegister(String name) {
        this.name = name+ "_"+cnt;
        cnt++;
    }

    @Override
    public String toString() {
        return name;
    }

    public HashSet<VirtualRegister> getAdjList() {
        return adjList;
    }

    public HashSet<Move> getMoveList() {
        return moveList;
    }

    public VirtualRegister getAlias() {
        return alias;
    }

    public void setAlias(VirtualRegister alias) {
        this.alias = alias;
    }

    public PhysicalRegister getColor() {
        return color;
    }

    public void setColor(PhysicalRegister color) {
        this.color = color;
    }

    public double getRealSpillCost(){
        return spillCost/degree;
    }

    public HashSet<VirtualRegister> getContainList() {
        return containList;
    }

    public HashSet<VirtualRegister> getSplitAround() {
        return splitAround;
    }

    public boolean isRematerializable() {
        return rematerializable;
    }

    public int getRematerializeVal() {
        return rematerializeVal;
    }

    public StackLocation getSplitAddr(MachineFunction function) {
        if (splitAddr == null) {
            return splitAddr=new StackLocation(function);
        }
        return splitAddr;
    }
}
