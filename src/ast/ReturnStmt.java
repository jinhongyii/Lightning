package ast;

import frontend.ASTVisitor;
import semantic.TypeChecker;

public class ReturnStmt extends Stmt{
    Expr val;

    public Expr getVal() {
        return val;
    }

    public ReturnStmt(Expr val){
        this.val=val;
    }

    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitReturnStmt(this);
    }
}
