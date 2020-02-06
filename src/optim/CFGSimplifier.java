package optim;

import IR.BasicBlock;
import IR.ConstantBool;
import IR.Function;
import IR.Module;
import IR.instructions.BranchInst;

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
        if (basicBlock.getPredecessors().isEmpty() && !basicBlock.getName().equals("entry")) {
            basicBlock.delete();
            return true;
        }
        return change;
        //todo:merge bbs
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
