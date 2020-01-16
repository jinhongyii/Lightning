package ast;

public class MemberExpr extends Expr{
    Expr instance_name;

    String member_name;

    public MemberExpr(Expr instance_name,String member_name){
        this.instance_name=instance_name;
        this.member_name=member_name;
    }
}
