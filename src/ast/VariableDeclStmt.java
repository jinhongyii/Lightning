package ast;

import frontend.ASTVisitor;
import org.jetbrains.annotations.Nullable;

public class VariableDeclStmt extends Stmt{
    String name;
    Type type;
    Expr init;

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
    public Object accept(ASTVisitor visitor) {
        return visitor.visitVariableDeclStmt(this);
    }
}
