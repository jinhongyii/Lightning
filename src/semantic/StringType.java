package semantic;

public class StringType extends SemanticType {
    @Override
    public boolean canAssignTo(SemanticType other) {
        return other.actual().isStringType();
    }
    public StringType(){
        super(kind.string);
    }

    @Override
    public boolean mediumComparable(SemanticType other) {
        return other.isStringType();
    }

    @Override
    public boolean looseComparable(SemanticType other) {
        return other.isStringType();
    }
}
