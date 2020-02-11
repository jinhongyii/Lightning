package IR.instructions;

import IR.*;
import IR.Types.CompositeType;
import IR.Types.PointerType;

import java.util.ArrayList;

public class GetElementPtrInst extends Instruction {

    public GetElementPtrInst(String name, Value ptr, ArrayList<Value> idx) {
        super(name,new PointerType(getInnerType(ptr.getType(),idx)) , Opcode.getelementptr);
        operands.add(new Use(ptr,this));
        for (var val : idx) {
            operands.add(new Use(val,this));
        }
    }

    @Override
    public Instruction cloneInst() {
        ArrayList<Value> idx=new ArrayList<>();
        for (int i = 1; i < operands.size(); i++) {
            idx.add(operands.get(i).getVal());
        }
        return new GetElementPtrInst(this.getName(),operands.get(0).getVal(),idx);
    }

    private static Type getInnerType(Type ptrType, ArrayList<Value> idx) {
        if (!(ptrType instanceof PointerType)) {
            return null;
        }
        if (idx.isEmpty()) {
            return ((PointerType)ptrType).getPtrType();
        }
        int cnt=0;
        while (cnt < idx.size()) {
            if (cnt == 0) {
                ptrType = ((PointerType) ptrType).getPtrType();
            } else {
                assert ptrType instanceof CompositeType && ((CompositeType) ptrType).checkIndexValid(idx.get(cnt));
                ptrType=((CompositeType)ptrType).getInnerType(idx.get(cnt));
            }
            cnt++;
        }
        return ptrType;
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitGEPInst(this);
    }
}
