package ast;

public class AssignmentExpr extends Expr{
    Expr lval;
    Expr rval;
    public AssignmentExpr(Expr lval,Expr rval){
        this.lval=lval;
        this.rval=rval;
    }
}
