package IR.instructions;

import IR.*;
import IR.Types.CompositeType;
import IR.Types.PointerType;
import IR.Types.StructType;
import backend.IRBuilder;

import java.util.ArrayList;

public class GetElementPtrInst extends Instruction {

    public GetElementPtrInst(String name, Value ptr, ArrayList<Value> idx) {
        super(name,new PointerType(getInnerType(ptr.getType(),idx)) , Opcode.getelementptr);
        operands.add(new Use(ptr,this));
        for (var val : idx) {
            operands.add(new Use(val,this));
        }
    }
    int getTypeSize(Type type){
        return type.equals(Type.TheInt1)?1:4;
    }
    int getFieldOffset(Type struct, int idx){
        var fieldTypes= ((StructType)struct).getRecordTypes();
        int tot=0;
        for (int i = 0; i < idx; i++) {
            if (tot % 4 == 0) {
                tot += getTypeSize(fieldTypes.get(i));
            } else {
                if (fieldTypes.get(i).equals(Type.TheInt1)) {
                    tot += 1;
                } else {
                    tot+=8-(tot%4);
                }
            }
        }
        return tot;
    }
    public int getOffset(){
        assert operands.size()<=3;
        var object=operands.get(0).getVal();
        if (operands.size() == 2) {
            //array
            var idx = operands.get(1).getVal();
            if (idx instanceof ConstantInt) {
                return getTypeSize(((PointerType) object.getType()).getPtrType()) * ((ConstantInt) idx).getVal();
            } else {
                return -1;
            }
        } else {
            //struct
            var idx=operands.get(2).getVal();
            assert idx instanceof  ConstantInt;
            return getFieldOffset(((PointerType) object.getType()).getPtrType(),((ConstantInt) idx).getVal());
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
