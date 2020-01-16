package ast;

public class LogicOrExpr extends  Expr{
    Expr loperand;
    Expr roperand;

    public LogicOrExpr(Expr loperand, Expr roperand) {
        this.loperand=loperand;
        this.roperand=roperand;
    }
}
