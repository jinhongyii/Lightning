package ast;

import frontend.ASTVisitor;

public class ThisExpr extends Expr{
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitThisExpr(this);
    }
}
