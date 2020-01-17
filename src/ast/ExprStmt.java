package ast;

import frontend.ASTVisitor;

public class ExprStmt extends Stmt {
    Expr expression;

    public Expr getExpression() {
        return expression;
    }

    public ExprStmt(Expr expression){
        this.expression=expression;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visitExprStmt(this);
    }
}
