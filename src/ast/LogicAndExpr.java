package ast;

import frontend.ASTVisitor;

public class LogicAndExpr extends Expr {
    Expr loperand;
    Expr roperand;

    public Expr getLoperand() {
        return loperand;
    }

    public Expr getRoperand() {
        return roperand;
    }

    public LogicAndExpr(Expr loperand, Expr roperand) {
        this.loperand=loperand;
        this.roperand=roperand;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitLogicAndExpr(this);
    }
}
