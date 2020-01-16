package ast;

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
}
