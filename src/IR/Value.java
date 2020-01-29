package IR;

import java.util.LinkedList;
import java.util.List;

public abstract class Value {
    public enum ValueType {
        TypeVal,                // This is an instance of Type
        ConstantVal,            // This is an instance of Constant
        ArgumentVal,            // This is an instance of Argument
        InstructionVal,         // This is an instance of Instruction
        BasicBlockVal,          // This is an instance of BasicBlock
        FunctionVal,            // This is an instance of Function
        GlobalVariableVal,      // This is an instance of GlobalVariable
    }
    private String name;
    private Type type;
    private ValueType valueType;
    List<Use> uses=new LinkedList<>();
    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Value.ValueType getValueType() {
        return valueType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Value(String name, Type type, ValueType valueType) {
        this.name = name;
        this.type = type;
        this.valueType = valueType;
    }

    public Object accept(IRVisitor visitor) {
        return null;
    }

    @Override
    public String toString() {
        return "%"+name;
    }
}
