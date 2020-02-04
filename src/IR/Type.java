package IR;

import IR.Types.*;

public class Type extends Value {
    public Type(String name, TypeID id) {
        super(name,TheTypeType, ValueType.TypeVal);
        this.id=id;
    }

    public enum TypeID {
        Int64,Int1,Int8,Typetype,LabelType,FunctionType,StructType,PointerType,ArrayType,VoidType
    }
    public static IntType TheInt64 =new IntType("int64",TypeID.Int64);
    public static IntType TheInt8=new IntType("int8", TypeID.Int8);

    public TypeID getId() {
        return id;
    }

    public static IntType TheInt1=new IntType("int1", TypeID.Int1);
    public static TypeType TheTypeType=new TypeType();
    public static LabelType TheLabelType=new LabelType("label");
    public static VoidType theVoidType=new VoidType("void");
    private TypeID id;

    @Override
    public boolean equals(Object obj) {
        assert obj instanceof Type;
        return id==((Type) obj).id;
    }
    public boolean isNull(){
        return false;
    }
    @Override
    public String toString() {
        return super.toString();
    }
    public static Value getNull(Type type){
        if (type.equals(TheInt1)) {
            return new ConstantBool(false);
        } else if (type.equals(TheInt64)) {
            return new ConstantInt(0);
        } else {
            return new ConstantNull();
        }
    }
}
