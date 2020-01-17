package ast;

import frontend.ASTVisitor;

public class MemberExpr extends Expr{
    Expr instance_name;

    String member_name;

    public Expr getInstance_name() {
        return instance_name;
    }

    public String getMember_name() {
        return member_name;
    }

    public MemberExpr(Expr instance_name, String member_name){
        this.instance_name=instance_name;
        this.member_name=member_name;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visitMemberExpr(this);
    }
}
