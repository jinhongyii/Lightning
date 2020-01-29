package semantic;

import ast.*;
import frontend.ASTVisitor;



public class TypeChecker implements ASTVisitor {
    public static class semanticException extends Exception{
        semanticException(String str){
            super(str);
        }
    }
    private SymbolTable<SemanticType> typeTable;
    private SymbolTable<NameEntry> valTable;

    private int inLoop;
    private ClassDecl inclassDecl;
    private MethodDecl inMethodDecl;
    public TypeChecker(SymbolTable<SemanticType> typeTable, SymbolTable<NameEntry> valTable,Node startNode) throws semanticException {
        this.typeTable=typeTable;
        this.valTable=valTable;
        visit(startNode);
    }

    @Override
    public Object visitAssignmentExpr(AssignmentExpr node) throws semanticException {

       if(!(node.getLval() instanceof NameExpr || node.getLval() instanceof MemberExpr
       || node.getLval() instanceof SubscriptorExpr)){
           throw new semanticException("lvalue not assignable");
       }
        var ltype=(SemanticType)visit(node.getLval());
        var rtype=(SemanticType)visit(node.getRval());
        if (!rtype.canAssignTo(ltype)) {
            throw new semanticException("assignment type not match") ;
        }
        return ltype;


    }

    @Override
    public Object visitBlockStmt(BlockStmt node) throws semanticException {
//        valTable.beginScope();
        for (var stmt : node.getStatements()) {
            visit(stmt);
        }
//        valTable.beginScope();
        return null;
    }

    @Override
    public Object visitBreakStmt(BreakStmt node) throws semanticException {
        if (inLoop == 0) {
            throw new semanticException("break not in a loop");
        }
        return null;
    }

    @Override
    public Object visitClassDecl(ClassDecl node) throws semanticException {

        inclassDecl=node;
        for (var vardecl : node.getVariables()) {
            valTable.enter(node.getName()+"@"+vardecl.getName(),new VarEntry(convert(vardecl.getType())));
        }
        for (var method : node.getMethods()) {
            visit(method);
        }
        inclassDecl=null;

        return null;
    }

    @Override
    public Object visitCompilationUnit(CompilationUnit node) throws semanticException {

        for (var son : node.getDeclarations()) {
            visit(son);
        }

        return null;
    }
    @Override
    public Object visitContinueStmt(ContinueStmt node) throws semanticException {
        if (inLoop == 0) {
            throw new semanticException("continue not in a loop");
        }
        return null;
    }

    @Override
    public Object visitExprStmt(ExprStmt node) throws semanticException {
        visit(node.getExpression());
        return null;
    }

    @Override
    public Object visitForStmt(ForStmt node) throws semanticException {
        if (node.getInit() != null) {
            visit(node.getInit());
        }
        if (node.getCondition() != null) {
            var condType=(SemanticType)visit(node.getCondition());
            if (!(condType.isBoolType())) {
                throw new semanticException("for condition type is not bool");
            }
        }
        if (node.getIncr() != null) {
            visit(node.getIncr());
        }
        inLoop++;
        visit(node.getLoopBody());
        inLoop--;
        return null;
    }

    @Override
    public Object visitIfStmt(IfStmt node) throws semanticException {
        var condType=(SemanticType)visit(node.getCondition());
        if (!condType.isBoolType()) {
            throw new semanticException("if condition type not bool");
        }
        visit(node.getThen());
        if (node.getOtherwise() != null) {
            visit(node.getOtherwise());
        }
        return null;
    }
    private SemanticType getInfixExprType(SemanticType ltype, SemanticType rtype, String operator)  {
        switch (operator){
            case "<=": case ">=": case "<": case ">":case "==": case "!=":
                return new BoolType();
            case "-": case "/": case "<<": case ">>": case ">>>": case "&": case "|": case "^": case "%": case "*": case "+":
                return ltype;
            default:
                return null;
        }
    }
    private boolean checkInfix(SemanticType ltype, SemanticType rtype, String operator){
        switch (operator){
            case "-": case "/": case "<<": case ">>": case ">>>": case "&": case "|": case "^": case "%": case "*":
                return ltype.strictComparable(rtype);
            case "<=": case ">=": case "<": case ">": case "+":
                return ltype.mediumComparable(rtype);
            case "==": case "!=":
                return ltype.looseComparable(rtype);
            default:return false;
        }
    }
    @Override
    public Object visitInfixExpr(InfixExpr node) throws semanticException {
        SemanticType ltype= (SemanticType) visit(node.getLoperand());
        SemanticType rtype= (SemanticType) visit(node.getRoperand());
        SemanticType returnType=getInfixExprType(ltype,rtype,node.getOperator());
        if (!checkInfix(ltype,rtype,node.getOperator())) {
            throw new semanticException("infix type not match");
        }
        return returnType;
    }

    @Override
    public Object visitLiteralExpr(LiteralExpr node) {
        if (node.getVal() == null) {
            return new NullType();
        }
        Object val=node.getVal();
        if (val instanceof Integer) {
            return new IntType();
        } else if (val instanceof String) {
            return new StringType();
        } else if (val instanceof Boolean) {
            return new BoolType();
        }
        return null;
    }

    @Override
    public Object visitLogicAndExpr(LogicAndExpr node) throws semanticException {
        SemanticType ltype= (SemanticType) visit(node.getLoperand());
        SemanticType rtype= (SemanticType) visit(node.getRoperand());
        if (!ltype.isBoolType() || !rtype.isBoolType()) {
            throw new semanticException("logic and type not match");
        }
        return ltype;
    }

    @Override
    public Object visitLogicOrExpr(LogicOrExpr node) throws semanticException {
        SemanticType ltype= (SemanticType) visit(node.getLoperand());
        SemanticType rtype= (SemanticType) visit(node.getRoperand());
        if (!ltype.isBoolType() || !rtype.isBoolType()) {
            throw new semanticException("logic or type not match");
        }
        return ltype;
    }

    @Override
    public Object visitMemberExpr(MemberExpr node) throws semanticException {
        SemanticType ltype=(SemanticType) visit(node.getInstance_name());
        node.setInstanceType(ltype);
        NameEntry entry;
        String name;
        if(ltype.isRecordType()){
            name=((RecordType)ltype).getRecordName()+"@"+node.getMember_name();
        }else if(ltype.isArrayType()){
            name="[]@"+node.getMember_name();
        } else if (ltype.isStringType()) {
            name="string@"+node.getMember_name();
        }else {
            throw new semanticException("instance type not match");
        }
        entry=valTable.lookup(name);
        if (entry != null) {
            return entry instanceof VarEntry?((VarEntry) entry).type:entry;
        }
        throw new semanticException("instance type not match");
    }

    @Override
    public Object visitMethodCallExpr(MethodCallExpr node) throws semanticException {
        NameEntry entry= (NameEntry) visit(node.getName());

        if (entry instanceof VarEntry) {
            throw new semanticException("not a method");
        } else {
            FuncEntry funcEntry=(FuncEntry)entry;
            if (funcEntry.params.size() != node.getArguments().size()) {
                throw  new semanticException("param num not match");
            }else {
                for (int i = 0; i < node.getArguments().size(); i++) {
                    if (!visit(node.getArguments().get(i)).equals(funcEntry.params.get(i))) {
                        throw new semanticException("method param type not match");
                    }
                }
                return funcEntry.returnType;
            }
        }
    }

    private SemanticType convert(ast.Type type){
        if (type.getDims() > 0) {
            return new ArrayType(typeTable.lookup(type.getTypename()),type.getDims());
        } else {
            return typeTable.lookup(type.getTypename());
        }
    }
    @Override
    public Object visitMethodDecl(MethodDecl node) throws semanticException {

        inMethodDecl=node;
        valTable.beginScope();
        for (var param : node.getParameters()) {
            valTable.enter(param.getName(), new VarEntry(convert(param.getType())));
        }
        visit(node.getStmt());
        valTable.endScope();
        inMethodDecl=null;

        return null;
    }

    @Override
    public Object visitNameExpr(NameExpr node) throws semanticException {
        var entry = valTable.lookup(node.getName());
        if (entry != null) {
            return entry instanceof VarEntry?((VarEntry) entry).type:entry;
        } else if(inclassDecl!=null){
            var entry2=valTable.lookup(inclassDecl.getName()+"@"+node.getName());
            if (entry2 != null) {
                return entry2 instanceof VarEntry?((VarEntry) entry2).type:entry2;
            }
        }
        throw new semanticException("name not defined");

    }

    @Override
    public Object visitNewExpr(NewExpr node) throws semanticException {
        for(var dim:node.getDims()){
            if (!((SemanticType) visit(dim)).isIntType()) {
                throw new semanticException("dim not int type");
            }
        }
        var newBaseType=typeTable.lookup(node.getTypename());
        node.setSemanticType(newBaseType);
        if (newBaseType == null) {
            throw new semanticException("type not defined");
        }
        if (node.getTotDim() != 0) {
            return new ArrayType(newBaseType, node.getTotDim());
        } else {
            return newBaseType;
        }
    }

    @Override
    public Object visitPostfixExpr(PostfixExpr node) throws semanticException {
        SemanticType type= (SemanticType) visit(node.getVal());
        if (!type.isIntType()) {
            throw new semanticException(node.getOperator() + " not int type");
        } else {
            return type;
        }
    }

    @Override
    public Object visitPrefixExpr(PrefixExpr node) throws semanticException {
        SemanticType type= (SemanticType) visit(node.getVal());
        if (!type.isIntType()) {
            throw new semanticException(node.getOperator() + " not int type");
        } else {
            return type;
        }
    }

    @Override
    public Object visitReturnStmt(ReturnStmt node) throws semanticException {
        SemanticType returnType=new VoidType();
        if(node.getVal()!=null) {
            returnType= (SemanticType) visit(node.getVal());
        }
        if (!returnType.canAssignTo(convert(inMethodDecl.getReturnType()))) {
            throw new semanticException("return wrong type");
        }
        return null;
    }

    @Override
    public Object visitSemi(Semi node) {
        return null;
    }

    @Override
    public Object visitSubscriptorExpr(SubscriptorExpr node) throws semanticException {
        SemanticType mainType= (SemanticType) visit(node.getName());
        SemanticType indexType=(SemanticType)visit(node.getIndex());
        if (!mainType.isArrayType()) {
            throw new semanticException("not an array");
        } else if (!indexType.isIntType()) {
            throw new semanticException("index not int type");
        } else {
            SemanticType type;
            if (((ArrayType) mainType).getDims() == 1) {
                type=((ArrayType)mainType).getElementType();
            }else {
                type= new ArrayType(((ArrayType)mainType).getElementType(),((ArrayType) mainType).getDims()-1);
            }
            node.setElementType(type);
            return type;
        }
    }

    @Override
    public Object visitThisExpr(ThisExpr node) {
        return typeTable.lookup(inclassDecl.getName());
    }

    @Override
    public Object visitVariableDeclStmt(VariableDeclStmt node) throws semanticException {
        SemanticType type=convert(node.getType());
        node.setSemanticType(type);
        valTable.enter(node.getName(), new VarEntry(type));
        if (node.getInit() != null) {
            SemanticType initType = (SemanticType) visit(node.getInit());
            if (!initType.canAssignTo(type)) {
                throw new semanticException("var declare initiation type not match");
            }
        }
        return null;
    }

    @Override
    public Object visitWhileStmt(WhileStmt node) throws semanticException {
        SemanticType condType = (SemanticType) visit(node.getCondition());
        if (!condType.isBoolType()) {
            throw new semanticException("while condition type is not bool");
        }
        inLoop++;
        visit(node.getLoopBody());
        inLoop--;
        return null;
    }

    @Override
    public Object visit(Node node) throws semanticException {
        return node.accept(this);
    }
}
