package IR.instructions;

import IR.*;
import IR.Types.PointerType;

public class LoadInst extends Instruction {
    public LoadInst(String name, Value ptr) {
        super(name, ((PointerType)ptr.getType()).getPtrType(),Opcode.load);
        operands.add(new Use(ptr,this));
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitLoadInst(this);
    }
}
