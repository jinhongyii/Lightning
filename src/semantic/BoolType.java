package semantic;

public class BoolType extends SemanticType {
    @Override
    public boolean canAssignTo(SemanticType other) {
        return other.actual().isBoolType();
    }
    public BoolType(){
        super(kind.bool);
    }

    @Override
    public boolean looseComparable(SemanticType other) {
        return other.isBoolType();
    }
}
