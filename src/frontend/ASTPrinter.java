package frontend;

import ast.*;

public class ASTPrinter implements ASTVisitor{
    private String prefix="";
    private void indent(){
        prefix+="\t";
    }
    private void dedent(){
        prefix=prefix.substring(0,prefix.length()-1);
    }
    public ASTPrinter(CompilationUnit startNode){
        visit(startNode);
    }
    private void print(String str){
        System.out.println(prefix+str);
    }
    @Override
    public void visitAssignmentExpr(AssignmentExpr node) {
        print("assign");
        indent();
        visit(node.getLval());
        visit(node.getRval());
        dedent();
    }

    @Override
    public void visitBlockStmt(BlockStmt node) {
        print("block");
        indent();
        for (var i : node.getStatements()) {
            visit(i);
        }
        dedent();
    }

    @Override
    public void visitBreakStmt(BreakStmt node) {
        print("break");
    }

    @Override
    public void visitClassDecl(ClassDecl node) {
        print("class declare");
        print("name: "+node.getName());
        print("methods:");
        indent();
        for (var i : node.getMethods()) {
            visit(i);
        }
        dedent();
        print("variables:");
        indent();
        for (var i : node.getVariables()) {
            visit(i);
        }
        dedent();
    }

    @Override
    public void visitCompilationUnit(CompilationUnit node) {
        for (var i : node.getDeclarations()) {
            visit(i);
        }
    }

    @Override
    public void visitContinueStmt(ContinueStmt node) {
        print("continue");
    }

    @Override
    public void visitExprStmt(ExprStmt node) {
        print("expr statement");
        indent();
        visit(node.getExpression());
        dedent();
    }

    @Override
    public void visitForStmt(ForStmt node) {
        print("for");
        if(node.getInit()!=null) {
            print("init:");
            indent();
            visit(node.getInit());
            dedent();
        }
        if(node.getCondition()!=null) {
            print("condition:");
            indent();
            visit(node.getCondition());
            dedent();
        }
        if(node.getIncr()!=null) {
            print("incr:");
            indent();
            visit(node.getIncr());
            dedent();
        }
        print("body:");
        indent();
        visit(node.getLoopBody());
        dedent();
    }

    @Override
    public void visitIfStmt(IfStmt node) {
        print("if");
        print("condition:");
        indent();
        visit(node.getCondition());
        dedent();
        print("then");
        indent();
        visit(node.getThen());
        dedent();
        print("otherwise");
        if (node.getOtherwise() != null) {
            indent();
            visit(node.getOtherwise());
            dedent();
        }
    }

    @Override
    public void visitInfixExpr(InfixExpr node) {
        print("infix "+node.getOperator());
        indent();
        visit(node.getLoperand());
        visit(node.getRoperand());
        dedent();
    }

    @Override
    public void visitLiteralExpr(LiteralExpr node) {
        print("literal "+node.getVal());
    }

    @Override
    public void visitLogicAndExpr(LogicAndExpr node) {
        print("logic and");
        indent();
        visit(node.getLoperand());
        visit(node.getRoperand());
        dedent();
    }

    @Override
    public void visitLogicOrExpr(LogicOrExpr node) {
        print("logic or");
        indent();
        visit(node.getLoperand());
        visit(node.getRoperand());
        dedent();
    }

    @Override
    public void visitMemberExpr(MemberExpr node) {
        print("member");
        print("instance name:");
        indent();
        visit(node.getInstance_name());
        dedent();
        print("member name: "+node.getMember_name());
    }

    @Override
    public void visitMethodCallExpr(MethodCallExpr node) {
        print("method call");
        print("method:");
        indent();
        visit(node.getName());
        dedent();
        print("arguments:");
        indent();
        for (var argument : node.getArguments()) {
            visit(argument);
        }
        dedent();
    }

    @Override
    public void visitMethodDecl(MethodDecl node) {
        print("method declare :"+node.getName());
        print("return type: "+node.getReturnType());
        print("parameters:");
        indent();
        if(node.getParameters()!=null) {
            for (var parameter : node.getParameters()) {
                print(parameter.toString());
            }
        }
        dedent();
        print("method body:");
        indent();
        visit(node.getStmt());
        dedent();
    }

    @Override
    public void visitNameExpr(NameExpr node) {
        print("name: "+node.getName());
    }

    @Override
    public void visitNewExpr(NewExpr node) {
        print("new "+node.getTypename());
        print("totdim: "+node.getTotDim());
        print("given dims:");
        indent();
        for (var i : node.getDims()) {
            visit(i);
        }
        dedent();
    }

    @Override
    public void visitPostfixExpr(PostfixExpr node) {
        print("postfix :"+node.getOperator());
        indent();
        visit(node.getVal());
        dedent();
    }

    @Override
    public void visitPrefixExpr(PrefixExpr node) {
        print("prefix :"+node.getOperator());
        indent();
        visit(node.getVal());
        dedent();
    }

    @Override
    public void visitReturnStmt(ReturnStmt node) {
        print("return ");
        if(node.getVal()!=null) {
            indent();
            visit(node.getVal());
            dedent();
        }
    }

    @Override
    public void visitSemi(Semi node) {
        print("semi");
    }

    @Override
    public void visitSubscriptorExpr(SubscriptorExpr node) {
        print("subscript");
        print("array name");
        indent();
        visit(node.getName());
        dedent();
        print("index:");
        indent();
        visit(node.getIndex());
        dedent();

    }

    @Override
    public void visitThisExpr(ThisExpr node) {
        print("this ");
    }

    @Override
    public void visitVariableDeclStmt(VariableDeclStmt node) {
        print("variable declaration "+node.getName());
        print("type: "+node.getType());
        if(node.getInit()!=null) {
            print("init");
            indent();
            visit(node.getInit());
            dedent();
        }
    }

    @Override
    public void visitWhileStmt(WhileStmt node) {
        print("while");
        print("condition:");
        indent();
        visit(node.getCondition());
        dedent();
        print("loop body:");
        indent();
        visit(node.getLoopBody());
        dedent();
    }

    @Override
    public void visit(Node node) {
        node.accept(this);
    }
}
