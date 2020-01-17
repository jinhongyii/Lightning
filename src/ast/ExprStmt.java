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
    public void accept(ASTVisitor visitor) {
        visitor.visitExprStmt(this);
    }
}
