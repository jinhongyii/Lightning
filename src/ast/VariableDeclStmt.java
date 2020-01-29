package ast;

import frontend.ASTVisitor;
import org.jetbrains.annotations.Nullable;
import semantic.SemanticType;
import semantic.TypeChecker;

public class VariableDeclStmt extends Stmt{
    String name;
    Type type;
    Expr init;

    public SemanticType getSemanticType() {
        return semanticType;
    }

    public void setSemanticType(SemanticType semanticType) {
        this.semanticType = semanticType;
    }

    SemanticType semanticType;
    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
    @Nullable
    public Expr getInit() {
        return init;
    }

    public VariableDeclStmt(String name, Type type, Expr init){
        this.name=name;
        this.type= type;
        this.init=init;

    }

    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitVariableDeclStmt(this);
    }
}
