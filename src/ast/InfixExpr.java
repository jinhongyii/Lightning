package ast;


import frontend.ASTVisitor;
import semantic.TypeChecker;

public class InfixExpr extends Expr {
    public Expr getLoperand() {
        return loperand;
    }

    public Expr getRoperand() {
        return roperand;
    }

    public String getOperator() {
        return operator;
    }

    Expr loperand;
    Expr roperand;
    String operator;

    public InfixExpr(Expr loperand,Expr roperand,String operator){
        this.loperand=loperand;
        this.roperand=roperand;
        this.operator=operator;
    }

    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitInfixExpr(this);
    }
}
