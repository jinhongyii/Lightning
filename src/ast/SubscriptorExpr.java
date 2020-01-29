package ast;

import frontend.ASTVisitor;
import semantic.SemanticType;
import semantic.TypeChecker;

public class SubscriptorExpr extends  Expr{
    Expr index;
    Expr name;
    SemanticType elementType;

    public SemanticType getElementType() {
        return elementType;
    }

    public void setElementType(SemanticType elementType) {
        this.elementType = elementType;
    }

    public Expr getIndex() {
        return index;
    }

    public Expr getName() {
        return name;
    }

    public SubscriptorExpr(Expr index, Expr name){
        this.index=index;
        this.name=name;

    }

    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitSubscriptorExpr(this);
    }
}
