package IR;

public class Argument extends  Value{
    Function parent;

    public Argument(String name, Type type,Function parent) {
        super(name, type, ValueType.ArgumentVal);
        this.parent=parent;
    }

    public Function getParent() {
        return parent;
    }
}
