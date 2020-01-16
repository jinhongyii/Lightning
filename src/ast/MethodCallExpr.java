package ast;

import java.util.ArrayList;

public class MethodCallExpr extends Expr {
    Expr name;
    ArrayList<Expr> arguments;

    public MethodCallExpr(Expr name,ArrayList<Expr> arguments){
        this.name=name;
        this.arguments=arguments;
    }
    void addarguments(Expr argument){
        arguments.add(argument);
    }
}
