package IR;

import IR.Types.PointerType;

public class GlobalVariable extends User{
    Module parent;

    public Value getInitializer() {
        return initializer;
    }

    Value initializer;
    public GlobalVariable(String name, Type type,Module parent) {
        super(name, new PointerType(type),ValueType.GlobalVariableVal);
        this.parent=parent;
    }

    public void setInitializer(Value initializer) {
        this.initializer = initializer;
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitGlobalVariable(this);
    }

    @Override
    public String toString() {
        return "@"+getName();
    }
}
