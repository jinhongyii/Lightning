package IR;

public class ConstantBool extends Value{
    boolean val;

    public boolean isVal() {
        return val;
    }

    public ConstantBool(boolean val) {
        super("", Type.TheInt1, ValueType.ConstantVal);
    }

    @Override
    public String toString() {
        return ((Boolean)val).toString();
    }
}
