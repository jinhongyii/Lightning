package semantic;

public class VoidType extends SemanticType {
    public VoidType(){
        super(kind.voidType);
    }

    @Override
    public boolean canAssignTo(SemanticType other) {
        return other.actual().isVoidType();
    }
}
