package IR;

import IR.Types.ArrayType;

import java.util.ArrayList;

public class ConstantString extends Value{
    String val;
    public ConstantString(String val) {
        super("",Type.TheInt8, ValueType.ConstantVal);
        this.val=val;
    }

    @Override
    public String toString() {
        return val;
    }
}

