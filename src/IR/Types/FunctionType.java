package IR.Types;

import IR.Type;

import java.util.ArrayList;

public class FunctionType extends Type {
    Type resultType;

    public Type getResultType() {
        return resultType;
    }

    public ArrayList<Type> getParamTypes() {
        return paramTypes;
    }

    ArrayList<Type> paramTypes;

    public FunctionType(Type resultType,ArrayList<Type>paramTypes) {
        super("",TypeID.FunctionType);
        this.resultType=resultType;
        this.paramTypes=paramTypes;
    }

}
