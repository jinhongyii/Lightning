package ast;

public class IfStmt extends  Stmt {
    Expr condition;
    Stmt then;
    Stmt otherwise;

    public IfStmt(Expr condition,Stmt then,Stmt otherwise){
        this.condition=condition;
        this.then=then;
        this.otherwise=otherwise;
    }
}