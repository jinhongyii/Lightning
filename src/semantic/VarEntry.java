package semantic;

import ast.VariableDeclStmt;

public class VarEntry extends NameEntry {
    SemanticType type;
    VariableDeclStmt declStmt;

    public VarEntry( SemanticType type, VariableDeclStmt declStmt) {
        super(kind.varEntry);
        this.type = type;
        this.declStmt = declStmt;
    }

    public VarEntry(SemanticType type){
        super(kind.varEntry);
        this.type=type;
        this.declStmt=null;
    }
}
