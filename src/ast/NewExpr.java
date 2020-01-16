package ast;

import java.util.ArrayList;

public class NewExpr extends  Expr {
    String typename;
    ArrayList<Expr> dims;
    int totDim;
    public NewExpr(String typename, int totDim,ArrayList<Expr> dims){
        this.typename = typename;
        this.totDim=totDim;
        this.dims=dims;
    }
    void addDim(Expr dim){
        dims.add(dim);
    }

}
