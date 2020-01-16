package ast;



import java.util.ArrayList;

public class ClassDecl implements Node {
    private ArrayList<methodDecl> methods=new ArrayList<>();
    private ArrayList<VariableDeclStmt> variables=new ArrayList<>();
    private String name;
    public ArrayList getMethods() {
        return methods;
    }
    public ArrayList getVariables(){
        return variables;
    }

    public ClassDecl(String name){
        this.name=name;
    }
    public void add(Node m) {
        if (m instanceof methodDecl) {
            methods.add((methodDecl) m);
        } else if (m instanceof VariableDeclStmt) {
            variables.add((VariableDeclStmt) m);
        } else {
            throw new Error("not a member");
        }
    }
}
