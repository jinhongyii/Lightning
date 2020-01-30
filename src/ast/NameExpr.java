package ast;


import frontend.ASTVisitor;
import semantic.TypeChecker;

public class NameExpr extends  Expr {
    public String getName() {
        return name;
    }

    String name;
    VariableDeclStmt declStmt;

    public void setDeclStmt(VariableDeclStmt declStmt) {
        this.declStmt = declStmt;
    }

    public NameExpr(String name){
        this.name=name;
    }

    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitNameExpr(this);
    }

    public VariableDeclStmt getDeclStmt() {
        return declStmt;
    }
}
