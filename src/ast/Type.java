package ast;

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

    public String getTypename() {
        return typename;
    }

    public int getDims() {
        return dims;
    }
}
