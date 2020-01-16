package ast;

public class InfixExpr extends Expr {
    Expr loperand;
    Expr roperand;
    String operator;

    public InfixExpr(Expr loperand,Expr roperand,String operator){
        this.loperand=loperand;
        this.roperand=roperand;
        this.operator=operator;
    }
}
