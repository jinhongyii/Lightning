package ast;

import frontend.ASTVisitor;

public class ThisExpr extends Expr{
    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visitThisExpr(this);
    }
}
