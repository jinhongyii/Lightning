package ast;

import frontend.ASTVisitor;
import semantic.TypeChecker;

public interface Node {
    Object accept(ASTVisitor visitor) throws TypeChecker.semanticException;
}
