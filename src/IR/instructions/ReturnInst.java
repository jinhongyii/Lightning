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
    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitReturnInst(this);
    }
}
