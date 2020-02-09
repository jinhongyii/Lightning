package IR.instructions;

import IR.*;

public class IcmpInst extends BinaryOperation {

    public IcmpInst(String name, Opcode opcode, Value lhs, Value rhs) {
        super(name, Type.TheInt1, opcode,lhs,rhs);
        assert lhs.getType().equals(rhs.getType());
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitIcmpInst(this);
    }
}
