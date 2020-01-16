package ast;

public class ExprStmt extends Stmt {
    Expr expression;

    public Expr getExpression() {
        return expression;
    }

    public ExprStmt(Expr expression){
        this.expression=expression;
    }
}
