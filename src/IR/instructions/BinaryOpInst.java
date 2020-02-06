package IR.instructions;

import IR.IRVisitor;
import IR.Instruction;
import IR.Use;
import IR.Value;

public class BinaryOpInst extends Instruction {


    public BinaryOpInst(String name, Opcode opcode, Value lhs,Value rhs) {
        super(name, lhs.getType(), opcode);
        assert lhs.getType().equals(rhs.getType());
        operands.add(new Use(lhs,this));
        operands.add(new Use(rhs,this));

    }
    public Value getLhs(){return operands.get(0).getVal();}
    public Value getRhs(){return operands.get(1).getVal();}
    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitBinaryOpInst(this);
    }
}
