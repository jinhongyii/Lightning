// Generated from C:/Users/jinho/IdeaProjects/Compiler2020/src/parser\mx.g4 by ANTLR 4.7.2
package parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link mxParser}.
 */
public interface mxListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link mxParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(mxParser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(mxParser.CompilationUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(mxParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(mxParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(mxParser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(mxParser.ClassBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassBodyDeclaration(mxParser.ClassBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassBodyDeclaration(mxParser.ClassBodyDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclaration(mxParser.MethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclaration(mxParser.MethodDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(mxParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(mxParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#variableDecorator}.
	 * @param ctx the parse tree
	 */
	void enterVariableDecorator(mxParser.VariableDecoratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#variableDecorator}.
	 * @param ctx the parse tree
	 */
	void exitVariableDecorator(mxParser.VariableDecoratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#typeType}.
	 * @param ctx the parse tree
	 */
	void enterTypeType(mxParser.TypeTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#typeType}.
	 * @param ctx the parse tree
	 */
	void exitTypeType(mxParser.TypeTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#typeTypeOrVoid}.
	 * @param ctx the parse tree
	 */
	void enterTypeTypeOrVoid(mxParser.TypeTypeOrVoidContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#typeTypeOrVoid}.
	 * @param ctx the parse tree
	 */
	void exitTypeTypeOrVoid(mxParser.TypeTypeOrVoidContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#classType}.
	 * @param ctx the parse tree
	 */
	void enterClassType(mxParser.ClassTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#classType}.
	 * @param ctx the parse tree
	 */
	void exitClassType(mxParser.ClassTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(mxParser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(mxParser.PrimitiveTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#funcDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFuncDeclaration(mxParser.FuncDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#funcDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFuncDeclaration(mxParser.FuncDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterParameters(mxParser.ParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitParameters(mxParser.ParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(mxParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(mxParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(mxParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(mxParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(mxParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(mxParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatement(mxParser.BlockStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatement(mxParser.BlockStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code blockStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStmt(mxParser.BlockStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code blockStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStmt(mxParser.BlockStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfStmt(mxParser.IfStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfStmt(mxParser.IfStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterForStmt(mxParser.ForStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitForStmt(mxParser.ForStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whileStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStmt(mxParser.WhileStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whileStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStmt(mxParser.WhileStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code returnStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStmt(mxParser.ReturnStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code returnStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStmt(mxParser.ReturnStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStmt(mxParser.BreakStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStmt(mxParser.BreakStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStmt(mxParser.ContinueStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStmt(mxParser.ContinueStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code semiStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterSemiStmt(mxParser.SemiStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code semiStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitSemiStmt(mxParser.SemiStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExprStmt(mxParser.ExprStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExprStmt(mxParser.ExprStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code variableDeclStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclStmt(mxParser.VariableDeclStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code variableDeclStmt}
	 * labeled alternative in {@link mxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclStmt(mxParser.VariableDeclStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNewExpr(mxParser.NewExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNewExpr(mxParser.NewExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPrefixExpr(mxParser.PrefixExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPrefixExpr(mxParser.PrefixExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code methodCallExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMethodCallExpr(mxParser.MethodCallExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code methodCallExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMethodCallExpr(mxParser.MethodCallExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code primaryExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpr(mxParser.PrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code primaryExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpr(mxParser.PrimaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterArrayExpr(mxParser.ArrayExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitArrayExpr(mxParser.ArrayExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code memberExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMemberExpr(mxParser.MemberExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code memberExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMemberExpr(mxParser.MemberExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryOpExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinaryOpExpr(mxParser.BinaryOpExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryOpExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinaryOpExpr(mxParser.BinaryOpExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code postfixExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExpr(mxParser.PostfixExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code postfixExpr}
	 * labeled alternative in {@link mxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExpr(mxParser.PostfixExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#forControl}.
	 * @param ctx the parse tree
	 */
	void enterForControl(mxParser.ForControlContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#forControl}.
	 * @param ctx the parse tree
	 */
	void exitForControl(mxParser.ForControlContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(mxParser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(mxParser.ExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenthesizedExpr}
	 * labeled alternative in {@link mxParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterParenthesizedExpr(mxParser.ParenthesizedExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenthesizedExpr}
	 * labeled alternative in {@link mxParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitParenthesizedExpr(mxParser.ParenthesizedExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code thisExpr}
	 * labeled alternative in {@link mxParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterThisExpr(mxParser.ThisExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code thisExpr}
	 * labeled alternative in {@link mxParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitThisExpr(mxParser.ThisExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literalExpr}
	 * labeled alternative in {@link mxParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterLiteralExpr(mxParser.LiteralExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literalExpr}
	 * labeled alternative in {@link mxParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitLiteralExpr(mxParser.LiteralExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nameExpr}
	 * labeled alternative in {@link mxParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterNameExpr(mxParser.NameExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nameExpr}
	 * labeled alternative in {@link mxParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitNameExpr(mxParser.NameExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link mxParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(mxParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link mxParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(mxParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code errorCreator}
	 * labeled alternative in {@link mxParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterErrorCreator(mxParser.ErrorCreatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code errorCreator}
	 * labeled alternative in {@link mxParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitErrorCreator(mxParser.ErrorCreatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayCreator}
	 * labeled alternative in {@link mxParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterArrayCreator(mxParser.ArrayCreatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayCreator}
	 * labeled alternative in {@link mxParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitArrayCreator(mxParser.ArrayCreatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code constructorCreator}
	 * labeled alternative in {@link mxParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterConstructorCreator(mxParser.ConstructorCreatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code constructorCreator}
	 * labeled alternative in {@link mxParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitConstructorCreator(mxParser.ConstructorCreatorContext ctx);
}