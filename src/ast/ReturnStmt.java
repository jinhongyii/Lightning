package ast;

public class ReturnStmt extends Stmt{
    Expr val;
    public ReturnStmt(Expr val){
        this.val=val;
    }
}
