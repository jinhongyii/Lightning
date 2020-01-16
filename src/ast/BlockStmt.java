package ast;

import java.util.ArrayList;

public class BlockStmt extends Stmt{
    ArrayList<Stmt> statements=new ArrayList<>();
    public void addStatement(Stmt stmt){
        statements.add(stmt);
    }
}
