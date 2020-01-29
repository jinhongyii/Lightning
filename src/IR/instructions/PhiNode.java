package IR.instructions;

import IR.*;

public class PhiNode extends Instruction {
    public PhiNode(String name, Type type) {
        super(name, type,Opcode.phi);
    }
    public void addIncoming(Value value, BasicBlock basicBlock){
        operands.add(new Use(value,this));
        operands.add(new Use(basicBlock,this));
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitPhiNode(this);
    }
}
