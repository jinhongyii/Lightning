package frontend;

import ast.*;

public interface ASTVisitor {
    void visit(AssignmentExpr node);
    void visit(BlockStmt node);
    void visit(BreakStmt node);
    void visit(ClassDecl node);
    void visit(CompilationUnit node);
    void visit(ContinueStmt node);
    void visit(ExprStmt node);
    void visit(ForStmt node);
    void visit(IfStmt node);
    void visit(InfixExpr node);
    void visit(LiteralExpr node);
    void visit(LogicAndExpr node);
    void visit(LogicOrExpr node);
    void visit(MemberExpr node);
    void visit(MethodCallExpr node);
    void visit(methodDecl node);
    void visit(NameExpr node);
    void visit(NewExpr node);
    void visit(PostfixStmt node);
    void visit(PrefixStmt node);
    void visit(ReturnStmt node);
    void visit(Semi node);
    void visit(SubscriptorExpr node);
    void visit(ThisExpr node);
    void visit(VariableDeclStmt node);
    void visit(WhileStmt node);

}
