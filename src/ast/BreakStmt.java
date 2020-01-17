package ast;

import frontend.ASTVisitor;

public class BreakStmt extends Stmt {
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitBreakStmt(this);
    }
}
