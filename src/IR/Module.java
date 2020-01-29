package IR;



import IR.Types.ArrayType;
import IR.Types.TypeType;

import java.util.ArrayList;
import java.util.HashMap;

public class Module {
    ArrayList<GlobalVariable> globalList=new ArrayList<>();

    public ArrayList<GlobalVariable> getGlobalList() {
        return globalList;
    }


    public ArrayList<Function> getFunctionList() {
        return functionList;
    }

    ArrayList<Function> functionList=new ArrayList<>();
    SymbolTable symbolTable=new SymbolTable();
    String name;
    HashMap<String,GlobalVariable> constantStrings=new HashMap<>();

    public void addGlobalVariable(GlobalVariable globalVariable){
        symbolTable.put(globalVariable.getName(), globalVariable);
        globalList.add(globalVariable);
    }
    public void addFunction(Function function){
        symbolTable.put(function.getName(),function);
        functionList.add(function);
    }
    public void addStruct(String name,Type type) {
        globalList.add(new GlobalVariable(name,new TypeType(), type,this));
        symbolTable.put(name, type);
    }
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public String getName() {
        return name;
    }

    public GlobalVariable addConstantStr(String string){
        if (!constantStrings.containsKey(string)) {
            var global = new GlobalVariable(".str", new ArrayType(Type.TheInt8, string.length() + 1), new ConstantString(string), this);
            constantStrings.put(string, global);
            addGlobalVariable(global);
            return global;
        } else {
            return constantStrings.get(string);
        }

    }
    public Object accept(IRVisitor visitor){
        return visitor.visitModule(this);
    }

}
