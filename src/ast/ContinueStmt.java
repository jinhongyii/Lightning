package ast;

import frontend.ASTVisitor;
import semantic.TypeChecker;

public class ContinueStmt extends  Stmt {
    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitContinueStmt(this);
    }
}
