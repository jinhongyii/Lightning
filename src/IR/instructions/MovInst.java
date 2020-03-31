package IR.instructions;

import IR.IRVisitor;
import IR.Instruction;
import IR.Type;
import IR.Value;

public class MovInst extends Instruction {
    Value from;

    public Value getFrom() {
        return from;
    }

    public Value getTo() {
        return to;
    }

    Value to;

    public MovInst(Value from,Value to) {
        super("", null, null);
        this.from=from;
        this.to=to;
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitMovInst(this);
    }
}
