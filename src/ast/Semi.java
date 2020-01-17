package ast;

import frontend.ASTVisitor;

public class Semi extends Stmt {
    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visitSemi(this);
    }
}
