package optim;

import IR.*;
import IR.Module;
import IR.instructions.BinaryOpInst;
import IR.instructions.CastInst;
import IR.instructions.IcmpInst;

public class ConstantFolding extends FunctionPass {

    public ConstantFolding(Function function) {
        super(function);
    }

    @Override
    public void run() {
        constantFold(function);
    }
    public static void runOnModule(Module module){
        for (var func : module.getFunctionList()) {
            if (!func.isExternalLinkage()) {
                var constantFolding=new ConstantFolding(func);
                constantFolding.run();
            }
        }
    }

    private void constantFold(Function function) {
        for (var bb = function.getHead(); bb != null; bb = bb.getNext()) {
            for (var inst = bb.getHead(); inst != null;) {
                var tmp=inst.getNext();
                handleInst(inst);
                inst=tmp;
            }
        }
    }
    private void handleInst(Instruction inst){
        Value replaceVal=null;
        if(inst instanceof BinaryOpInst){
            var lhs=((BinaryOpInst) inst).getLhs();
            var rhs=((BinaryOpInst) inst).getRhs();
            if (lhs instanceof ConstantInt && rhs instanceof ConstantInt) {
                var constLhs=(ConstantInt) lhs;
                var constRhs=(ConstantInt) rhs;
                switch (inst.getOpcode()) {
                    case add:replaceVal=new ConstantInt(constLhs.getVal()+constRhs.getVal());break;
                    case sub:replaceVal=new ConstantInt(constLhs.getVal()-constRhs.getVal());break;
                    case mul:replaceVal=new ConstantInt(constLhs.getVal()*constRhs.getVal());break;
                    case div:replaceVal=new ConstantInt(constLhs.getVal()/constRhs.getVal());break;
                    case rem:replaceVal=new ConstantInt(constLhs.getVal()%constRhs.getVal());break;
                    case and:replaceVal=new ConstantInt(constLhs.getVal()& constRhs.getVal());break;
                    case or:replaceVal=new ConstantInt(constLhs.getVal() | constRhs.getVal());break;
                    case xor:replaceVal=new ConstantInt(constLhs.getVal() ^ constRhs.getVal());break;
                    case shl:replaceVal=new ConstantInt(constLhs.getVal()<<constRhs.getVal());break;
                    case shr:replaceVal=new ConstantInt(constLhs.getVal()>>constRhs.getVal());break;
                }
            }

        }else if(inst instanceof IcmpInst){
            var lhs=((IcmpInst) inst).getLhs();
            var rhs=((IcmpInst) inst).getRhs();
            if (lhs instanceof ConstantInt && rhs instanceof ConstantInt) {
                var constLhs=((ConstantInt) lhs).getVal();
                var constRhs=((ConstantInt) rhs).getVal();
                switch (inst.getOpcode()){
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
                switch (inst.getOpcode()){
                    case EQ:replaceVal=new ConstantBool(constLhs==constRhs);break;
                    case NE:replaceVal=new ConstantBool(constLhs!=constRhs);break;
                }
            }else if(lhs instanceof ConstantNull && rhs instanceof ConstantNull){
                switch (inst.getOpcode()){
                    case EQ:replaceVal=new ConstantBool(true);
                    case NE:replaceVal=new ConstantBool(false);
                }
            }
        } else if (inst instanceof CastInst) {
            var source=inst.getOperands().get(0).getVal();
            if (source instanceof ConstantNull) {
                replaceVal=new ConstantNull();
            } else if (inst.getType().equals(source.getType())) {
                replaceVal=source;
            }
        }
        if(replaceVal!=null) {
            inst.transferUses(replaceVal);
            inst.delete();
        }
    }
}
