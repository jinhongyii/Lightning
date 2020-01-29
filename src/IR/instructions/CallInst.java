package IR.instructions;

import IR.*;
import IR.Types.FunctionType;

import java.util.ArrayList;

public class CallInst extends Instruction {

    public CallInst(String name, Function function, ArrayList<Value> params) {
        super(name, ((FunctionType)function.getType()).getResultType(),Opcode.call);
        operands.add(new Use(function, this));
        for (var param : params) {
            operands.add(new Use(param,this));
        }
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitCallInst(this);
    }
}
