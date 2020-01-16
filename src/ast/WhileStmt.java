package ast;

public class WhileStmt extends Stmt {
    Expr condition;
    Stmt loopBody;

    public WhileStmt(Expr condition,Stmt loopBody){
        this.condition=condition;
        this.loopBody=loopBody;
    }
}
