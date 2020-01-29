package IR;

import java.util.ArrayList;

public class User extends  Value{
    protected ArrayList<Use> operands=new ArrayList<>();


    public User(String name, Type type, ValueType valueType) {
        super(name, type, valueType);
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return null;
    }
}
