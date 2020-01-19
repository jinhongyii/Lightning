package ast;
import frontend.ASTVisitor;
import org.jetbrains.annotations.Nullable;
import semantic.TypeChecker;

public class ForStmt extends Stmt {
    Expr init,condition,incr;
    Stmt loopBody;
    @Nullable
    public Expr getCondition() {
        return condition;
    }
    @Nullable
    public Expr getIncr() {
        return incr;
    }

    public Stmt getLoopBody() {
        return loopBody;
    }
    @Nullable
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
