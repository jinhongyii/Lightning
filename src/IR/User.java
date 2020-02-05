package IR;

import java.util.ArrayList;

public class User extends  Value{
    //must delete the use before delete element of operands
    protected ArrayList<Use> operands=new ArrayList<>();


    public ArrayList<Use> getOperands() {
        return operands;
    }

    public User(String name, Type type, ValueType valueType) {
        super(name, type, valueType);
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return null;
    }
}
