package IR;

import IR.Types.PointerType;

public class GlobalVariable extends User{
    Module parent;
    Value initializer;
    public GlobalVariable(String name, Type type,Value initializer,Module parent) {
        super(name, new PointerType(type),ValueType.GlobalVariableVal);
        this.initializer=initializer;
        this.parent=parent;
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitGlobalVariable(this);
    }

    @Override
    public String toString() {
        return (((PointerType)getType()).getPtrType().equals(Type.TheTypeType)?"%":"@")+getName();
    }
}
