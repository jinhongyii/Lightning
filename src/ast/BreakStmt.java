package ast;

import frontend.ASTVisitor;

public class BreakStmt extends Stmt {
    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visitBreakStmt(this);
    }
}
