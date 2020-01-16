package ast;

public class VariableDeclStmt extends Stmt{
    String name;
    Type type;
    Expr init;
    public VariableDeclStmt(String name,Type type,Expr init){
        this.name=name;
        this.type= type;
        this.init=init;

    }
}
