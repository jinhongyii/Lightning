package IR.Types;

import IR.Type;
import IR.Value;

public class CompositeType extends Type {

    public CompositeType(String name, TypeID id) {
        super(name, id);
    }
    public Type getInnerType(Value idx){return null;}
    public boolean checkIndexValid(Value idx){return true;}
}
