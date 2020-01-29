package IR.instructions;

import IR.*;

public class BinaryOpInst extends Instruction {


    public BinaryOpInst(String name, Opcode opcode, Value lhs,Value rhs) {
        super(name, lhs.getType(), opcode);
        assert lhs.getType().equals(rhs.getType());
        operands.add(new Use(lhs,this));
        operands.add(new Use(rhs,this));

    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitBinaryOpInst(this);
    }
}
