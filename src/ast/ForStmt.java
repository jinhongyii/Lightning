package ast;
import frontend.ASTVisitor;
import org.jetbrains.annotations.Nullable;

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
    public void accept(ASTVisitor visitor) {
        visitor.visitForStmt(this);
    }
}
