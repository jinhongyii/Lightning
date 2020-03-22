package optim;

import IR.Module;
import IR.*;
import IR.instructions.BranchInst;
import IR.instructions.PhiNode;

import java.util.HashMap;
//todo: a lot
public class CFGSimplifier extends FunctionPass {
    public  CFGSimplifier(Function function) {
        super(function);
    }
    static public void runOnModule(Module module){
        for (var func : module.getFunctionList()) {
            if(!func.isExternalLinkage()) {
                CFGSimplifier cfgSimplifier = new CFGSimplifier(func);
                cfgSimplifier.run();
            }
        }
    }
    @Override
    public boolean run() {
        boolean changed=true;
        boolean realChanged=false;
        while(changed) {
            changed=false;
            for (var bb = function.getHead(); bb != null; ) {
                var tmp = bb.getNext();
                changed|=simplify(bb);
                realChanged|=changed;
                bb = tmp;
            }
        }
        return realChanged;
    }
    private boolean simplify(BasicBlock basicBlock){
        boolean change=false;
        change |= constantCondition(basicBlock);
        if (function.getEntryBB() == basicBlock) {
            return change;
        }
        if (basicBlock.getPredecessors().isEmpty()) {
            basicBlock.delete();
            return true;
        }
        if (basicBlock.getPredecessors().size() == 1) {
            var pred=basicBlock.getPredecessors().get(0);
            if (pred.getSuccessors().size() == 1 &&pred!=basicBlock) {
                basicBlock.mergetoBB(pred);
                return true;
            }
        }
        var inst = basicBlock.getHead();
        while (inst instanceof PhiNode) {
            inst = inst.getNext();
        }
        if (inst instanceof BranchInst && !((BranchInst) inst).isConditional()) {
            var theOnlySuccessor=basicBlock.getSuccessors().get(0);
            if (notifySuccessor(basicBlock, theOnlySuccessor)) {
                basicBlock.transferUses(theOnlySuccessor);
                basicBlock.delete();
                return true;
            }
        }

        return change;
    }
    private boolean notifySuccessor(BasicBlock pred,BasicBlock suc){
        //check whether suc's predecessor conflict with pred's
        //if so,check whether they have the same value in phi insts
        //  if they have different values, we can't merge pred block into suc ,so return false .
        for (var suc_pred : suc.getPredecessors()) {
            var pred_preds=pred.getPredecessors();
            if (pred_preds.contains(suc_pred)) {
                for (var inst = suc.getHead(); inst instanceof PhiNode; inst = inst.getNext()) {
                    Value pred_val=((PhiNode) inst).findValue(pred);
                    Value suc_pred_val=((PhiNode) inst).findValue(suc_pred);
                    if (pred_val != suc_pred_val) {
                        if (pred_val instanceof PhiNode && ((PhiNode) pred_val).getParent() == pred) {
                            pred_val = ((PhiNode) pred_val).findValue(suc_pred);
                            if (pred_val != suc_pred_val) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        //now we've checked all predecessors, we are going to put replace
        for (var inst = suc.getHead(); inst instanceof PhiNode; inst = inst.getNext()) {
            HashMap<BasicBlock,Value> phiMap =new HashMap<>();
            for (int i = 0; i < inst.getOperands().size() / 2; i++) {
                phiMap.put(((PhiNode) inst).getBB(i), ((PhiNode) inst).getValue(i));
            }
            var pred_val=phiMap.get(pred);
            if (pred_val instanceof PhiNode && ((PhiNode) pred_val).getParent() == pred) {
                var pred_phi = (PhiNode) pred_val;
                for (int i = 0; i < pred_phi.getOperands().size() / 2; i++) {
                    var bb = pred_phi.getBB(i);
                    var val = pred_phi.getValue(i);
                    if (!phiMap.containsKey(bb)) {
                        ((PhiNode) inst).addIncoming(val, bb);
                    }
                }
            } else {
                for (var pred_pred : pred.getPredecessors()) {
                    if (!phiMap.containsKey(pred_pred)) {
                        ((PhiNode) inst).addIncoming(pred_val,pred_pred);
                    }
                }
            }
            ((PhiNode) inst).removeIncoming(pred);
        }
        return true;
    }
    private boolean constantCondition(BasicBlock basicBlock) {
        var terminator=basicBlock.getTerminator();
        if (terminator instanceof BranchInst) {
            if (((BranchInst) terminator).isConditional()) {
                var cond=((BranchInst) terminator).getCondition();
                var dst1=((BranchInst) terminator).getDstThen();
                var dst2=((BranchInst) terminator).getDstElse();
                BasicBlock newdst=null;
                if (cond instanceof ConstantBool) {
                    newdst=((ConstantBool) cond).isTrue()?dst1:dst2;
                    if (((ConstantBool) cond).isTrue()) {
                        for (var inst = dst2.getHead(); inst instanceof PhiNode; inst = inst.getNext()) {
                            ((PhiNode) inst).removeIncoming(basicBlock);
                        }
                    } else {
                        for (var inst = dst1.getHead(); inst instanceof PhiNode; inst = inst.getNext()) {
                            ((PhiNode) inst).removeIncoming(basicBlock);
                        }
                    }
                } else if (dst1 == dst2) {
                    newdst=dst1;
                }
                if (newdst != null) {
                    ((BranchInst) terminator).setUnconditional(newdst);
                    return true;
                }
            }
        }
        return false;
    }
}
