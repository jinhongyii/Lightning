package ast;

import frontend.ASTVisitor;

public class Semi extends Stmt {
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitSemi(this);
    }
}
