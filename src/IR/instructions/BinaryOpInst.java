package IR.instructions;

import IR.*;

public class BinaryOpInst extends BinaryOperation {


    public BinaryOpInst(String name, Opcode opcode, Value lhs,Value rhs) {
        super(name, lhs.getType(), opcode,lhs,rhs);
        assert lhs.getType().equals(rhs.getType());

    }
    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitBinaryOpInst(this);
    }

    @Override
    public Instruction cloneInst() {
        return new BinaryOpInst(this.getName(),this.getOpcode(),this.getLhs(),this.getRhs());
    }
}
