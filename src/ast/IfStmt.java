package ast;

import frontend.ASTVisitor;
import semantic.TypeChecker;

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

    public Stmt getOtherwise() {
        return otherwise;
    }

    public IfStmt(Expr condition, Stmt then, Stmt otherwise){
        this.condition=condition;
        this.then=then;
        this.otherwise=otherwise;
    }

    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitIfStmt(this);
    }
}
