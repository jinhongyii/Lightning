package ast;

import frontend.ASTVisitor;
import semantic.TypeChecker;

import java.util.ArrayList;

public class CompilationUnit implements Node{
    public ArrayList<Node> getDeclarations() {
        return Declarations;
    }

    public void setDeclarations(ArrayList<Node> declarations) {
        Declarations = declarations;
    }

    private ArrayList<Node> Declarations=new ArrayList<>();
    public void addDeclarations(Node declare){
        Declarations.add(declare);
    }

    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitCompilationUnit(this);
    }
}
