package ast;

import frontend.ASTVisitor;
import semantic.TypeChecker;

abstract public class Stmt implements Node{
    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {return null ;}
}
