package ast;

import frontend.ASTVisitor;
import org.jetbrains.annotations.Nullable;
import semantic.TypeChecker;

public class ReturnStmt extends Stmt{
    Expr val;
    @Nullable
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
