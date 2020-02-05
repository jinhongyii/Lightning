package IR.instructions;

import IR.*;

public class IcmpInst extends Instruction {

    public IcmpInst(String name, Opcode opcode, Value lhs, Value rhs) {
        super(name, Type.TheInt1, opcode);
        assert lhs.getType().equals(rhs.getType());
        operands.add(new Use(lhs,this));
        operands.add(new Use(rhs,this));
    }
    public Value getLhs(){
        return operands.get(0).getVal();
    }
    public Value getRhs(){
        return operands.get(1).getVal();
    }
    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitIcmpInst(this);
    }
}
