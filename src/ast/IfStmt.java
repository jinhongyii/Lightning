package ast;

import frontend.ASTVisitor;
import org.jetbrains.annotations.Nullable;

public class IfStmt extends  Stmt {
    Expr condition;
    Stmt then;
    Stmt otherwise;

    public Expr getCondition() {
        return condition;
    }

    public Stmt getThen() {
        return then;
    }
    @Nullable
    public Stmt getOtherwise() {
        return otherwise;
    }

    public IfStmt(Expr condition, Stmt then, Stmt otherwise){
        this.condition=condition;
        this.then=then;
        this.otherwise=otherwise;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitIfStmt(this);
    }
}
