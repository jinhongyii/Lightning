package ast;

import frontend.ASTVisitor;

public class ContinueStmt extends  Stmt {
    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visitContinueStmt(this);
    }
}
