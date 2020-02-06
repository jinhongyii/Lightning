package IR.instructions;

import IR.IRVisitor;
import IR.Instruction;
import IR.Types.PointerType;
import IR.Use;
import IR.Value;

public class LoadInst extends Instruction {
    public LoadInst(String name, Value ptr) {
        super(name, ((PointerType)ptr.getType()).getPtrType(),Opcode.load);
        operands.add(new Use(ptr,this));
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitLoadInst(this);
    }
    public Value getLoadTarget(){
        return operands.get(0).getVal();
    }
}
