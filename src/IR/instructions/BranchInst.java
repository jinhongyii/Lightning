package IR.instructions;

import IR.*;

public class BranchInst extends Instruction {


    public BranchInst( BasicBlock Then, BasicBlock Else, Value cond) {
        super("", Type.theVoidType, Opcode.br);
        operands.add(new Use(Then,this));
        if (cond != null) {
            operands.add(new Use(Else, this));
            operands.add(new Use(cond,this));
        }
    }
    public boolean isConditional(){
        return operands.size()==3;
    }
    public BasicBlock getDstThen(){
        return (BasicBlock) operands.get(0).getVal();
    }
    public BasicBlock getDstElse(){
        return (BasicBlock) operands.get(1).getVal();
    }
    public Value getCondition(){
        return operands.get(2).getVal();
    }
    public void setUnconditional(BasicBlock dst){
        for (var use : operands) {
            use.delete();
        }
        operands.clear();
        operands.add(new Use(dst, this));
    }
    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitBranchInst(this);
    }
}