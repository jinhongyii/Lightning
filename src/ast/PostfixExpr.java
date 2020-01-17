package ast;

import frontend.ASTVisitor;

public class PostfixExpr extends Expr{
    Expr val;
    String operator;

    public Expr getVal() {
        return val;
    }

    public String getOperator() {
        return operator;
    }

    public PostfixExpr(Expr val, String operator){
        this.val=val;
        this.operator=operator;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visitPostfixExpr(this);
    }
}
