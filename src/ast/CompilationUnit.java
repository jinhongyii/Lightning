package ast;

import java.util.ArrayList;

public class CompilationUnit implements Node{
    private ArrayList<Node> Declarations=new ArrayList<>();
    public void addDeclarations(Node declare){
        Declarations.add(declare);
    }
}
