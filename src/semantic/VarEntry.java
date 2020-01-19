package semantic;

public class VarEntry extends NameEntry {
    SemanticType type;

    public VarEntry(SemanticType type){
        super(kind.varEntry);
        this.type=type;

    }
}
