package ast;

public class ForStmt extends Stmt {
    Expr init,condition,incr;
    Stmt loopBody;

    public ForStmt(Expr init,Expr condition,Expr incr,Stmt loopBody){
        this.incr=incr;
        this.init=init;
        this.condition=condition;
        this.loopBody=loopBody;

    }
}
