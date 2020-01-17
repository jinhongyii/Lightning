package ast;

import frontend.ASTVisitor;

public class SubscriptorExpr extends  Expr{
    Expr index;
    Expr name;

    public Expr getIndex() {
        return index;
    }

    public Expr getName() {
        return name;
    }

    public SubscriptorExpr(Expr index, Expr name){
        this.index=index;
        this.name=name;

    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitSubscriptorExpr(this);
    }
}
