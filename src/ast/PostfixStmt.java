package ast;

public class PostfixStmt extends Stmt {
    Expr val;
    String operator;

    public PostfixStmt(Expr val,String operator){
        this.val=val;
        this.operator=operator;
    }
}
