package ast;

public class PostfixStmt extends Stmt {
    Expr val;
    String operator;

    public Expr getVal() {
        return val;
    }

    public String getOperator() {
        return operator;
    }

    public PostfixStmt(Expr val, String operator){
        this.val=val;
        this.operator=operator;
    }
}
