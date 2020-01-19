package semantic;

public class ArrayType extends SemanticType {
    private SemanticType elementType;
    private int dims;

    public SemanticType getElementType() {
        return elementType;
    }

    public int getDims() {
        return dims;
    }

    public ArrayType(SemanticType elementType, int dims){
        super(kind.array);
        this.elementType=elementType;
        this.dims=dims;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && elementType.equals(((ArrayType)obj).elementType) && dims==((ArrayType)obj).dims;
    }

    @Override
    public boolean canAssignTo(SemanticType other) {
        return other.actual().equals(this);
    }

    @Override
    public boolean looseComparable(SemanticType other) {
        return other.isNullType();
    }
}
