package ast;

import frontend.ASTVisitor;
import semantic.TypeChecker;

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
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitPostfixExpr(this);
    }
}
