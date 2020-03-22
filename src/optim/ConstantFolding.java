package optim;

import IR.Module;
import IR.*;
import IR.instructions.BinaryOpInst;
import IR.instructions.CastInst;
import IR.instructions.IcmpInst;

public class ConstantFolding extends FunctionPass {

    public ConstantFolding(Function function) {
        super(function);
    }

    @Override
    public boolean run() {
        return constantFold(function);
    }
    public static void runOnModule(Module module){
        for (var func : module.getFunctionList()) {
            if (!func.isExternalLinkage()) {
                var constantFolding=new ConstantFolding(func);
                constantFolding.run();
            }
        }
    }

    private boolean constantFold(Function function) {
        boolean changed=false;
        for (var bb = function.getHead(); bb != null; bb = bb.getNext()) {
            for (var inst = bb.getHead(); inst != null;) {
                var tmp=inst.getNext();
                var replaceVal= constFoldInst(inst);
                if(replaceVal!=null) {
                    inst.transferUses(replaceVal);
                    inst.delete();
                    changed=true;
                }
                inst=tmp;
            }
        }
        return changed;
    }
    public static Value constFoldInst(Instruction inst){
        Value replaceVal=null;
        if(inst instanceof BinaryOpInst){
            var lhs=((BinaryOpInst) inst).getLhs();
            var rhs=((BinaryOpInst) inst).getRhs();
            replaceVal = constFoldBinaryOpInst(inst.getOpcode(), lhs, rhs);

        }else if(inst instanceof IcmpInst){
            var lhs=((IcmpInst) inst).getLhs();
            var rhs=((IcmpInst) inst).getRhs();
            replaceVal = constFoldIcmpInst(inst.getOpcode(), lhs, rhs);
        } else if (inst instanceof CastInst) {
            var source=inst.getOperands().get(0).getVal();
            replaceVal = constFoldCastInst(inst.getType(), replaceVal, source);
        }
        return replaceVal;
    }

    public static Value constFoldCastInst(Type type, Value replaceVal, Value source) {
        if (source instanceof ConstantNull) {
            replaceVal=new ConstantNull();
        } else if (type.equals(source.getType())) {
            replaceVal=source;
        }
        return replaceVal;
    }

    public static Value constFoldIcmpInst(Instruction.Opcode opcode, Value lhs, Value rhs) {
        Value replaceVal=null;
        if (lhs instanceof ConstantInt && rhs instanceof ConstantInt) {
            var constLhs=((ConstantInt) lhs).getVal();
            var constRhs=((ConstantInt) rhs).getVal();
            switch (opcode){
                case EQ:replaceVal=new ConstantBool(constLhs==constRhs);break;
                case NE:replaceVal=new ConstantBool(constLhs!=constRhs);break;
                case GT:replaceVal=new ConstantBool(constLhs>constRhs);break;
                case GE:replaceVal=new ConstantBool(constLhs>=constRhs);break;
                case LT:replaceVal=new ConstantBool(constLhs<constRhs);break;
                case LE:replaceVal=new ConstantBool(constLhs<=constRhs);break;
            }
        }else if(lhs instanceof ConstantBool && rhs instanceof ConstantBool){
            var constLhs=((ConstantBool)lhs).isTrue();
            var constRhs=((ConstantBool)rhs).isTrue();
            switch (opcode){
                case EQ:replaceVal=new ConstantBool(constLhs==constRhs);break;
                case NE:replaceVal=new ConstantBool(constLhs!=constRhs);break;
            }
        }else if(lhs instanceof ConstantNull && rhs instanceof ConstantNull){
            switch (opcode){
                case EQ:replaceVal=new ConstantBool(true);break;
                case NE:replaceVal=new ConstantBool(false);break;
            }
        }
        return replaceVal;
    }
    public static Value constFoldBinaryOperation(Instruction.Opcode opcode, Value lhs, Value rhs){
        switch (opcode){
            case EQ:case NE:case LT:case LE:case GE:case GT:
                return constFoldIcmpInst(opcode,lhs,rhs);
            default:
                return constFoldBinaryOpInst(opcode,lhs,rhs);
        }
    }
    public static Value constFoldBinaryOpInst(Instruction.Opcode opcode,  Value lhs, Value rhs) {
        Value replaceVal=null;
        if (lhs instanceof ConstantInt && rhs instanceof ConstantInt) {
            var constLhs=(ConstantInt) lhs;
            var constRhs=(ConstantInt) rhs;
            switch (opcode) {
                case add:replaceVal=new ConstantInt(constLhs.getVal()+constRhs.getVal());break;
                case sub:replaceVal=new ConstantInt(constLhs.getVal()-constRhs.getVal());break;
                case mul:replaceVal=new ConstantInt(constLhs.getVal()*constRhs.getVal());break;
                case div: {
                    if (constRhs.getVal() == 0) {
                        return null;
                    }
                    replaceVal = new ConstantInt(constLhs.getVal() / constRhs.getVal());
                    break;
                }
                case rem: {
                    if (constRhs.getVal() == 0) {
                        return null;
                    }
                    replaceVal = new ConstantInt(constLhs.getVal() % constRhs.getVal());
                    break;
                }
                case and:replaceVal=new ConstantInt(constLhs.getVal()& constRhs.getVal());break;
                case or:replaceVal=new ConstantInt(constLhs.getVal() | constRhs.getVal());break;
                case xor:replaceVal=new ConstantInt(constLhs.getVal() ^ constRhs.getVal());break;
                case shl:replaceVal=new ConstantInt(constLhs.getVal()<<constRhs.getVal());break;
                case shr:replaceVal=new ConstantInt(constLhs.getVal()>>constRhs.getVal());break;
            }
        } else if ((lhs instanceof ConstantBool && rhs instanceof ConstantBool)) {
            var constLhs=(ConstantBool) lhs;
            var constRhs=(ConstantBool) rhs;
            switch (opcode) {
                case and:replaceVal=new ConstantBool(constLhs.isTrue()& constRhs.isTrue());break;
                case or:replaceVal=new ConstantBool(constLhs.isTrue() | constRhs.isTrue());break;
                case xor:replaceVal=new ConstantBool(constLhs.isTrue() ^ constRhs.isTrue());break;
            }
        }
        return replaceVal;
    }
}
