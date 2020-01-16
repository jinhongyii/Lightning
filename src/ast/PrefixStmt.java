package ast;

public class PrefixStmt extends Stmt {
    Expr val;
    String operator;

    public PrefixStmt(Expr val,String operator){
        this.val=val;
        this.operator=operator;
    }
}
