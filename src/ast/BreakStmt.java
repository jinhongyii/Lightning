package ast;

import frontend.ASTVisitor;
import semantic.TypeChecker;

public class BreakStmt extends Stmt {
    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitBreakStmt(this);
    }
}
