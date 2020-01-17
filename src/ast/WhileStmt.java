package ast;

import frontend.ASTVisitor;

public class WhileStmt extends Stmt {
    public Expr getCondition() {
        return condition;
    }

    public Stmt getLoopBody() {
        return loopBody;
    }

    Expr condition;
    Stmt loopBody;

    public WhileStmt(Expr condition,Stmt loopBody){
        this.condition=condition;
        this.loopBody=loopBody;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visitWhileStmt(this);
    }
}
