package backend;

import IR.*;
import IR.Module;
import IR.Type;
import IR.Types.PointerType;
import IR.Types.StructType;
import IR.instructions.*;
import ast.*;
import frontend.ASTVisitor;
import semantic.*;
import semantic.SymbolTable;

import java.util.ArrayList;
import java.util.LinkedList;


public class IRBuilder implements ASTVisitor {
    private Module topModule=new Module();
    private semantic.SymbolTable<SemanticType> typeTable;
    private semantic.SymbolTable<NameEntry> valTable;
    private boolean isglobal;
    private ClassDecl curClass=null;
    private Function curFunc;
    private BasicBlock curBB;
    private BasicBlock curLoopBB;
    private BasicBlock curAfterLoopBB;
    private Function InitializerFunc=new Function(".initializer",topModule,false,Type.theVoidType,new ArrayList<>(),new ArrayList<>());
    boolean lhs=false;

    public Module getTopModule() {
        return topModule;
    }

    public IRBuilder(semantic.SymbolTable<SemanticType> typeTable, SymbolTable<NameEntry> valTable,CompilationUnit compilationUnit) throws TypeChecker.semanticException {
        this.typeTable = typeTable;
        this.valTable=valTable;
        addInitializer();
        initializeSymTab();
        visit(compilationUnit);
        InitializerFunc.getLastBB().addInst(new ReturnInst(null));
        Function main= (Function) topModule.getSymbolTable().get("main");
        main.getEntryBB().addInstToFirst(new CallInst("initializer_func", InitializerFunc,new ArrayList<>()));
    }
    private void addInitializer(){
        topModule.addFunction(InitializerFunc);
        InitializerFunc.addBB("entry");
        curFunc=InitializerFunc;
        curBB=InitializerFunc.getEntryBB();
    }
    private void initializeSymTab(){
        topModule.getSymbolTable().put("int", IR.Type.TheInt64);
        topModule.getSymbolTable().put("char", IR.Type.TheInt8);
        topModule.getSymbolTable().put("bool", IR.Type.TheInt1);
        topModule.getSymbolTable().put("void", IR.Type.theVoidType);
        topModule.getSymbolTable().put("string", new PointerType(Type.TheInt8));
        for (var i : typeTable) {
            if (i.isRecordType()) {
                Type type= convertTypeInitialize(i);
                topModule.addStruct(((RecordType)i).getRecordName(),type);
            }
        }
        topModule.addFunction(convertExternalFunction("print"));
        topModule.addFunction(convertExternalFunction("println"));
        topModule.addFunction(convertExternalFunction("printInt"));
        topModule.addFunction(convertExternalFunction("printlnInt"));
        topModule.addFunction(convertExternalFunction("getString"));
        topModule.addFunction(convertExternalFunction("getInt"));
        topModule.addFunction(convertExternalFunction("toString"));
        topModule.addFunction(convertExternalFunction("string@length"));
        topModule.addFunction(convertExternalFunction("string@substring"));
        topModule.addFunction(convertExternalFunction("string@parseInt"));
        topModule.addFunction(convertExternalFunction("string@ord"));
        topModule.addFunction(convertExternalFunction("[]@size"));
        topModule.addFunction(createMalloc());
        topModule.addFunction(createExternalStringOpFunc("string_add", new PointerType(Type.TheInt8)));
        topModule.addFunction(createExternalStringOpFunc("string_eq", Type.TheInt1));
        topModule.addFunction(createExternalStringOpFunc("string_ne", Type.TheInt1));
        topModule.addFunction(createExternalStringOpFunc("string_lt", Type.TheInt1));
        topModule.addFunction(createExternalStringOpFunc("string_le", Type.TheInt1));
        topModule.addFunction(createExternalStringOpFunc("string_gt", Type.TheInt1));
        topModule.addFunction(createExternalStringOpFunc("string_ge", Type.TheInt1));


    }
    private Function createExternalStringOpFunc(String name,Type returnType){
        ArrayList<Type> paramType=new ArrayList<>();
        paramType.add(new PointerType(Type.TheInt8));
        paramType.add(new PointerType(Type.TheInt8));
        ArrayList<String>paramNames=new ArrayList<>();
        paramNames.add("lhs");
        paramNames.add("rhs");
        return new Function(name, topModule,true,returnType,paramType,paramNames);
    }
    private Function createMalloc(){
        ArrayList<Type> paramType=new ArrayList<>();
        paramType.add(Type.TheInt64);
        ArrayList<String>paramNames=new ArrayList<>();
        paramNames.add("size");
        return new Function("malloc", topModule, true, new PointerType(Type.TheInt8), paramType,paramNames);
    }
    private Function convertExternalFunction(String name){
        FuncEntry funcEntry= (FuncEntry) valTable.lookup(name);
        ArrayList<Type> paramTypes=new ArrayList<>();
        ArrayList<String> paramNames=new ArrayList<>();
        int cnt=0;
        for (var semanticType : funcEntry.getParams()) {
            paramTypes.add(convertTypeLookUp(semanticType));
            paramNames.add("arg"+cnt);
            cnt++;
        }
        if (name.contains("@")) {
            var className=name.split("@")[0];
            var methodName=name.split("@")[1];
            if (className.equals("[]")) {
                paramTypes.add(new PointerType(Type.TheInt8));
                className="_array";
            }else {
                if (className.equals("string")) {
                    paramTypes.add(new PointerType(Type.TheInt8));
                }else {
                    paramTypes.add(new PointerType(convertTypeLookUp(typeTable.lookup(className))));
                }
            }
            name=className+"_"+methodName;
            paramNames.add("thisptr");
        }
        return new Function(name,topModule,true,convertTypeLookUp(funcEntry.getReturnType()), paramTypes,paramNames);
    }

    //string can not  malloc
    private int getRecordMallocSize(SemanticType type) {
        if (type.actual().isIntType()) {
            return 8;
        } else if(type.actual().isBoolType()){
            return 1;
        }  else if (type.actual().isRecordType()) {
            int tot = 0;
            for (var i : ((RecordType) type.actual()).getFieldType()) {
                tot +=i.isBoolType()?1:8;
            }
            return tot;
        } else {
            return 0;
        }
    }
    private int getArrayMallocSize(SemanticType type,int size){
        ArrayType arrayType=(ArrayType)type;
        return (arrayType.getRealElementType().actual().isBoolType()?1:8)*size+8;
    }
    private Type convertTypeInitialize(SemanticType type) {
        if (type.actual().isIntType()) {
            return Type.TheInt64;
        }else if(type.actual().isVoidType()){
            return Type.theVoidType;
        } else if (type.actual().isRecordType()) {
            ArrayList<Type> tmp=new ArrayList<>();
            for (var i : ((RecordType) type.actual()).getFieldType()) {
                tmp.add(convertTypeInitialize(i));
            }
            return new StructType(tmp);
        } else if (type.actual().isStringType()) {
            return new PointerType(Type.TheInt8);
        } else if (type.actual().isArrayType()) {
            return new PointerType(convertTypeInitialize(((ArrayType)type.actual()).getElementType()));
        } else if (type.actual().isNullType()) {
            return new PointerType(Type.theVoidType);
        } else if (type.actual().isBoolType()) {
            return Type.TheInt1;
        }
        return null;
    }
    private Type convertTypeLookUp(SemanticType type){
        if (type.actual().isIntType()) {
            return Type.TheInt64;
        } else if (type.actual().isBoolType()) {
            return Type.TheInt1;
        } else if (type.actual().isVoidType()) {
            return Type.theVoidType;
        } else if (type.actual().isNullType()) {
            return new PointerType(Type.theVoidType);
        } else if(type.actual().isRecordType()){
            return (Type) topModule.getSymbolTable().get(((RecordType)type).getRecordName());
        } else if (type.actual().isArrayType()) {
            var eleType=convertTypeLookUp(((ArrayType) type.actual()).getElementType());
            for (int i = 0; i < ((ArrayType) type.actual()).getDims();i++) {
                eleType=new PointerType(eleType);
            }
            return eleType;
        } else if(type.actual().isStringType()){
            return new PointerType(Type.TheInt8);
        }
        return null;
    }
    @Override
    public Object visitAssignmentExpr(AssignmentExpr node) throws TypeChecker.semanticException {
        Value rhsV= (Value) visit(node.getRval());
        lhs=true;
        Value lhsV=(Value) visit(node.getLval());
        lhs=false;
        curBB.addInst(new StoreInst(rhsV,lhsV ));
        return null;
    }

    @Override
    public Object visitBlockStmt(BlockStmt node) throws TypeChecker.semanticException {
        for (var stmt : node.getStatements()) {
            visit(stmt);
        }
        return null;
    }

    @Override
    public Object visitBreakStmt(BreakStmt node) throws TypeChecker.semanticException {
        curBB.addInst(new BranchInst(curAfterLoopBB, null,null));
        return null;
    }

    @Override
    public Object visitClassDecl(ClassDecl node) throws TypeChecker.semanticException {
        curClass= node;
        for (var decl : node.getMethods()) {
            visit(decl);
        }
        curClass=null;
        return null;
    }

    @Override
    public Object visitCompilationUnit(CompilationUnit node) throws TypeChecker.semanticException {
        isglobal=true;
        for (var decl : node.getDeclarations()) {
            if (decl instanceof VariableDeclStmt) {
                visit(decl);
            }
        }
        isglobal=false;
        for (var decl : node.getDeclarations()) {
            if(!(decl instanceof VariableDeclStmt)){
                visit(decl);
            }
        }
        return null;
    }

    @Override
    public Object visitContinueStmt(ContinueStmt node) throws TypeChecker.semanticException {
        curBB.addInst(new BranchInst(curLoopBB,null,null));
        return null;
    }

    @Override
    public Object visitExprStmt(ExprStmt node) throws TypeChecker.semanticException {
        visit(node.getExpression());
        return null;
    }

    @Override
    public Object visitForStmt(ForStmt node) throws TypeChecker.semanticException {
        BasicBlock condBB=curFunc.addBB("for_cond");
        BasicBlock stepBB=new BasicBlock("for_step");
        BasicBlock loopBB=new BasicBlock("for_loop");
        BasicBlock afterLoopBB=new BasicBlock("for_afterLoop");
        if (node.getInit() != null) {
            visit(node.getInit());
        }
        curBB.addInst(new BranchInst(condBB, null,null ));
        curBB=condBB;
        Value condV;
        if (node.getCondition() != null) {
            condV = (Value) visit(node.getCondition());
            condBB.addInst(new BranchInst(loopBB, afterLoopBB, condV));
        } else {
            condBB.addInst(new BranchInst(loopBB,null,null));
        }
        var prevLoopBB=curLoopBB;
        var prevAfterLoopBB=curAfterLoopBB;
        curLoopBB=loopBB;
        curAfterLoopBB=afterLoopBB;
        curFunc.addBB(loopBB);
        curBB=loopBB;
        visit(node.getLoopBody());
        curBB.addInst(new BranchInst(stepBB,null,null));
        curFunc.addBB(stepBB);
        curBB=stepBB;
        if (node.getIncr() != null) {
            visit(node.getIncr());
        }
        curBB.addInst(new BranchInst(condBB,null,null));
        curLoopBB=prevLoopBB;
        curAfterLoopBB=prevAfterLoopBB;
        curFunc.addBB(afterLoopBB);
        curBB=afterLoopBB;
        return null;
    }

    @Override
    public Object visitIfStmt(IfStmt node) throws TypeChecker.semanticException {
        Value condV= (Value) visit(node.getCondition());
        var ThenBB=curFunc.addBB("if_then");
        var ElseBB=new BasicBlock("if_else");
        var MergeBB=new BasicBlock("if_cont");
        curBB.addInst(new BranchInst( ThenBB, ElseBB, condV));
        curBB=ThenBB;
        visit(node.getThen());
        curBB.addInst(new BranchInst( MergeBB, null, null));
        curFunc.addBB(ElseBB);
        curBB=ElseBB;
        if (node.getOtherwise() != null) {
            visit(node.getOtherwise());
        }
        curBB.addInst(new BranchInst(MergeBB, null, null));
        curFunc.addBB(MergeBB);
        curBB=MergeBB;
        return null;
    }

    @Override
    public Object visitInfixExpr(InfixExpr node) throws TypeChecker.semanticException {
        Value lhsV= (Value) visit(node.getLoperand());
        Value rhsV= (Value) visit(node.getRoperand());
        Instruction.Opcode opcode;
        Instruction opInst;
        if (lhsV.getType().getId() == Type.TypeID.PointerType) {
            Function function;
            String funcName="";
            switch (node.getOperator()) {
                case "<=":
                    funcName="string_le";
                    break;
                case"<":
                    funcName="string_lt";
                    break;
                case ">":
                    funcName="string_gt";
                    break;
                case ">=":
                    funcName="string_ge";
                    break;
                case "==":
                    funcName="string_eq";
                    break;
                case "!=":
                    funcName="string_ne";
                    break;
                case "+":
                    funcName="string_add";
            }
            function= (Function) topModule.getSymbolTable().get(funcName);
            ArrayList<Value> params=new ArrayList<>();
            params.add(lhsV);
            params.add(rhsV);
            opInst=new CallInst("stringop", function, params);
        }else {
            boolean isCmp = false;
            switch (node.getOperator()) {
                case "<=":
                    opcode = Instruction.Opcode.LE;
                    isCmp = true;
                    break;
                case ">=":
                    opcode = Instruction.Opcode.GE;
                    isCmp = true;
                    break;
                case "<":
                    opcode = Instruction.Opcode.LT;
                    isCmp = true;
                    break;
                case ">":
                    opcode = Instruction.Opcode.GT;
                    isCmp = true;
                    break;
                case "==":
                    opcode = Instruction.Opcode.EQ;
                    isCmp = true;
                    break;
                case "!=":
                    opcode = Instruction.Opcode.NE;
                    isCmp = true;
                    break;
                case "-":
                    opcode = Instruction.Opcode.sub;
                    break;
                case "/":
                    opcode = Instruction.Opcode.div;
                    break;
                case "<<":
                    opcode = Instruction.Opcode.shl;
                    break;
                case ">>":
                    opcode = Instruction.Opcode.shr;
                    break;
                case "&":
                    opcode = Instruction.Opcode.and;
                    break;
                case "|":
                    opcode = Instruction.Opcode.or;
                    break;
                case "^":
                    opcode = Instruction.Opcode.xor;
                    break;
                case "%":
                    opcode = Instruction.Opcode.rem;
                    break;
                case "*":
                    opcode = Instruction.Opcode.mul;
                    break;
                case "+":
                    opcode = Instruction.Opcode.add;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + node.getOperator());
            }
            if (isCmp) {
                opInst = new IcmpInst("cmp", opcode, lhsV, rhsV);
            } else {
                opInst = new BinaryOpInst("cmp", opcode, lhsV, rhsV);
            }
        }
        curBB.addInst(opInst);
        return opInst;
    }

    @Override
    public Object visitLiteralExpr(LiteralExpr node) {
        if (node.getVal() instanceof Boolean) {
            return new ConstantBool((Boolean) node.getVal());
        } else if(node.getVal() instanceof Integer) {
            return new ConstantInt((Integer)node.getVal());
        } else if (node.getVal() instanceof String) {
            var globalVariable=this.topModule.addConstantStr((String) node.getVal());
            ArrayList<Value>index=new ArrayList<>();
            index.add(new ConstantInt(0));
            index.add(new ConstantInt(0));
            var ptr=new GetElementPtrInst("gep", globalVariable,index);
            curBB.addInst(ptr);
            return ptr;
        } else {
            return new ConstantNull();
        }
    }

    @Override
    public Object visitLogicAndExpr(LogicAndExpr node) throws TypeChecker.semanticException {
        Value test1 = (Value) visit(node.getLoperand());
        var originalBB=curBB;
        var firstTrueBB=curFunc.addBB("land.true");
        var mergeBB=new BasicBlock("land.merge");
        curBB.addInst(new BranchInst(firstTrueBB,mergeBB,test1));
        curBB=firstTrueBB;
        Value test2= (Value) visit(node.getRoperand());
        curBB.addInst(new BranchInst(mergeBB, null,null));
        curFunc.addBB(mergeBB);
        curBB=mergeBB;
        var phi=new PhiNode("mergetmp", Type.TheInt1);
        curBB.addInst(phi);
        phi.addIncoming(test1,originalBB);
        phi.addIncoming(test2,firstTrueBB);
        return phi;
    }

    @Override
    public Object visitLogicOrExpr(LogicOrExpr node) throws TypeChecker.semanticException {

        Value test1 = (Value) visit(node.getLoperand());
        var originalBB=curBB;
        var firstFalseBB=curFunc.addBB("lor.false");
        var mergeBB=new BasicBlock("lor.merge");
        curBB.addInst(new BranchInst(mergeBB,firstFalseBB,test1));
        curBB=firstFalseBB;
        Value test2= (Value) visit(node.getRoperand());
        curBB.addInst(new BranchInst(mergeBB, null,null));
        curFunc.addBB(mergeBB);
        curBB=mergeBB;
        var phi=new PhiNode("mergetmp", Type.TheInt1);
        curBB.addInst(phi);
        phi.addIncoming(test1,originalBB);
        phi.addIncoming(test2,firstFalseBB);
        return phi;
    }

    @Override
    public Object visitMemberExpr(MemberExpr node) throws TypeChecker.semanticException {

        Value instanceV ;
        if (lhs) {
            lhs = false;
            instanceV = (Value) visit(node.getInstance_name());
            lhs = true;
        } else {
            lhs = false;
            instanceV = (Value) visit(node.getInstance_name());
            lhs = true;
        }
        int i;
        for (i = 0; i < ((RecordType) node.getInstanceType()).getFieldName().size(); i++) {
            if (node.getMember_name().equals(((RecordType) node.getInstanceType()).getFieldName().get(i))) {
                break;
            }
        }
        ArrayList<Value>index=new ArrayList<>();
        index.add(new ConstantInt(0));
        index.add(new ConstantInt(i));
        var newinst=new GetElementPtrInst("member",instanceV,index);
        curBB.addInst(newinst);
        //if the member type is a derived type ,we just need to return a pointer
        ptr=newinst;
        if (((RecordType) node.getInstanceType()).getFieldType().get(i).isPrimitiveType() && !lhs) {
            var loadinst=new LoadInst("load", newinst);
            curBB.addInst(loadinst);
            return loadinst;
        }
        return newinst;
    }

    @Override
    public Object visitMethodCallExpr(MethodCallExpr node) throws TypeChecker.semanticException {
        if (node.getName() instanceof MemberExpr) {
            var instanceV = (Value) visit(((MemberExpr) node.getName()).getInstance_name());
            SemanticType instanceType = ((MemberExpr) node.getName()).getInstanceType();
            String className = "";
            if(instanceType.isRecordType() ) {
                className = ((RecordType) instanceType).getRecordName();
            } else if (instanceType.isArrayType()) {
                className="_array";
                instanceV=new CastInst("cast", new PointerType(Type.TheInt8), instanceV);
                curBB.addInst((Instruction) instanceV);
            } else if (instanceType.isStringType()) {
                className="string";
            }
            Function function = (Function) topModule.getSymbolTable().get(className + "_" + ((MemberExpr) node.getName()).getMember_name());
            ArrayList<Value> arguments = new ArrayList<>();
            for (var arg : node.getArguments()) {
                Value argV = (Value) visit(arg);
                arguments.add(argV);
            }
            arguments.add(instanceV);
            var callInst=new CallInst("calltmp", function, arguments);
            curBB.addInst(callInst);
            return callInst;
        } else {
            String funcName=((NameExpr)node.getName()).getName();

            ArrayList<Value> arguments = new ArrayList<>();
            for (var arg : node.getArguments()) {
                Value argV = (Value) visit(arg);
                arguments.add(argV);
            }
            Function function= (Function) topModule.getSymbolTable().get(funcName);
            if (function == null) {
                function=(Function)topModule.getSymbolTable().get(curClass.getName()+"_"+funcName);
                arguments.add(curFunc.getThisPtr());
            }
            var callInst=new CallInst("calltmp",function,arguments);
            curBB.addInst(callInst);
            return callInst;
        }
    }

    @Override
    public Object visitMethodDecl(MethodDecl node) throws TypeChecker.semanticException {
        ArrayList<Type> paramTypes=new ArrayList<>();
        ArrayList<String> paramNames=new ArrayList<>();
        int cnt=0;
        for (var i : node.getSemanticParamTypes()) {
            paramTypes.add(convertTypeLookUp(i));
            paramNames.add(node.getParameters().get(cnt).getName());
            cnt++;
        }
        var ReturnType=convertTypeLookUp(node.getSemanticReturnType());
        String funcName;
        Function newfunc;
        if (curClass != null) {
            paramTypes.add(new PointerType(convertTypeLookUp(curClass.getSemanticType())));
            funcName=curClass.getName() + "_" + node.getName();
            paramNames.add("this_ptr");
        } else {
            funcName=node.getName();
        }
        newfunc=new Function(funcName, topModule, false,ReturnType , paramTypes,paramNames);
        curFunc=newfunc;
        topModule.addFunction(newfunc);
        curBB=newfunc.addBB("entry");
        for (int i = 0; i < node.getParameters().size(); i++) {
            var arg=new AllocaInst(node.getParameters().get(i).getName(), paramTypes.get(i));
            curBB.addInstToFirst(arg);
            curBB.addInst(new StoreInst(curFunc.getArguments().get(i), arg));
            curFunc.getSymtab().put(node.getParameters().get(i).getName(),arg);
        }
        return visit(node.getStmt());

    }

    private Value getIRRepresentation(String name) {
        var local=curFunc.getSymtab().get(name);
        if (local != null) {
            return local;
        }
        return topModule.getSymbolTable().get(name);//todo:maybe wrong
    }
    private Value ptr;
    @Override
    public Object visitNameExpr(NameExpr node) throws TypeChecker.semanticException {
        if (curClass == null || getIRRepresentation(node.getName()) != null) {
            if (!lhs) {
                ptr=getIRRepresentation(node.getName());
                var newInst = new LoadInst("load", ptr);
                curBB.addInst(newInst);
                return newInst;
            } else {
                return getIRRepresentation(node.getName());
            }
        } else {
            var thisV=curFunc.getThisPtr();
            int i;
            RecordType thisType= (RecordType) curClass.getSemanticType();
            for (i = 0; i < thisType.getFieldName().size(); i++) {
                if (thisType.getFieldName().get(i).equals(node.getName())) {
                    break;
                }
            }
            ArrayList<Value>  idx=new ArrayList<>();
            idx.add(new ConstantInt(0));
            idx.add(new ConstantInt(i));
            var getelement=new GetElementPtrInst("getelementptr", thisV, idx);
            ptr=getelement;
            curBB.addInst(getelement);
            if (!lhs && thisType.getFieldType().get(i).isPrimitiveType()) {
                var loadInst=new LoadInst("load", getelement);
                curBB.addInst(loadInst);
                return loadInst;
            }
            return getelement;
        }
    }
    private Type getPtrType(Type original,int dim){
        for (int i = 0; i < dim; i++) {
            original=new PointerType(original);
        }
        return original;
    }

    private Value generateNewArray(SemanticType type,int totdim, LinkedList<Value> dims){
        Function mallocFunc= (Function) topModule.getSymbolTable().get("malloc");
        ArrayList<Value> params = new ArrayList<>();
        Value firstDimV= dims.pollFirst();
        assert firstDimV != null;
        int size=totdim==1?(type.isBoolType()?1:8):8;
        var memberLength=new BinaryOpInst("mul", Instruction.Opcode.mul, firstDimV,new ConstantInt(size));
        var totLength=new BinaryOpInst("add", Instruction.Opcode.add,memberLength,new ConstantInt(8));
        params.add(totLength);
        var malloc=new CallInst("malloc", mallocFunc,params);
        var typePtrToArray=getPtrType(convertTypeLookUp(type),totdim);
        var newArray=new CastInst("cast_malloc",typePtrToArray,malloc);
        var cast_PrepareForSize=new CastInst("cast_size", new PointerType(Type.TheInt64), newArray);
        var storeInst=new StoreInst(firstDimV,cast_PrepareForSize);
        ArrayList<Value> gepindex=new ArrayList<>();
        gepindex.add(new ConstantInt(8/size));
        var arrayBase=new GetElementPtrInst("gep", newArray,gepindex );
        curBB.addInst(memberLength);
        curBB.addInst(totLength);
        curBB.addInst(malloc);
        curBB.addInst(newArray);
        curBB.addInst(cast_PrepareForSize);
        curBB.addInst(storeInst);
        curBB.addInst(arrayBase);
        if (dims.isEmpty()) {
            return arrayBase;
        }
        var condBB=curFunc.addBB("for_cond");
        var loopBB = new BasicBlock("for_loop");
        var stepBB=new BasicBlock("for_step");
        var afterLoopBB=new BasicBlock("for_afterloop");
        ArrayList<Value> gepindex2=new ArrayList<>();
        gepindex2.add(firstDimV);
        var arrayEnd=new GetElementPtrInst("arrayend", arrayBase, gepindex2);
        curBB.addInst(arrayEnd);
        var looptmp=new AllocaInst("looptmp", typePtrToArray);
        curFunc.getEntryBB().addInstToFirst(looptmp);
        curBB.addInst(new StoreInst(arrayEnd,looptmp));
        curBB.addInst(new BranchInst(condBB,null,null));

        curBB=condBB;
        var loadtmp=new LoadInst("loadtmp",looptmp);
        curBB.addInst(loadtmp);
        var cmptmp=new IcmpInst("cmptmp", Instruction.Opcode.LT, loadtmp,arrayEnd);
        curBB.addInst(cmptmp);
        curBB.addInst(new BranchInst(loopBB, afterLoopBB,cmptmp ));

        curFunc.addBB(loopBB);
        curBB=loopBB;
        var storeGenerated=new StoreInst(generateNewArray(type,totdim-1,dims), loadtmp);
        curBB.addInst(storeGenerated);
        curBB.addInst(new BranchInst(stepBB, null,null));

        curFunc.addBB(stepBB);
        curBB=stepBB;
        var gepindex3=new ArrayList<Value>();
        gepindex3.add(new ConstantInt(size));
        var nextLoop=new GetElementPtrInst("nextLoop", loadtmp,gepindex3 );
        curBB.addInst(nextLoop);
        curBB.addInst(new StoreInst(nextLoop,looptmp));
        curBB.addInst(new BranchInst(condBB, null,null));

        curFunc.addBB(afterLoopBB);
        curBB=afterLoopBB;

        return arrayBase;
    }
    @Override
    public Object visitNewExpr(NewExpr node) throws TypeChecker.semanticException {

        //array
        if (node.getTotDim() != 0) {
            LinkedList<Value> dims=new LinkedList<>();
            for (var dim: node.getDims()) {
                dims.addLast((Value) visit(dim));
            }
            return generateNewArray(node.getSemanticType(), node.getTotDim(), dims);

        }
        //non-array(call ctor)
        else {
            Function mallocFunc= (Function) topModule.getSymbolTable().get("malloc");
            ArrayList<Value> params = new ArrayList<>();
            params.add(new ConstantInt(getRecordMallocSize(node.getSemanticType())));
            var newInstance=new CallInst("malloc",mallocFunc,params);
            curBB.addInst(newInstance);
            Function ctor=(Function)topModule.getSymbolTable().get(node.getTypename()+"_"+node.getTypename());
            if(ctor!=null) {
                ArrayList<Value> params2 = new ArrayList<>();
                params2.add(newInstance);
                var ctorCall = new CallInst(node.getTypename() + ".ctor", ctor, params2);
                curBB.addInst(ctorCall);
            }
            var cast=new CastInst("cast", new PointerType(convertTypeLookUp(node.getSemanticType())), newInstance);
            curBB.addInst(cast);
            return cast;
        }
    }

    @Override
    public Object visitPostfixExpr(PostfixExpr node) throws TypeChecker.semanticException {
        Value originalV_rhs= (Value) visit(node.getVal());
        if (node.getOperator().equals("++")) {
            Instruction tmpInst = new BinaryOpInst("postfix_add", Instruction.Opcode.add, originalV_rhs, new ConstantInt(1));
            Instruction storeInst = new StoreInst(tmpInst, ptr);
            curBB.addInst(tmpInst);
            curBB.addInst(storeInst);
            return originalV_rhs;
        } else {
            Instruction tmpInst = new BinaryOpInst("postfix_sub", Instruction.Opcode.sub, originalV_rhs, new ConstantInt(1));
            Instruction storeInst = new StoreInst(tmpInst, ptr);
            curBB.addInst(tmpInst);
            curBB.addInst(storeInst);
            return originalV_rhs;
        }
    }

    @Override
    public Object visitPrefixExpr(PrefixExpr node) throws TypeChecker.semanticException {
        Value originalV_rhs= (Value) visit(node.getVal());
        if (node.getOperator().equals("++")) {
            Instruction tmpInst = new BinaryOpInst("postfix_add", Instruction.Opcode.add, originalV_rhs, new ConstantInt(1));
            Instruction storeInst = new StoreInst(tmpInst, ptr);
            curBB.addInst(tmpInst);
            curBB.addInst(storeInst);
            return tmpInst;
        } else {
            Instruction tmpInst = new BinaryOpInst("postfix_sub", Instruction.Opcode.sub, originalV_rhs, new ConstantInt(1));
            Instruction storeInst = new StoreInst(tmpInst, ptr);
            curBB.addInst(tmpInst);
            curBB.addInst(storeInst);
            return tmpInst;
        }
    }

    @Override
    public Object visitReturnStmt(ReturnStmt node) throws TypeChecker.semanticException {
        Value returnV = null;
        if (node.getVal() != null) {
             returnV= (Value) visit(node.getVal());
        }
        curBB.addInst(new ReturnInst(returnV));
        return null;
    }

    @Override
    public Object visitSemi(Semi node) {
        return null;
    }

    @Override
    public Object visitSubscriptorExpr(SubscriptorExpr node) throws TypeChecker.semanticException {

        Value arrayV;
        Value indexV;
        if(lhs){
            lhs=false;
            arrayV = (Value) visit(node.getName());
            indexV = (Value) visit(node.getIndex());
            lhs=true;
        }else {
            arrayV = (Value) visit(node.getName());
            indexV = (Value) visit(node.getIndex());
        }
        ArrayList<Value>idx=new ArrayList<>();
//        idx.add(new ConstantInt(0));
        idx.add(indexV);
        var newInst=new GetElementPtrInst("array", arrayV, idx);
        curBB.addInst(newInst);
        if (!lhs) {
            var loadInst=new LoadInst( "load", newInst);
            curBB.addInst(loadInst);
            return loadInst;
        }
        return newInst;
    }

    @Override
    public Object visitThisExpr(ThisExpr node) {
        return curFunc.getThisPtr();
    }

    @Override
    public Object visitVariableDeclStmt(VariableDeclStmt node) throws TypeChecker.semanticException {
        Type type= convertTypeLookUp(node.getSemanticType());
        if (isglobal) {
            var globalV=new GlobalVariable(node.getName(),type,topModule);
            topModule.addGlobalVariable(globalV);
            if (node.getInit() != null) {
                 Value initV= (Value) visit(node.getInit());
                if (initV.getValueType() == Value.ValueType.ConstantVal) {
                    globalV.setInitializer(initV);
                }else {
                    curBB.addInst(new StoreInst(initV, globalV));
                }
            }
            return null;
        } else {
            if (node.getSemanticType().actual().isRecordType()) {
                type=new PointerType(type);
            }
            var newInst=new AllocaInst(node.getName(), type);
            curFunc.getEntryBB().addInstToFirst(newInst);

            if (node.getInit() != null) {
                Value initCode=(Value) visit(node.getInit());
                curBB.addInst(new StoreInst(initCode,newInst));
            }

            return newInst;
        }
    }

    @Override
    public Object visitWhileStmt(WhileStmt node) throws TypeChecker.semanticException {
        BasicBlock condBB=curFunc.addBB("while_cond");
        BasicBlock loopBB=new BasicBlock("while_loop");
        BasicBlock afterLoopBB =new BasicBlock("while_afterLoop");
        curBB.addInst(new BranchInst(condBB, null, null));
        curBB=condBB;
        Value condV= (Value) visit(node.getCondition());
        curBB.addInst(new BranchInst(loopBB,afterLoopBB,condV));
        curFunc.addBB(loopBB);
        curBB=loopBB;
        var prevLoopBB=curLoopBB;
        var prevAfterLoopBB=curAfterLoopBB;
        curLoopBB=loopBB;
        curAfterLoopBB=afterLoopBB;
        visit(node.getLoopBody());
        curBB.addInst(new BranchInst(condBB,null,null));
        curLoopBB=prevLoopBB;
        curAfterLoopBB=prevAfterLoopBB;
        curFunc.addBB(afterLoopBB);
        curBB=afterLoopBB;
        return null;
    }

    @Override
    public Object visit(Node node) throws TypeChecker.semanticException {
        return node.accept(this);
    }
}
