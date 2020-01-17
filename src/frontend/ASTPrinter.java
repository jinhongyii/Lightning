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
    public Object visitAssignmentExpr(AssignmentExpr node) {
        print("assign");
        indent();
        visit(node.getLval());
        visit(node.getRval());
        dedent();
        return null;
    }

    @Override
    public Object visitBlockStmt(BlockStmt node) {
        print("block");
        indent();
        for (var i : node.getStatements()) {
            visit(i);
        }
        dedent();
        return null;
    }

    @Override
    public Object visitBreakStmt(BreakStmt node) {
        print("break");
        return null;
    }

    @Override
    public Object visitClassDecl(ClassDecl node) {
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
        return null;
    }

    @Override
    public Object visitCompilationUnit(CompilationUnit node) {
        for (var i : node.getDeclarations()) {
            visit(i);
        }
        return null;
    }

    @Override
    public Object visitContinueStmt(ContinueStmt node) {
        print("continue");
        return null;
    }

    @Override
    public Object visitExprStmt(ExprStmt node) {
        print("expr statement");
        indent();
        visit(node.getExpression());
        dedent();
        return null;
    }

    @Override
    public Object visitForStmt(ForStmt node) {
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
        return null;
    }

    @Override
    public Object visitIfStmt(IfStmt node) {
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
        return null;
    }

    @Override
    public Object visitInfixExpr(InfixExpr node) {
        print("infix "+node.getOperator());
        indent();
        visit(node.getLoperand());
        visit(node.getRoperand());
        dedent();
        return null;
    }

    @Override
    public Object visitLiteralExpr(LiteralExpr node) {
        print("literal "+node.getVal());
        return null;
    }

    @Override
    public Object visitLogicAndExpr(LogicAndExpr node) {
        print("logic and");
        indent();
        visit(node.getLoperand());
        visit(node.getRoperand());
        dedent();
        return null;
    }

    @Override
    public Object visitLogicOrExpr(LogicOrExpr node) {
        print("logic or");
        indent();
        visit(node.getLoperand());
        visit(node.getRoperand());
        dedent();
        return null;
    }

    @Override
    public Object visitMemberExpr(MemberExpr node) {
        print("member");
        print("instance name:");
        indent();
        visit(node.getInstance_name());
        dedent();
        print("member name: "+node.getMember_name());
        return null;
    }

    @Override
    public Object visitMethodCallExpr(MethodCallExpr node) {
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
        return null;
    }

    @Override
    public Object visitMethodDecl(MethodDecl node) {
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
        return null;
    }

    @Override
    public Object visitNameExpr(NameExpr node) {
        print("name: "+node.getName());
        return null;
    }

    @Override
    public Object visitNewExpr(NewExpr node) {
        print("new "+node.getTypename());
        print("totdim: "+node.getTotDim());
        print("given dims:");
        indent();
        for (var i : node.getDims()) {
            visit(i);
        }
        dedent();
        return null;
    }

    @Override
    public Object visitPostfixExpr(PostfixExpr node) {
        print("postfix :"+node.getOperator());
        indent();
        visit(node.getVal());
        dedent();
        return null;
    }

    @Override
    public Object visitPrefixExpr(PrefixExpr node) {
        print("prefix :"+node.getOperator());
        indent();
        visit(node.getVal());
        dedent();
        return null;
    }

    @Override
    public Object visitReturnStmt(ReturnStmt node) {
        print("return ");
        if(node.getVal()!=null) {
            indent();
            visit(node.getVal());
            dedent();
        }
        return null;
    }

    @Override
    public Object visitSemi(Semi node) {
        print("semi");
        return null;
    }

    @Override
    public Object visitSubscriptorExpr(SubscriptorExpr node) {
        print("subscript");
        print("array name");
        indent();
        visit(node.getName());
        dedent();
        print("index:");
        indent();
        visit(node.getIndex());
        dedent();
        return null;
    }

    @Override
    public Object visitThisExpr(ThisExpr node) {
        print("this ");
        return null;
    }

    @Override
    public Object visitVariableDeclStmt(VariableDeclStmt node) {
        print("variable declaration "+node.getName());
        print("type: "+node.getType());
        if(node.getInit()!=null) {
            print("init");
            indent();
            visit(node.getInit());
            dedent();
        }
        return null;
    }

    @Override
    public Object visitWhileStmt(WhileStmt node) {
        print("while");
        print("condition:");
        indent();
        visit(node.getCondition());
        dedent();
        print("loop body:");
        indent();
        visit(node.getLoopBody());
        dedent();
        return null;
    }

    @Override
    public Object visit(Node node) {
        return node.accept(this);
    }
}
