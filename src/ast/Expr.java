package ast;

import frontend.ASTVisitor;

abstract public class Expr implements Node {
    @Override
    public Object accept(ASTVisitor visitor){
        return null;
    }
}
