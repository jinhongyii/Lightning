package ast;

import frontend.ASTVisitor;
import semantic.SemanticType;
import semantic.TypeChecker;

import java.util.ArrayList;

public class NewExpr extends  Expr {
    String typename;
    SemanticType semanticType;

    public SemanticType getSemanticType() {
        return semanticType;
    }

    public void setSemanticType(SemanticType semanticType) {
        this.semanticType = semanticType;
    }

    ArrayList<Expr> dims;
    int totDim;

    public String getTypename() {
        return typename;
    }

    public ArrayList<Expr> getDims() {
        return dims;
    }

    public int getTotDim() {
        return totDim;
    }

    public NewExpr(String typename, int totDim, ArrayList<Expr> dims){
        this.typename = typename;
        this.totDim=totDim;
        this.dims=dims;
    }
    void addDim(Expr dim){
        dims.add(dim);
    }

    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitNewExpr(this);
    }
}
