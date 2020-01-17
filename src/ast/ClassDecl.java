package ast;



import frontend.ASTVisitor;

import java.util.ArrayList;

public class ClassDecl implements Node {
    private ArrayList<MethodDecl> methods=new ArrayList<>();
    private ArrayList<VariableDeclStmt> variables=new ArrayList<>();
    private String name;
    public ArrayList<MethodDecl> getMethods() {
        return methods;
    }
    public ArrayList<VariableDeclStmt> getVariables(){
        return variables;
    }

    public ClassDecl(String name){
        this.name=name;
    }
    public void add(Node m) {
        if (m instanceof MethodDecl) {
            methods.add((MethodDecl) m);
        } else if (m instanceof VariableDeclStmt) {
            variables.add((VariableDeclStmt) m);
        } else {
            throw new Error("not a member");
        }
    }


    public String getName() {
        return name;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visitClassDecl(this);
    }
}
