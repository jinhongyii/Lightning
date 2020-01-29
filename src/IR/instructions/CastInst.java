package IR.instructions;

import IR.*;

public class CastInst extends Instruction {

    public CastInst(String name, Type type, Value src) {
        super(name, type,Opcode.cast);
        operands.add(new Use(src,this));
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitCastInst(this);
    }
}
