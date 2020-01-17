package ast;

import frontend.ASTVisitor;

abstract public class Stmt implements Node{
    @Override
    public void accept(ASTVisitor visitor){}
}
