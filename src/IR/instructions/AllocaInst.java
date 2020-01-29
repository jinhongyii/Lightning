package IR.instructions;

import IR.IRVisitor;
import IR.Instruction;
import IR.Type;
import IR.Types.PointerType;


public class AllocaInst extends Instruction{

    public AllocaInst(String name, Type type) {
        super(name, new PointerType(type), Opcode.alloca);
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitAllocaInst(this);
    }
}
