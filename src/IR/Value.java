package IR;

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

//    public List<Use> getUses() {
//        return uses;
//    }

    private Type type;
    private ValueType valueType;
    Use use_head;
    Use use_tail;
//    List<Use> uses=new LinkedList<>();
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

    public Use getUse_head() {
        return use_head;
    }

    public Use getUse_tail() {
        return use_tail;
    }

    //this value is going to be deleted, so its use must be transferred to another value
    public void transferUses(Value value){
        for (var use = use_head; use != null;) {
            var tmp=use.next;
            use.setValue(value);
            use=tmp;
        }
        use_head=null;
        use_tail=null;
    }
}
