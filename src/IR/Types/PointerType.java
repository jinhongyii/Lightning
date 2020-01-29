package IR.Types;

import IR.Type;
import IR.Value;
import org.hamcrest.core.IsNull;

public class PointerType extends CompositeType{
    Type ptrType;

    public Type getPtrType() {
        return ptrType;
    }

    public PointerType(Type ptrType) {
        super("",TypeID.PointerType);
        this.ptrType=ptrType;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PointerType && ptrType.equals(((PointerType) obj).ptrType);
    }

    @Override
    public Type getInnerType(Value idx) {
        return ptrType;
    }

    @Override
    public String toString() {
        return ptrType.toString()+"*";
    }

    @Override
    public boolean isNull() {
        return ptrType==null;
    }
}
