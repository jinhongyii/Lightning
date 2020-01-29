package IR.instructions;

import IR.*;

public class StoreInst extends Instruction {

    public StoreInst( Value storeVal,Value ptr) {
        super("",Type.theVoidType, Opcode.store);
        operands.add(new Use(storeVal,this));
        operands.add(new Use(ptr,this));
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitStoreInst(this);
    }
}
