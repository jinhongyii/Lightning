package IR;

public class ConstantBool extends Value{
    boolean val;

    public boolean isTrue() {
        return val;
    }

    public ConstantBool(boolean val) {
        super("", Type.TheInt1, ValueType.ConstantVal);
        this.val=val;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConstantBool && ((ConstantBool) obj).val==val;
    }

    @Override
    public String toString() {
        return ((Boolean)val).toString();
    }
}
