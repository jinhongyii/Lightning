package ast;

public class LiteralExpr extends  Expr {
//    enum literal_type{integer,string,bool,Null};
//    literal_type type;
//    Integer val1;
//    String val2;
//    Boolean val3;
    Object val;

    public Object getVal() {
        return val;
    }

    public LiteralExpr(Object val){
        this.val=val;
    }

}
