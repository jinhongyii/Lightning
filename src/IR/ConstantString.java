package IR;

import IR.Types.PointerType;

public class ConstantString extends Value{
    String val;
    public ConstantString(String val) {
        super("",new PointerType(Type.TheInt8), ValueType.ConstantVal);
        this.val=val;
    }

    @Override
    public String toString() {
        var tmp=val.replace("\n","\\0A");
        tmp=tmp.replace("\0", "\\00");
        tmp=tmp.replace("\t","\\09");
        tmp=tmp.replace("\"", "\\22");
        return "c\""+tmp+"\"";
    }
}

