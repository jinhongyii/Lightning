package ast;

import frontend.ASTVisitor;

public interface Node {
    Object accept(ASTVisitor visitor);
}
