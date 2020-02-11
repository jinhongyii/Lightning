package IR.instructions;

import IR.*;
import IR.Types.FunctionType;
import org.jetbrains.annotations.NotNull;

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

    public Function getCallee(){
        return (Function) operands.get(0).getVal();
    }

    @Override
    public Instruction cloneInst() {
        ArrayList<Value> params = getParams();
        return new CallInst(getName(), (Function) operands.get(0).getVal(),params );
    }

    @NotNull
    public ArrayList<Value> getParams() {
        ArrayList<Value> params=new ArrayList<>();
        for (int i = 1; i < operands.size(); i++) {
            params.add(operands.get(i).getVal());
        }
        return params;
    }
}
