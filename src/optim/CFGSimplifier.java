package optim;

import IR.BasicBlock;
import IR.ConstantBool;
import IR.Function;
import IR.Module;
import IR.instructions.BranchInst;

public class CFGSimplifier extends FunctionPass {
    CFGSimplifier(Function function) {
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
    void run() {
        for (var bb = function.getHead(); bb != null;) {
            var tmp=bb.getNext();
            simplify(bb);
            bb=tmp;
        }
    }
    private void simplify(BasicBlock basicBlock){
        if (basicBlock.getPredecessors().isEmpty() && !basicBlock.getName().equals("entry")) {
            basicBlock.delete();
        }
        var terminator=basicBlock.getTerminator();
        if (terminator instanceof BranchInst) {
            if (((BranchInst) terminator).isConditional()) {
                var cond=((BranchInst) terminator).getCondition();
                var dst1=((BranchInst) terminator).getDstThen();
                var dst2=((BranchInst) terminator).getDstElse();
                BasicBlock newdst=null;
                if (cond instanceof ConstantBool) {
                    newdst=((ConstantBool) cond).isTrue()?dst1:dst2;
                } else if (dst1 == dst2) {
                    newdst=dst1;
                }
                if (newdst != null) {
                    ((BranchInst) terminator).setUnconditional(newdst);
                }
            }
        }
        //todo:merge bbs
    }
}
