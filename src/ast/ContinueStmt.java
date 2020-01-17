package ast;

import frontend.ASTVisitor;

public class ContinueStmt extends  Stmt {
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitContinueStmt(this);
    }
}
