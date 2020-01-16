package frontend;

import ast.*;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import parser.mxBaseListener;
import parser.mxParser;

import java.time.temporal.ValueRange;
import java.util.ArrayList;

public class ASTBuilder extends mxBaseListener {
    public ParseTreeProperty<Object> values = new ParseTreeProperty<>();
    public CompilationUnit compilationUnit=new CompilationUnit();
    @Override
    public void exitCompilationUnit(mxParser.CompilationUnitContext ctx) {
        for(var sons: ctx.classDeclaration()){
            compilationUnit.addDeclarations((Node) values.get(sons));
        }
        for(var sons:ctx.funcDeclaration()){
            compilationUnit.addDeclarations((Node) values.get(sons));
        }
        for(var sons:ctx.variableDeclaration()){
            var decls=(ArrayList)values.get(sons);
            for(Object decl:decls){
                compilationUnit.addDeclarations((Node) decl);
            }
        }
    }

    @Override
    public void exitClassDeclaration(mxParser.ClassDeclarationContext ctx) {
        var classDeclaration=new ClassDecl(ctx.IDENTIFIER().toString());
        for(var i:ctx.classBody().classBodyDeclaration()){
            classDeclaration.add((Node) values.get(i));
        }
        values.put(ctx, classDeclaration);
    }

    @Override
    public void exitClassBodyDeclaration(mxParser.ClassBodyDeclarationContext ctx) {
        if (ctx.methodDeclaration() != null) {
            values.put(ctx, values.get(ctx.methodDeclaration()));
        } else {
            values.put(ctx, values.get(ctx.variableDeclaration()));
        }
    }

    @Override
    public void exitMethodDeclaration(mxParser.MethodDeclarationContext ctx) {

        Type returntype;
        if (ctx.typeTypeOrVoid() == null) {
            returntype = null;
        } else {
            returntype= (Type) values.get(ctx.typeTypeOrVoid());
        }
        String name=ctx.IDENTIFIER().toString();
        BlockStmt stmt= (BlockStmt) values.get(ctx.block().blockStatement());
        ArrayList<methodDecl.parameter> parameters;
        if (ctx.parameters().parameterList() == null) {
            parameters = null;
        } else {
            parameters= (ArrayList<methodDecl.parameter>) values.get(ctx.parameters().parameterList());
        }
        values.put(ctx, new methodDecl(name,returntype,stmt,parameters));
    }

    class Variable {
        String name;
        Expr init;
        Variable(String name, Expr init){
            this.name=name;
            this.init=init;
        }
    }
    @Override
    public void exitVariableDeclaration(mxParser.VariableDeclarationContext ctx) {
        ArrayList<VariableDeclStmt> stmts=new ArrayList<>();
        Type typename= (Type) values.get(ctx.typeType());
        for (var vars : ctx.variableDecorator()) {
            Variable variable= (Variable) values.get(vars);
            stmts.add(new VariableDeclStmt(variable.name, typename, variable.init));
        }
        values.put(ctx, stmts);
    }

    @Override
    public void exitVariableDecorator(mxParser.VariableDecoratorContext ctx) {
        if (ctx.expression() != null) {
            values.put(ctx, new Variable(ctx.IDENTIFIER().toString(), (Expr) values.get(ctx.expression())));
        } else {
            values.put(ctx, new Variable(ctx.IDENTIFIER().toString(),null));
        }

    }

    @Override
    public void exitTypeType(mxParser.TypeTypeContext ctx) {
        String typename;
        if (ctx.primitiveType() != null) {
            typename= (String) values.get(ctx.primitiveType());
        } else {
            typename=(String)values.get(ctx.classType());
        }
        values.put(ctx, new Type(typename, (ctx.getChildCount()-1)/2 ));
    }

    @Override
    public void exitClassType(mxParser.ClassTypeContext ctx) {
        values.put(ctx, new String(ctx.IDENTIFIER().toString()));
    }

    @Override
    public void exitPrimitiveType(mxParser.PrimitiveTypeContext ctx) {
        if (ctx.BOOL() != null) {
            values.put(ctx, ctx.BOOL().toString());
        } else if (ctx.INT() != null) {
            values.put(ctx, ctx.INT().toString());
        } else {
            values.put(ctx, ctx.STRING().toString());
        }
    }

    @Override
    public void exitTypeTypeOrVoid(mxParser.TypeTypeOrVoidContext ctx) {
        if (ctx.VOID() != null) {
            values.put(ctx, new Type("void", 0));
        } else {
            values.put(ctx, values.get(ctx.typeType()));
        }
    }

    @Override
    public void exitFuncDeclaration(mxParser.FuncDeclarationContext ctx) {

        Type returntype= (Type) values.get(ctx.typeTypeOrVoid());
        String name=ctx.IDENTIFIER().toString();
        BlockStmt stmt= (BlockStmt) values.get(ctx.block().blockStatement());
        ArrayList<methodDecl.parameter> parameters;
        if (ctx.parameters().parameterList() == null) {
            parameters = null;
        } else {
            parameters= (ArrayList<methodDecl.parameter>) values.get(ctx.parameters().parameterList());
        }
        values.put(ctx, new methodDecl(name,returntype,stmt,parameters));
    }

    @Override
    public void exitParameterList(mxParser.ParameterListContext ctx) {
        ArrayList<methodDecl.parameter> pars=new ArrayList<>();
        for (var parctx : ctx.parameter()) {
            pars.add((methodDecl.parameter) values.get(parctx));
        }
        values.put(ctx, pars);
    }

    @Override
    public void exitParameter(mxParser.ParameterContext ctx) {
        values.put(ctx, new methodDecl.parameter((Type) values.get(ctx.typeType()),ctx.IDENTIFIER().toString()));
    }

    @Override
    public void exitBlock(mxParser.BlockContext ctx) {
        values.put(ctx, values.get(ctx.blockStatement()));
    }

    @Override
    public void exitBlockStatement(mxParser.BlockStatementContext ctx) {
        BlockStmt stmt=new BlockStmt();
        for (var stmts : ctx.statement()) {
            stmt.addStatement((Stmt) values.get(stmts));
        }
        values.put(ctx, stmt);
    }

    @Override
    public void exitBlockStmt(mxParser.BlockStmtContext ctx) {
        values.put(ctx,values.get(ctx.block()));
    }

    @Override
    public void exitIfStmt(mxParser.IfStmtContext ctx) {
        Expr cond= (Expr) values.get(ctx.expression());
        Stmt Then= (Stmt) values.get(ctx.statement(0)),Else;
        if (ctx.statement().size() == 1) {
            Else = null;
        } else {
            Else= (Stmt) values.get(ctx.statement(1));
        }

        values.put(ctx,new IfStmt(cond,Then,Else));
    }

    @Override
    public void exitForStmt(mxParser.ForStmtContext ctx) {
        forElement element= (forElement) values.get(ctx.forControl());
        values.put(ctx, new ForStmt(element.init,element.condition,element.incr, (Stmt) values.get(ctx.statement())));
    }
    private static class forElement{
        Expr init,condition,incr;
        forElement(Expr init,Expr condition,Expr incr){
            this.init=init;
            this.incr=incr;
            this.condition=condition;
        }
    }
    @Override
    public void exitForControl(mxParser.ForControlContext ctx) {
        Expr init=null,condition=null,incr=null;
        if (ctx.forinit != null) {
            init = (Expr) values.get(ctx.forinit);
        }
        if (ctx.forcond != null) {
            condition=(Expr)values.get(ctx.forcond);
        }
        if (ctx.forUpdate != null) {
            incr=(Expr)values.get(ctx.forUpdate);
        }
        values.put(ctx, new forElement(init, incr, condition));
    }

    @Override
    public void exitWhileStmt(mxParser.WhileStmtContext ctx) {
        values.put(ctx, new WhileStmt((Expr)values.get(ctx.expression()),(Stmt)values.get(ctx.statement())));
    }

    @Override
    public void exitReturnStmt(mxParser.ReturnStmtContext ctx) {
        if (ctx.expression() != null) {
            values.put(ctx, new ReturnStmt((Expr) values.get(ctx.expression())));
        } else {
            values.put(ctx, new ReturnStmt(null));
        }
    }

    @Override
    public void exitBreakStmt(mxParser.BreakStmtContext ctx) {
        values.put(ctx, new BreakStmt());
    }

    @Override
    public void exitContinueStmt(mxParser.ContinueStmtContext ctx) {
        values.put(ctx, new ContinueStmt());
    }

    @Override
    public void exitSemiStmt(mxParser.SemiStmtContext ctx) {
        values.put(ctx, new Semi());
    }

    @Override
    public void exitExprStmt(mxParser.ExprStmtContext ctx) {
        values.put(ctx, new ExprStmt((Expr) values.get(ctx.expression())));
    }

    @Override
    public void exitVariableDeclStmt(mxParser.VariableDeclStmtContext ctx) {
        values.put(ctx, values.get(ctx.variableDeclaration()));
    }

    @Override
    public void exitPrimaryExpr(mxParser.PrimaryExprContext ctx) {
        values.put(ctx, values.get(ctx.primary()));
    }

    @Override
    public void exitParenthesizedExpr(mxParser.ParenthesizedExprContext ctx) {
        values.put(ctx, values.get(ctx.expression()));
    }

    @Override
    public void exitThisExpr(mxParser.ThisExprContext ctx) {
        values.put(ctx, new ThisExpr());
    }

    @Override
    public void exitLiteralExpr(mxParser.LiteralExprContext ctx) {
        values.put(ctx, values.get(ctx.literal()));
    }

    @Override
    public void exitLiteral(mxParser.LiteralContext ctx) {
        if (ctx.DECIMAL_LITERAL() != null) {
            values.put(ctx, new LiteralExpr(Integer.parseInt(ctx.DECIMAL_LITERAL().toString())));
        } else if (ctx.BOOL_LITERAL() != null) {
            values.put(ctx, new LiteralExpr(ctx.BOOL_LITERAL().toString().equals("true")));
        } else if (ctx.STRING_LITERAL() != null) {
            var str = ctx.STRING_LITERAL().toString();
            values.put(ctx, new LiteralExpr(str.substring(1, str.length() - 1)));
        } else {
            values.put(ctx, new LiteralExpr(null));
        }
    }

    @Override
    public void exitMemberExpr(mxParser.MemberExprContext ctx) {
        Expr instance = (Expr) values.get(ctx.expression());
        String member=ctx.IDENTIFIER().toString();
        values.put(ctx, new MemberExpr(instance,member));
    }

    @Override
    public void exitArrayExpr(mxParser.ArrayExprContext ctx) {
        Expr name=(Expr)values.get(ctx.expression(0));
        Expr index= (Expr) values.get(ctx.expression(1));
        values.put(ctx, new SubscriptorExpr(index,name));
    }

    @Override
    public void exitMethodCallExpr(mxParser.MethodCallExprContext ctx) {
        Expr methodname= (Expr) values.get(ctx.expression());
        ArrayList<Expr> arguments=new ArrayList<>();
        if (ctx.expressionList() != null) {
            arguments = (ArrayList<Expr>) values.get(ctx.expressionList());
        }
        values.put(ctx, new MethodCallExpr(methodname,arguments));
    }

    @Override
    public void exitExpressionList(mxParser.ExpressionListContext ctx) {
        ArrayList<Expr> arguments=new ArrayList<>();
        for (var expr : ctx.expression()) {
            arguments.add((Expr) values.get(expr));
        }
        values.put(ctx, arguments);
    }

    @Override
    public void exitNewExpr(mxParser.NewExprContext ctx) {
        values.put(ctx, values.get(ctx.creator()));
    }

    @Override
    public void exitPostfixExpr(mxParser.PostfixExprContext ctx) {
        values.put(ctx, new PostfixStmt((Expr)values.get(ctx.expression()),ctx.postfix.toString()));
    }

    @Override
    public void exitPrefixExpr(mxParser.PrefixExprContext ctx) {
        values.put(ctx,new PrefixStmt((Expr)values.get(ctx.expression()), ctx.prefix.toString()));
    }

    @Override
    public void exitBinaryOpExpr(mxParser.BinaryOpExprContext ctx) {
        if (ctx.bop.toString().equals("=")) {
            values.put(ctx, new AssignmentExpr((Expr)values.get(ctx.expression(0)),(Expr)values.get(ctx.expression(1))));
        } else if (ctx.bop.toString().equals("||")) {
            values.put(ctx,new LogicOrExpr((Expr)values.get(ctx.expression(0)),(Expr)values.get(ctx.expression(1))));
        } else if (ctx.bop.toString().equals("&&")) {
            values.put(ctx, new LogicAndExpr((Expr) values.get(ctx.expression(0)), (Expr) values.get(ctx.expression(1))));
        } else {
            values.put(ctx, new InfixExpr((Expr)values.get(ctx.expression(0)),(Expr)values.get(ctx.expression(1)),ctx.bop.toString()));
        }
    }

    @Override
    public void exitNameExpr(mxParser.NameExprContext ctx) {
        values.put(ctx, new NameExpr(ctx.IDENTIFIER().toString()));
    }

    @Override
    public void exitArrayCreator(mxParser.ArrayCreatorContext ctx) {
        String typename;
        ArrayList<Expr> dims=new ArrayList<>();
        if (ctx.classType() != null) {
            typename = (String) values.get(ctx.classType());
        } else {
            typename=(String) values.get(ctx.primitiveType());
        }
        for (var expr : ctx.expression()) {
            dims.add((Expr) values.get(expr));
        }
        int totDim=(ctx.getChildCount()-1-ctx.expression().size())/2;
        values.put(ctx, new NewExpr(typename,totDim,dims));
    }

    @Override
    public void exitConstructorCreator(mxParser.ConstructorCreatorContext ctx) {
        String typename;
        ArrayList<Expr> dims=new ArrayList<>();
        if (ctx.classType() != null) {
            typename = (String) values.get(ctx.classType());
        } else {
            typename=(String) values.get(ctx.primitiveType());
        }
        int totDim=0;
        values.put(ctx, new NewExpr(typename,totDim,dims));
    }

    @Override
    public void exitParameters(mxParser.ParametersContext ctx) {
        if (ctx.parameterList() != null) {
            values.put(ctx, values.get(ctx.parameterList()));
        } else {
            values.put(ctx, new ArrayList<methodDecl.parameter>());
        }
    }

}
