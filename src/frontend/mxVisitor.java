package frontend;
// Generated from C:/Users/jinho/IdeaProjects/Compiler2020/src/frontend\mx.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link mxParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface mxVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link mxParser#compilationUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompilationUnit(mxParser.CompilationUnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#classDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDeclaration(mxParser.ClassDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#classBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassBody(mxParser.ClassBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassBodyDeclaration(mxParser.ClassBodyDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#methodDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodDeclaration(mxParser.MethodDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#variableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaration(mxParser.VariableDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#variableDecorator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDecorator(mxParser.VariableDecoratorContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#typeType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeType(mxParser.TypeTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#typeTypeOrVoid}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeTypeOrVoid(mxParser.TypeTypeOrVoidContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#classType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassType(mxParser.ClassTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#primitiveType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveType(mxParser.PrimitiveTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#funcDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncDeclaration(mxParser.FuncDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#parameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameters(mxParser.ParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#parameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterList(mxParser.ParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(mxParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(mxParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#blockStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockStatement(mxParser.BlockStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(mxParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(mxParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#methodCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodCall(mxParser.MethodCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#forControl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForControl(mxParser.ForControlContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#forInit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForInit(mxParser.ForInitContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#expressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionList(mxParser.ExpressionListContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(mxParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(mxParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link mxParser#creator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreator(mxParser.CreatorContext ctx);
}