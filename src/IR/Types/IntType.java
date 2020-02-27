package IR.Types;

import IR.Type;

public class IntType extends Type {
    public IntType(String name, TypeID id) {
        super(name, id);
    }

    @Override
    public String toString() {
        switch (getId()) {
            case Int1:
                return "i1";
            case Int8:
                return "i8";
            case Int32:
                return "i32";
            default:return null;
        }
    }
}
