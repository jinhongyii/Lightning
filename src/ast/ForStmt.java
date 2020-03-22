package ast;
import frontend.ASTVisitor;
import semantic.TypeChecker;

public class ForStmt extends Stmt {
    Expr init,condition,incr;
    Stmt loopBody;

    public Expr getCondition() {
        return condition;
    }

    public Expr getIncr() {
        return incr;
    }

    public Stmt getLoopBody() {
        return loopBody;
    }

    public Expr getInit() {
        return init;
    }

    public ForStmt(Expr init, Expr condition, Expr incr, Stmt loopBody){
        this.incr=incr;
        this.init=init;
        this.condition=condition;
        this.loopBody=loopBody;

    }

    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitForStmt(this);
    }
}
