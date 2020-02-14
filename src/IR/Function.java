package IR;

import IR.Types.FunctionType;
import IR.instructions.AllocaInst;
import IR.instructions.BranchInst;
import IR.instructions.LoadInst;
import IR.instructions.ReturnInst;

import java.util.ArrayList;

public class Function extends User{
    boolean externalLinkage;
    BasicBlock head;
    BasicBlock tail;
//    ArrayList<BasicBlock> basicBlockList=new ArrayList<>();
    BasicBlock returnBB=new BasicBlock("return");
    Instruction returnV;
    ArrayList<Argument> arguments=new ArrayList<>();
    SymbolTable symtab=new SymbolTable();
    Module parent;

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

    public BasicBlock getHead() {
        return head;
    }

    public BasicBlock getTail() {
        return tail;
    }

    public boolean isExternalLinkage() {
        return externalLinkage;
    }

    public BasicBlock getReturnBB() {
        return returnBB;
    }

    public Instruction getReturnV() {
        return returnV;
    }

    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    public void setExternalLinkage(boolean externalLinkage) {
        this.externalLinkage = externalLinkage;
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

    public Module getParent() {
        return parent;
    }

    public void updateReturnBB(){
        if(!externalLinkage) {
            tail.addInst(new BranchInst(returnBB, null, null));
            addBB(returnBB);
        }

    }
    public BasicBlock addBB(String name){
        BasicBlock newBB=new BasicBlock(name);
        if (tail == null) {
            head=tail=newBB;
        }else {
            tail.setNextBB(newBB);
            tail=newBB;
//            basicBlockList.add(newBB);
        }
        newBB.setParent(this);
        symtab.put(name, newBB);
        return newBB;
    }

    public void addBB(BasicBlock basicBlock) {
        if(tail==null){
            head=tail=basicBlock;
        }else {
//            basicBlockList.add(basicBlock);
            tail.setNextBB(basicBlock);
            tail=basicBlock;
        }
        basicBlock.setParent(this);
        symtab.put(basicBlock.getName(),basicBlock);
        for (var i = basicBlock.head; i !=null;i = i.next) {
            symtab.put(i.getName(),i);
        }
//        for (var inst : basicBlock.instructionList) {
//            symtab.put(inst.getName(),inst);
//        }
    }


    public Value getThisPtr(){
        return arguments.get(arguments.size()-1);
    }

    public SymbolTable getSymtab() {
        return symtab;
    }
    public BasicBlock getEntryBB(){
        return head;
    }
    public BasicBlock getLastBB(){return tail;}
    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitFunction(this);
    }

    @Override
    public String toString() {
        return "@"+getName();
    }



}
