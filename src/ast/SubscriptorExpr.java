package ast;

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
}
