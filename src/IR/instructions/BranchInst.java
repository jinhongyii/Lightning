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
    public void setCond(Value cond){
        operands.remove(2).delete();
        operands.add(new Use(cond,this));
    }
    public void swapThenElse(){
        operands.add(1,operands.remove(0));
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
    public void setConditional(BasicBlock dst,BasicBlock prev){
        int idx;
        if (getDstThen() == prev) {
            idx = 0;
        } else {
            idx=1;
        }
        operands.get(idx).delete();
        operands.remove(idx);
        operands.add(idx, new Use(dst,this));
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

    @Override
    public Instruction cloneInst() {
        if (this.isConditional()) {
            return new BranchInst(getDstThen(), getDstElse(), getCondition());
        } else {
            return new BranchInst(getDstThen(),null,null);
        }
    }
}
