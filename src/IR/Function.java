package IR;

import IR.Types.FunctionType;
import IR.instructions.AllocaInst;
import IR.instructions.BranchInst;
import IR.instructions.LoadInst;
import IR.instructions.ReturnInst;

import java.util.ArrayList;

public class Function extends User{
    boolean externalLinkage;
    ArrayList<BasicBlock> basicBlockList=new ArrayList<>();
    BasicBlock returnBB=new BasicBlock("return");

    public BasicBlock getReturnBB() {
        return returnBB;
    }

    public Instruction getReturnV() {
        return returnV;
    }

    Instruction returnV;
    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    ArrayList<Argument> arguments=new ArrayList<>();
    SymbolTable symtab=new SymbolTable();
    Module parent;

    public void setExternalLinkage(boolean externalLinkage) {
        this.externalLinkage = externalLinkage;
    }


    public Function(String name, Module parent, Type returnType, ArrayList<Type> paramTypes, ArrayList<String> paramNames) {
        super(name, new FunctionType(returnType,paramTypes), ValueType.FunctionVal);
        int cnt=0;
        for (var type : paramTypes) {
            arguments.add(new Argument(paramNames.get(cnt), type, this));
            cnt++;
        }
        this.parent=parent;
        this.externalLinkage=true;
    }
    public void internalLinkage(){
        this.externalLinkage=false;
        addBB("entry");
        if(((FunctionType)getType()).getResultType().getId()==Type.TypeID.VoidType){
            returnBB.addInst(new ReturnInst(null));
        }else {
            returnV = new AllocaInst("retV", ((FunctionType) getType()).getResultType());
            getEntryBB().addInst(returnV);
            Instruction realReturn = new LoadInst("loadret", returnV);
            returnBB.addInst(realReturn);
            returnBB.addInst(new ReturnInst(realReturn));
        }
    }
    public void updateReturnBB(){
        if(!externalLinkage) {
            basicBlockList.get(basicBlockList.size() - 1).addInst(new BranchInst(returnBB, null, null));
            addBB(returnBB);
        }

    }
    public BasicBlock addBB(String name){
        BasicBlock newBB=new BasicBlock(name);
        basicBlockList.add(newBB);
        newBB.setParent(this);
        symtab.put(name, newBB);
        return newBB;
    }

    public void addBB(BasicBlock basicBlock) {
        basicBlockList.add(basicBlock);
        basicBlock.setParent(this);
        symtab.put(basicBlock.getName(),basicBlock);
        for (var inst : basicBlock.instructionList) {
            symtab.put(inst.getName(),inst);
        }
    }


    public Value getThisPtr(){
        return arguments.get(arguments.size()-1);
    }

    public SymbolTable getSymtab() {
        return symtab;
    }
    public BasicBlock getEntryBB(){
        return basicBlockList.get(0);
    }
    public BasicBlock getLastBB(){return basicBlockList.get(basicBlockList.size()-1);}
    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitFunction(this);
    }

    @Override
    public String toString() {
        return "@"+getName();
    }

    public ArrayList<BasicBlock> getBasicBlockList() {
        return basicBlockList;
    }

}
