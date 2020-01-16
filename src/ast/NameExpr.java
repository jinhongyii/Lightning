package ast;



public class NameExpr extends  Expr {
    String name;

    public NameExpr(String name){
        this.name=name;
    }
}
