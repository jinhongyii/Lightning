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

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitBranchInst(this);
    }
}
