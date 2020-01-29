package semantic;

import java.util.ArrayList;

public class FuncEntry extends NameEntry{
    public ArrayList<SemanticType> getParams() {
        return params;
    }

    public SemanticType getReturnType() {
        return returnType;
    }

    SemanticType returnType;
    ArrayList<SemanticType> params=new ArrayList<>();

    public FuncEntry(SemanticType returnType){
        super(kind.funcEntry);
        this.returnType=returnType;
    }
    public FuncEntry(SemanticType returnType,ArrayList<SemanticType> params) {
        super(kind.funcEntry);
        this.returnType=returnType;
        this.params=params;
    }
    public void addParam(SemanticType type){
        this.params.add(type);
    }
}
