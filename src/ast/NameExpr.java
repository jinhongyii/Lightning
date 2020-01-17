package ast;


import frontend.ASTVisitor;

public class NameExpr extends  Expr {
    public String getName() {
        return name;
    }

    String name;

    public NameExpr(String name){
        this.name=name;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visitNameExpr(this);
    }
}
