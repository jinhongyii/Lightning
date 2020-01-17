package frontend;

import ast.*;

public interface ASTVisitor {
    Object visitAssignmentExpr(AssignmentExpr node);
    Object visitBlockStmt(BlockStmt node);
    Object visitBreakStmt(BreakStmt node);
    Object visitClassDecl(ClassDecl node);
    Object visitCompilationUnit(CompilationUnit node);
    Object visitContinueStmt(ContinueStmt node);
    Object visitExprStmt(ExprStmt node);
    Object visitForStmt(ForStmt node);
    Object visitIfStmt(IfStmt node);
    Object visitInfixExpr(InfixExpr node);
    Object visitLiteralExpr(LiteralExpr node);
    Object visitLogicAndExpr(LogicAndExpr node);
    Object visitLogicOrExpr(LogicOrExpr node);
    Object visitMemberExpr(MemberExpr node);
    Object visitMethodCallExpr(MethodCallExpr node);
    Object visitMethodDecl(MethodDecl node);
    Object visitNameExpr(NameExpr node);
    Object visitNewExpr(NewExpr node);
    Object visitPostfixExpr(PostfixExpr node);
    Object visitPrefixExpr(PrefixExpr node);
    Object visitReturnStmt(ReturnStmt node);
    Object visitSemi(Semi node);
    Object visitSubscriptorExpr(SubscriptorExpr node);
    Object visitThisExpr(ThisExpr node);
    Object visitVariableDeclStmt(VariableDeclStmt node);
    Object visitWhileStmt(WhileStmt node);
    Object visit(Node node);
}
