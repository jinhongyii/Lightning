package ast;

public class LogicAndExpr extends Expr {
    Expr loperand;
    Expr roperand;

    public LogicAndExpr(Expr loperand, Expr roperand) {
        this.loperand=loperand;
        this.roperand=roperand;
    }
}
