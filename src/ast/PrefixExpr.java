package ast;

import frontend.ASTVisitor;

public class PrefixExpr extends Expr {
    Expr val;
    String operator;

    public Expr getVal() {
        return val;
    }

    public String getOperator() {
        return operator;
    }

    public PrefixExpr(Expr val, String operator){
        this.val=val;
        this.operator=operator;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visitPrefixExpr(this);
    }
}
