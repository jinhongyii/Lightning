package IR.Types;

import IR.Type;
import IR.Value;

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
        if (!(obj instanceof PointerType)) {
            return false;
        }
        var objPtrtype=((PointerType) obj).ptrType;
        if (this.ptrType == null) {
            return objPtrtype == null;
        } else {
            return this.ptrType.equals(objPtrtype);
        }
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
