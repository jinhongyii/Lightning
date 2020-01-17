package ast;

import frontend.ASTVisitor;
import org.jetbrains.annotations.Nullable;

public class LiteralExpr extends  Expr {
//    enum literal_type{integer,string,bool,Null};
//    literal_type type;
//    Integer val1;
//    String val2;
//    Boolean val3;
    Object val;
    @Nullable
    public Object getVal() {
        return val;
    }

    public LiteralExpr(Object val){
        this.val=val;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitLiteralExpr(this);
    }
}
