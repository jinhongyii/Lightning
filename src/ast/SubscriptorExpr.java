package ast;

public class SubscriptorExpr extends  Expr{
    Expr index;
    Expr name;
    public SubscriptorExpr(Expr index,Expr name){
        this.index=index;
        this.name=name;

    }
}
