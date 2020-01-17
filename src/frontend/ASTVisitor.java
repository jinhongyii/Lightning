package frontend;

import ast.*;

public interface ASTVisitor {
    void visitAssignmentExpr(AssignmentExpr node);
    void visitBlockStmt(BlockStmt node);
    void visitBreakStmt(BreakStmt node);
    void visitClassDecl(ClassDecl node);
    void visitCompilationUnit(CompilationUnit node);
    void visitContinueStmt(ContinueStmt node);
    void visitExprStmt(ExprStmt node);
    void visitForStmt(ForStmt node);
    void visitIfStmt(IfStmt node);
    void visitInfixExpr(InfixExpr node);
    void visitLiteralExpr(LiteralExpr node);
    void visitLogicAndExpr(LogicAndExpr node);
    void visitLogicOrExpr(LogicOrExpr node);
    void visitMemberExpr(MemberExpr node);
    void visitMethodCallExpr(MethodCallExpr node);
    void visitMethodDecl(MethodDecl node);
    void visitNameExpr(NameExpr node);
    void visitNewExpr(NewExpr node);
    void visitPostfixExpr(PostfixExpr node);
    void visitPrefixExpr(PrefixExpr node);
    void visitReturnStmt(ReturnStmt node);
    void visitSemi(Semi node);
    void visitSubscriptorExpr(SubscriptorExpr node);
    void visitThisExpr(ThisExpr node);
    void visitVariableDeclStmt(VariableDeclStmt node);
    void visitWhileStmt(WhileStmt node);
    void visit(Node node);
}
