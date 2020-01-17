package ast;

import frontend.ASTVisitor;

import java.util.ArrayList;

public class BlockStmt extends Stmt{
    ArrayList<Stmt> statements=new ArrayList<>();
    public void addStatement(Stmt stmt){
        statements.add(stmt);
    }

    public ArrayList<Stmt> getStatements() {
        return statements;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visitBlockStmt(this);
    }
}
