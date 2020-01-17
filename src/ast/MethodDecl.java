package ast;


import frontend.ASTVisitor;

import java.util.ArrayList;

public class MethodDecl implements Node {
    private String name;

    public static class parameter {
        Type type;
        String name;
        public parameter(Type type, String name){
            this.type=type;
            this.name=name;
        }

        @Override
        public String toString() {
            return "type: "+type+" name: "+name;
        }
    }
    private Type returnType;
    private ArrayList<parameter> parameters;
    private Stmt stmt;

    public String getName() {
        return name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public ArrayList<parameter> getParameters() {
        return parameters;
    }

    public Stmt getStmt() {
        return stmt;
    }

    public MethodDecl(String name, Type returnType, Stmt stmt, ArrayList<parameter> parameters){
        this.name=name;
        this.returnType=returnType;
        this.stmt=stmt;
        this.parameters=parameters;
    }
    void addparameter(Type type,String name){
        parameters.add(new parameter(type,name));
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visitMethodDecl(this);
    }
}
