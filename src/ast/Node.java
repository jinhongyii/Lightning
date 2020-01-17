package ast;

import frontend.ASTVisitor;

public interface Node {
    void accept(ASTVisitor visitor);
}
