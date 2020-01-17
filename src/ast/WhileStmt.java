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
    public void accept(ASTVisitor visitor) {
        visitor.visitWhileStmt(this);
    }
}
