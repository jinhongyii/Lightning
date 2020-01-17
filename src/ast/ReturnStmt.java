package ast;

import frontend.ASTVisitor;
import org.jetbrains.annotations.Nullable;

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
    public Object accept(ASTVisitor visitor) {
        return visitor.visitReturnStmt(this);
    }
}
