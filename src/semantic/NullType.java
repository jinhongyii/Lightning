package semantic;

public class NullType extends SemanticType {
    @Override
    public boolean canAssignTo(SemanticType other) {
        return other.actual().isRecordType() || other.actual().isArrayType() || other.actual().isNullType()|| other.actual().isVoidType();
    }
    public NullType(){
        super(kind.nil);
    }

    @Override
    public boolean looseComparable(SemanticType other) {
        return other.isNullType() || other.isRecordType() || other.isArrayType();
    }
}
