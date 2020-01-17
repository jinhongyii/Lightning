package ast;

import java.util.ArrayList;

public class Type {
    String typename;
    int dims;

    public Type(String typename,int dims){
        this.typename=typename;
        this.dims=dims;
    }

    @Override
    public String toString() {
        return typename+"[]".repeat(dims);
    }
}
