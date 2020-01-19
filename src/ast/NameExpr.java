package ast;


import frontend.ASTVisitor;
import semantic.TypeChecker;

public class NameExpr extends  Expr {
    public String getName() {
        return name;
    }

    String name;

    public NameExpr(String name){
        this.name=name;
    }

    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitNameExpr(this);
    }
}
