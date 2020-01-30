package IR.Types;

import IR.ConstantInt;
import IR.Type;
import IR.Value;

import java.util.ArrayList;

public class StructType extends CompositeType{
    public ArrayList<Type> getRecordTypes() {
        return recordTypes;
    }

    ArrayList<Type> recordTypes;

    public StructType(String name,ArrayList<Type> recordTypes) {
        super(name,TypeID.StructType);
        this.recordTypes=recordTypes;
    }

    @Override
    public boolean equals(Object obj) {
        return obj==this;
    }

    @Override
    public Type getInnerType(Value idx) {
        int idxnum=  ((ConstantInt)idx).getVal();
        return recordTypes.get(idxnum);
    }

    @Override
    public boolean checkIndexValid(Value idx) {
        if (!(idx instanceof ConstantInt)) {
            return false;
        }
        int idxnum=((ConstantInt) idx).getVal();
        return idxnum<recordTypes.size();
    }

    @Override
    public String toString() {
        return "%"+getName();
    }
    public String getDetailedText(){
        StringBuilder str= new StringBuilder("{");
        boolean flag=false;
        for (var i : recordTypes) {
            str.append(i).append(",");
            flag=true;
        }
        if(flag) {
            str.delete(str.length() - 1, str.length());
        }
        str.append("}");
        return str.toString();
    }
}
