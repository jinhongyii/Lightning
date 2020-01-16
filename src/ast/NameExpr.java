package ast;



public class NameExpr extends  Expr {
    public String getName() {
        return name;
    }

    String name;

    public NameExpr(String name){
        this.name=name;
    }
}
