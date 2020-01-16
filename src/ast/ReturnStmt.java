package ast;

public class ReturnStmt extends Stmt{
    Expr val;

    public Expr getVal() {
        return val;
    }

    public ReturnStmt(Expr val){
        this.val=val;
    }
}
