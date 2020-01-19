package ast;

import frontend.ASTVisitor;
import semantic.TypeChecker;

public class ExprStmt extends Stmt {
    Expr expression;

    public Expr getExpression() {
        return expression;
    }

    public ExprStmt(Expr expression){
        this.expression=expression;
    }

    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitExprStmt(this);
    }
}
