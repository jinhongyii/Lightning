package semantic;



public class IntType extends SemanticType {
    @Override
    public boolean canAssignTo(SemanticType other) {
        return other.actual().isIntType();
    }
    public IntType(){
        super(kind.integer);
    }

    @Override
    public boolean strictComparable(SemanticType other) {
        return other.isIntType();
    }

    @Override
    public boolean mediumComparable(SemanticType other) {
        return other.isIntType();
    }

    @Override
    public boolean looseComparable(SemanticType other) {
        return other.isIntType();
    }
}
