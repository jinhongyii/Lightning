package frontend;

import ast.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import parser.mxBaseListener;
import parser.mxParser;
import semantic.*;

import java.util.ArrayList;

public class ASTBuilder extends mxBaseListener {
    private ParseTreeProperty<Object> values = new ParseTreeProperty<>();
    private CompilationUnit compilationUnit=new CompilationUnit();
    private SymbolTable<SemanticType> typeTable ;
    public CompilationUnit getASTStartNode(){return compilationUnit;}
    public ASTBuilder(SymbolTable<SemanticType> typeTable) throws TypeChecker.semanticException {
        this.typeTable=typeTable;
        typeTable.enter("int",new IntType());
        typeTable.enter("string", new StringType());
        typeTable.enter("void",new VoidType());
        typeTable.enter("bool", new BoolType());
    }
    @Override
    public void exitCompilationUnit(mxParser.CompilationUnitContext ctx) {
        for (var sonctx : ctx.getRuleContexts(ParserRuleContext.class)) {
            var tmp=values.get(sonctx);
            if (tmp instanceof ArrayList) {
                for (Object decl : (ArrayList) tmp) {
                    compilationUnit.addDeclarations((Node) decl);
                }
            } else {
                compilationUnit.addDeclarations(((Node)tmp));
            }
        }

    }

    @Override
    public void exitClassDeclaration(mxParser.ClassDeclarationContext ctx) {
        var classDeclaration=new ClassDecl(ctx.IDENTIFIER().getText());
        var record=new RecordType(ctx.IDENTIFIER().getText());

        for(var i:ctx.classBody().classBodyDeclaration()){
            Object decl=values.get(i);
            if(decl instanceof  ArrayList){
                for(var j: (ArrayList) decl){
                    classDeclaration.add((Node)j);
                    VariableDeclStmt stmt=(VariableDeclStmt)j;
                    String typename=stmt.getType().getTypename();
                    SemanticType semanticType =typeTable.lookup(typename);
                    if(semanticType !=null){
                        if(stmt.getType().getDims()!=0) {
                            semanticType = new ArrayType(semanticType, stmt.getType().getDims());
                        }
                        record.addRecord(stmt.getName(), semanticType);
                    }else {
                        if (stmt.getType().getDims() != 0) {
                            record.addRecord(stmt.getName(), new ArrayType(new NameType(typename), stmt.getType().getDims()));
                        } else {
                            record.addRecord(stmt.getName(), new NameType(typename));
                        }
                    }
                }
            }else {
                classDeclaration.add((Node) decl);
            }
        }

        try {
            typeTable.enter(ctx.IDENTIFIER().getText(),record);
            classDeclaration.setSemanticType(record);
        } catch (TypeChecker.semanticException e) {
            e.printStackTrace();
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
        String name=ctx.IDENTIFIER().getText();
        BlockStmt stmt= (BlockStmt) values.get(ctx.block().blockStatement());
        if (stmt.getStatements().isEmpty()|| !(stmt.getStatements().get(stmt.getStatements().size() - 1) instanceof ReturnStmt) &&( returntype==null || returntype.getTypename().equals("void") || name.equals("main"))) {
            stmt.addStatement(new ReturnStmt(null));
        }
        ArrayList<MethodDecl.parameter> parameters;
        if (ctx.parameters().parameterList() == null) {
            parameters = new ArrayList<>();
        } else {
            parameters= (ArrayList<MethodDecl.parameter>) values.get(ctx.parameters().parameterList());
        }
        values.put(ctx, new MethodDecl(name,returntype,stmt,parameters));
    }

    static class Variable {
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
            values.put(ctx, new Variable(ctx.IDENTIFIER().getText(), (Expr) values.get(ctx.expression())));
        } else {
            values.put(ctx, new Variable(ctx.IDENTIFIER().getText(), null));
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
        values.put(ctx, new String(ctx.IDENTIFIER().getText()));
    }

    @Override
    public void exitPrimitiveType(mxParser.PrimitiveTypeContext ctx) {
        if (ctx.BOOL() != null) {
            values.put(ctx, ctx.BOOL().getText());
        } else if (ctx.INT() != null) {
            values.put(ctx, ctx.INT().getText());
        } else {
            values.put(ctx, ctx.STRING().getText());
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
        String name=ctx.IDENTIFIER().getText();
        BlockStmt stmt= (BlockStmt) values.get(ctx.block().blockStatement());
        if ((stmt.getStatements().isEmpty()|| !(stmt.getStatements().get(stmt.getStatements().size() - 1) instanceof ReturnStmt)) && (returntype.getTypename().equals("void") || name.equals("main"))) {
            stmt.addStatement(new ReturnStmt(name.equals("main")?new LiteralExpr(0):null));
        }
        ArrayList<MethodDecl.parameter> parameters;
        if (ctx.parameters().parameterList() == null) {
            parameters = new ArrayList<>();
        } else {
            parameters= (ArrayList<MethodDecl.parameter>) values.get(ctx.parameters().parameterList());
        }
        values.put(ctx, new MethodDecl(name,returntype,stmt,parameters));
    }

    @Override
    public void exitParameterList(mxParser.ParameterListContext ctx) {
        ArrayList<MethodDecl.parameter> pars=new ArrayList<>();
        for (var parctx : ctx.parameter()) {
            pars.add((MethodDecl.parameter) values.get(parctx));
        }
        values.put(ctx, pars);
    }

    @Override
    public void exitParameter(mxParser.ParameterContext ctx) {
        values.put(ctx, new MethodDecl.parameter((Type) values.get(ctx.typeType()),ctx.IDENTIFIER().getText()));
    }

    @Override
    public void exitBlock(mxParser.BlockContext ctx) {
        values.put(ctx, values.get(ctx.blockStatement()));
    }

    @Override
    public void exitBlockStatement(mxParser.BlockStatementContext ctx) {
        BlockStmt stmt=new BlockStmt();
        for (var stmts : ctx.statement()) {
            var i=values.get(stmts);
            if (i instanceof ArrayList) {
                for (var ele : (ArrayList)i) {
                    stmt.addStatement((Stmt)ele);
                }
            }else {
                stmt.addStatement((Stmt)i);
            }
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
        Stmt Then= getRealStmt(values.get(ctx.statement(0))),Else;
        if (ctx.statement().size() == 1) {
            Else = null;
        } else {
            Else= getRealStmt( values.get(ctx.statement(1)));
        }

        values.put(ctx,new IfStmt(cond,Then,Else));
    }

    @Override
    public void exitForStmt(mxParser.ForStmtContext ctx) {
        forElement element= (forElement) values.get(ctx.forControl());
        var body=values.get(ctx.statement());
        Stmt newBody;
        newBody = getRealStmt(body);
        values.put(ctx, new ForStmt(element.init, element.condition, element.incr,newBody));

    }


    public Stmt getRealStmt(Object body) {
        Stmt newBody;
        if(body instanceof Stmt) {
            newBody= (Stmt) body;
        } else if (body instanceof ArrayList) {
            newBody = new BlockStmt();
            for (var varDecl : (ArrayList) body) {
                ((BlockStmt) newBody).addStatement((Stmt) varDecl);
            }
        } else {
            newBody=null;
        }
        return newBody;
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
        values.put(ctx, new forElement(init, condition, incr));
    }

    @Override
    public void exitWhileStmt(mxParser.WhileStmtContext ctx) {
        values.put(ctx, new WhileStmt((Expr)values.get(ctx.expression()),getRealStmt(values.get(ctx.statement()))));
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
            values.put(ctx, new LiteralExpr(Integer.parseInt(ctx.DECIMAL_LITERAL().getText())));
        } else if (ctx.BOOL_LITERAL() != null) {
            values.put(ctx, new LiteralExpr(ctx.BOOL_LITERAL().getText().equals("true")));
        } else if (ctx.STRING_LITERAL() != null) {
            var str = ctx.STRING_LITERAL().getText();
            values.put(ctx, new LiteralExpr(str.substring(1, str.length() - 1)));
        } else {
            values.put(ctx, new LiteralExpr(null));
        }
    }

    @Override
    public void exitMemberExpr(mxParser.MemberExprContext ctx) {
        Expr instance = (Expr) values.get(ctx.expression());
        String member=ctx.IDENTIFIER().getText();
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
        values.put(ctx, new PostfixExpr((Expr)values.get(ctx.expression()),ctx.postfix.getText()));
    }

    @Override
    public void exitPrefixExpr(mxParser.PrefixExprContext ctx) {
        values.put(ctx,new PrefixExpr((Expr)values.get(ctx.expression()), ctx.prefix.getText()));
    }

    @Override
    public void exitBinaryOpExpr(mxParser.BinaryOpExprContext ctx) {
        if (ctx.bop.getText().equals("=")) {
            values.put(ctx, new AssignmentExpr((Expr)values.get(ctx.expression(0)),(Expr)values.get(ctx.expression(1))));
        } else if (ctx.bop.getText().equals("||")) {
            values.put(ctx,new LogicOrExpr((Expr)values.get(ctx.expression(0)),(Expr)values.get(ctx.expression(1))));
        } else if (ctx.bop.getText().equals("&&")) {
            values.put(ctx, new LogicAndExpr((Expr) values.get(ctx.expression(0)), (Expr) values.get(ctx.expression(1))));
        } else {
            values.put(ctx, new InfixExpr((Expr)values.get(ctx.expression(0)),(Expr)values.get(ctx.expression(1)),ctx.bop.getText()));
        }
    }

    @Override
    public void exitNameExpr(mxParser.NameExprContext ctx) {
        values.put(ctx, new NameExpr(ctx.IDENTIFIER().getText()));
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
            values.put(ctx, new ArrayList<MethodDecl.parameter>());
        }
    }

    @Override
    public void exitErrorCreator(mxParser.ErrorCreatorContext ctx) {
        throw new Error("wrong creator");
    }
}
