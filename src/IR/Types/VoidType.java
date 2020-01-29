package IR.Types;

import IR.Type;

public class VoidType extends Type {

    public VoidType(String name) {
        super(name, TypeID.VoidType);
    }

    @Override
    public String toString() {
        return "void";
    }
}
