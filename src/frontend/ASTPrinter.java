package frontend;

import ast.*;

public class ASTPrinter implements ASTVisitor{
    private String prefix="";
    void indent(){
        prefix+="\t";
    }
    void dedent(){
        prefix=prefix.substring(0,prefix.length()-1);
    }
    public ASTPrinter(CompilationUnit startNode){
        visit(startNode);
    }
    @Override
    public void visit(AssignmentExpr node) {
        System.out.println("assign");
        visit();
    }

    @Override
    public void visit(BlockStmt node) {

    }

    @Override
    public void visit(BreakStmt node) {

    }

    @Override
    public void visit(ClassDecl node) {

    }

    @Override
    public void visit(CompilationUnit node) {

    }

    @Override
    public void visit(ContinueStmt node) {

    }

    @Override
    public void visit(ExprStmt node) {

    }

    @Override
    public void visit(ForStmt node) {

    }

    @Override
    public void visit(IfStmt node) {

    }

    @Override
    public void visit(InfixExpr node) {

    }

    @Override
    public void visit(LiteralExpr node) {

    }

    @Override
    public void visit(LogicAndExpr node) {

    }

    @Override
    public void visit(LogicOrExpr node) {

    }

    @Override
    public void visit(MemberExpr node) {

    }

    @Override
    public void visit(MethodCallExpr node) {

    }

    @Override
    public void visit(methodDecl node) {

    }

    @Override
    public void visit(NameExpr node) {

    }

    @Override
    public void visit(NewExpr node) {

    }

    @Override
    public void visit(PostfixStmt node) {

    }

    @Override
    public void visit(PrefixStmt node) {

    }

    @Override
    public void visit(ReturnStmt node) {

    }

    @Override
    public void visit(Semi node) {

    }

    @Override
    public void visit(SubscriptorExpr node) {

    }

    @Override
    public void visit(ThisExpr node) {

    }

    @Override
    public void visit(VariableDeclStmt node) {

    }

    @Override
    public void visit(WhileStmt node) {

    }
}
