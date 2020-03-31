package Riscv;

public class GlobalVar extends MachineOperand {
    boolean isString;
    String name;
    String str;
    int val;
    int size;

    public boolean isString() {
        return isString;
    }

    public String getName() {
        return name;
    }

    public String getStr() {
        var tmp=str.replace("\\", "\\\\");
        tmp=tmp.replace("\n","\\n");
        tmp=tmp.replace("\0", "");
        tmp=tmp.replace("\t","\\t");
        tmp=tmp.replace("\"", "\\\"");
        return "\""+tmp+"\"";
    }

    public int getVal() {
        return val;
    }

    public GlobalVar(String string, String name){
        isString=true;
        str=string;
        this.name=name;
    }
    public GlobalVar(int val, int size, String name){
        isString=false;
        this.val=val;
        this.size=size;
        this.name=name;
    }

    @Override
    public int getSize() {
        return isString?-1:size;
    }

    @Override
    public String toString() {
        return name;
    }
}
