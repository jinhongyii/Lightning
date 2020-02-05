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
    //may delete phi node itself
    public void removeIncoming(BasicBlock basicBlock){
        for (int i = 0; i < operands.size(); i += 2) {
            if (operands.get(i + 1).getVal() == basicBlock) {
                operands.get(i).delete();
                operands.remove(i);
                operands.get(i).delete();
                operands.remove(i);
            }
        }
        if (operands.size() == 0) {
            this.transferUses(Type.getNull(getType()));
            this.delete();
        }
    }
    public BasicBlock getBB(int index){
        return (BasicBlock) operands.get(index*2+1).getVal();
    }
    public Value getValue(int index){
        return operands.get(index*2).getVal();
    }
    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitPhiNode(this);
    }
}
