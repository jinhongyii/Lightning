package ast;

import frontend.ASTVisitor;
import semantic.TypeChecker;

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
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitWhileStmt(this);
    }
}
