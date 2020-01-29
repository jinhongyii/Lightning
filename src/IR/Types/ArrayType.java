package IR.Types;

import IR.Type;
import IR.Value;

public class ArrayType extends CompositeType {
    @Override
    public Type getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    Type type;
    int length;
    public ArrayType(Type type,int length) {
        super("", TypeID.ArrayType);
        this.type=type;
        this.length=length;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ArrayType && type.equals(((ArrayType) obj).type) && length==((ArrayType) obj).length;
    }

    @Override
    public Type getInnerType(Value idx) {
        return type;
    }

    @Override
    public boolean checkIndexValid(Value idx) {
        return idx.getType().equals(TheInt64);
    }

    @Override
    public String toString() {
        return  "["+length+" x "+type.toString()+"]";
    }
}
