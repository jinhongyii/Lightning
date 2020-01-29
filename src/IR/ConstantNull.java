package IR;

import IR.Types.PointerType;

public class ConstantNull extends Value {
    public ConstantNull() {
        super("", new PointerType(null), ValueType.ConstantVal);
    }

    @Override
    public String toString() {
        return "null";
    }
}
