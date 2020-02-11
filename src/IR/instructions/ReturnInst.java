package IR.instructions;

import IR.*;

public class ReturnInst extends Instruction {

    public ReturnInst( Value value) {
        super("", Type.theVoidType, Opcode.ret);
        if(value!=null) {
            operands.add(new Use(value,this));
        }
    }
    public boolean hasRetValue(){
        return operands.size()>0;
    }
    public Value getRetValue(){
        return operands.get(0).getVal();
    }
    @Override
    public Instruction cloneInst() {
        if (operands.size() == 1) {
            return new ReturnInst(operands.get(0).getVal());
        } else {
            return new ReturnInst(null);
        }
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitReturnInst(this);
    }
}
