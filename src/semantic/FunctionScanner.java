package semantic;

import ast.*;
import frontend.ASTVisitor;

import java.util.ArrayList;

public class FunctionScanner implements ASTVisitor {
    private SymbolTable<SemanticType> typeTable;
    private SymbolTable<NameEntry> valTable;
    public FunctionScanner(SymbolTable<SemanticType> typeTable, SymbolTable<NameEntry> valTable, Node startNode)throws TypeChecker.semanticException {
        this.typeTable=typeTable;
        this.valTable=valTable;
        preprocessTypeTable();
        preprocessBuiltinMethods();
        visit(startNode);
        NameEntry main=valTable.lookup("main");
        if (main == null || main instanceof VarEntry || !((FuncEntry)main).returnType.isIntType()) {
            throw new TypeChecker.semanticException("no main function");
        }
    }
    private void preprocessTypeTable() throws TypeChecker.semanticException {
        for (var entry : typeTable) {
            if (entry.isRecordType()) {
                for (int i=0;i<((RecordType) entry).getFieldName().size();i++) {
                    var fieldName=((RecordType) entry).getFieldName();
                    var fieldType=((RecordType) entry).getFieldType();
                    if (((RecordType) entry).getFieldType().get(i).isNameType()) {
                        var bindType=typeTable.lookup(fieldName.get(i));
                        if (bindType == null) {
                            throw new TypeChecker.semanticException("type not declared");
                        }
                        ((NameType) fieldType.get(i)).bind(bindType);
                    }
                }
            }
        }
    }
    private SemanticType getbasicType(SemanticType.kind kind)  {
        switch (kind){
            case nil:
                return new NullType();
            case bool:
                return new BoolType();
            case string:
                return new StringType();
            case integer:
                return new IntType();
            case voidType:
                return new VoidType();
            default:
                return null;
        }
    }
    //used to add library functions
    private void addSingleParamFunc(SemanticType.kind paramKind, SemanticType.kind returnKind, String name) throws TypeChecker.semanticException {
        ArrayList<SemanticType> params=new ArrayList<>();
        if(paramKind!= SemanticType.kind.nil) {
            params.add(getbasicType(paramKind));
        }
        valTable.enter(name,new FuncEntry(getbasicType(returnKind),params));
    }
    private void  addDoubleParamFunc(SemanticType.kind paramKind1,SemanticType.kind paramKind2,SemanticType.kind returnKind,String name) throws TypeChecker.semanticException {
        ArrayList<SemanticType>params=new ArrayList<>();
        params.add(getbasicType(paramKind1));
        params.add(getbasicType(paramKind2));
        valTable.enter(name,new FuncEntry(getbasicType(returnKind),params));
    }
    private void preprocessBuiltinMethods() throws TypeChecker.semanticException {
        addSingleParamFunc(SemanticType.kind.nil, SemanticType.kind.integer,"[]@size");
        addSingleParamFunc(SemanticType.kind.string, SemanticType.kind.voidType,"print");
        addSingleParamFunc(SemanticType.kind.string, SemanticType.kind.voidType,"println");
        addSingleParamFunc(SemanticType.kind.integer, SemanticType.kind.voidType,"printInt");
        addSingleParamFunc(SemanticType.kind.integer, SemanticType.kind.voidType,"printlnInt");
        addSingleParamFunc(SemanticType.kind.nil, SemanticType.kind.string,"getString");
        addSingleParamFunc(SemanticType.kind.nil, SemanticType.kind.integer,"getInt");
        addSingleParamFunc(SemanticType.kind.integer, SemanticType.kind.string,"toString");
        addSingleParamFunc(SemanticType.kind.nil, SemanticType.kind.integer,"string@length");
        addDoubleParamFunc(SemanticType.kind.integer, SemanticType.kind.integer, SemanticType.kind.string,"string@substring");
        addSingleParamFunc(SemanticType.kind.nil, SemanticType.kind.integer,"string@parseInt");
        addSingleParamFunc(SemanticType.kind.integer, SemanticType.kind.integer,"string@ord");
    }

    @Override
    public Object visitAssignmentExpr(AssignmentExpr node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitBlockStmt(BlockStmt node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitBreakStmt(BreakStmt node) throws TypeChecker.semanticException {
        return null;
    }
    private SemanticType convert(ast.Type type){
        if (type.getDims() > 0) {
            return new ArrayType(typeTable.lookup(type.getTypename()),type.getDims());
        } else {
            return typeTable.lookup(type.getTypename());
        }
    }
    @Override
    public Object visitClassDecl(ClassDecl node) throws TypeChecker.semanticException {
        for (var method : node.getMethods()) {
            ArrayList<SemanticType> convertedParamTypes = new ArrayList<>();
            for (var param : method.getParameters()) {
                convertedParamTypes.add(convert(param.getType()));
            }
            var convertedReturnType=convert(method.getReturnType());
            method.setSemanticReturnType(convertedReturnType);
            method.setSemanticParamTypes(convertedParamTypes);
            valTable.enter(node.getName() + "@" + method.getName(), new FuncEntry(convertedReturnType, convertedParamTypes));
        }
        return null;
    }

    @Override
    public Object visitCompilationUnit(CompilationUnit node) throws TypeChecker.semanticException {
        for (var son : node.getDeclarations()) {
            if (!(son instanceof VariableDeclStmt)) {
                visit(son);
            }
        }
        return null;
    }

    @Override
    public Object visitContinueStmt(ContinueStmt node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitExprStmt(ExprStmt node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitForStmt(ForStmt node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitIfStmt(IfStmt node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitInfixExpr(InfixExpr node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitLiteralExpr(LiteralExpr node) {
        return null;
    }

    @Override
    public Object visitLogicAndExpr(LogicAndExpr node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitLogicOrExpr(LogicOrExpr node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitMemberExpr(MemberExpr node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitMethodCallExpr(MethodCallExpr node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitMethodDecl(MethodDecl node) throws TypeChecker.semanticException {
        ArrayList<SemanticType> paramTypes=new ArrayList<>();
        for (var param : node.getParameters()) {
            paramTypes.add(convert(param.getType()));
        }
        var convertedReturnType=convert(node.getReturnType());
        node.setSemanticParamTypes(paramTypes);
        node.setSemanticReturnType(convertedReturnType);
        valTable.enter(node.getName(), new FuncEntry(convertedReturnType,paramTypes));
        return null;
    }

    @Override
    public Object visitNameExpr(NameExpr node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitNewExpr(NewExpr node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitPostfixExpr(PostfixExpr node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitPrefixExpr(PrefixExpr node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitReturnStmt(ReturnStmt node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitSemi(Semi node) {
        return null;
    }

    @Override
    public Object visitSubscriptorExpr(SubscriptorExpr node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitThisExpr(ThisExpr node) {
        return null;
    }

    @Override
    public Object visitVariableDeclStmt(VariableDeclStmt node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visitWhileStmt(WhileStmt node) throws TypeChecker.semanticException {
        return null;
    }

    @Override
    public Object visit(Node node) throws TypeChecker.semanticException {
        return node.accept(this);
    }
}
