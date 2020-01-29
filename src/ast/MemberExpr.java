package ast;

import frontend.ASTVisitor;
import semantic.SemanticType;
import semantic.TypeChecker;

public class MemberExpr extends Expr{
    Expr instance_name;
    SemanticType instanceType;

    public SemanticType getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(SemanticType instanceType) {
        this.instanceType = instanceType;
    }

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
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitMemberExpr(this);
    }
}
