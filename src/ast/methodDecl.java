package ast;


import com.sun.source.tree.NewArrayTree;

import java.util.ArrayList;

public class methodDecl implements Node {
    private String name;

    public static class parameter {
        Type type;
        String name;
        public parameter(Type type, String name){
            this.type=type;
            this.name=name;
        }
    }
    private Type returnType;
    private ArrayList<parameter> parameters;
    private Stmt stmt;
    public methodDecl(String name,Type returnType,Stmt stmt,ArrayList<parameter> parameters){
        this.name=name;
        this.returnType=returnType;
        this.stmt=stmt;
        this.parameters=parameters;
    }
    void addparameter(Type type,String name){
        parameters.add(new parameter(type,name));
    }

}
