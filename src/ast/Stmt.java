package ast;

import frontend.ASTVisitor;

abstract public class Stmt implements Node{
    @Override
    public Object accept(ASTVisitor visitor){return null ;}
}
