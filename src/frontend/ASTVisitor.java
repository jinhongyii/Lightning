package frontend;

import ast.*;
import semantic.TypeChecker;

public interface ASTVisitor {
    Object visitAssignmentExpr(AssignmentExpr node) throws TypeChecker.semanticException;
    Object visitBlockStmt(BlockStmt node) throws TypeChecker.semanticException;
    Object visitBreakStmt(BreakStmt node) throws TypeChecker.semanticException;
    Object visitClassDecl(ClassDecl node) throws TypeChecker.semanticException;
    Object visitCompilationUnit(CompilationUnit node) throws TypeChecker.semanticException;
    Object visitContinueStmt(ContinueStmt node) throws TypeChecker.semanticException;
    Object visitExprStmt(ExprStmt node) throws TypeChecker.semanticException;
    Object visitForStmt(ForStmt node) throws TypeChecker.semanticException;
    Object visitIfStmt(IfStmt node) throws TypeChecker.semanticException;
    Object visitInfixExpr(InfixExpr node) throws TypeChecker.semanticException;
    Object visitLiteralExpr(LiteralExpr node);
    Object visitLogicAndExpr(LogicAndExpr node) throws TypeChecker.semanticException;
    Object visitLogicOrExpr(LogicOrExpr node) throws TypeChecker.semanticException;
    Object visitMemberExpr(MemberExpr node) throws TypeChecker.semanticException;
    Object visitMethodCallExpr(MethodCallExpr node) throws TypeChecker.semanticException;
    Object visitMethodDecl(MethodDecl node) throws TypeChecker.semanticException;
    Object visitNameExpr(NameExpr node) throws TypeChecker.semanticException;
    Object visitNewExpr(NewExpr node) throws TypeChecker.semanticException;
    Object visitPostfixExpr(PostfixExpr node) throws TypeChecker.semanticException;
    Object visitPrefixExpr(PrefixExpr node) throws TypeChecker.semanticException;
    Object visitReturnStmt(ReturnStmt node) throws TypeChecker.semanticException;
    Object visitSemi(Semi node);
    Object visitSubscriptorExpr(SubscriptorExpr node) throws TypeChecker.semanticException;
    Object visitThisExpr(ThisExpr node);
    Object visitVariableDeclStmt(VariableDeclStmt node) throws TypeChecker.semanticException;
    Object visitWhileStmt(WhileStmt node) throws TypeChecker.semanticException;
    Object visit(Node node) throws TypeChecker.semanticException;
}
