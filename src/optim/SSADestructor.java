package optim;

import IR.BasicBlock;
import IR.Function;
import IR.instructions.BranchInst;
import IR.instructions.MovInst;
import IR.instructions.PhiNode;

import java.util.ArrayList;
import java.util.HashSet;

public class SSADestructor extends FunctionPass {
    public SSADestructor(Function function) {
        super(function);
    }

    @Override
    public boolean run() {
        ArrayList<BasicBlock> postOrder=new ArrayList<>();
        calcPostOrder(function.getEntryBB(),postOrder, new HashSet<>());
        for (int i = postOrder.size() - 1; i >= 0; i--) {
            processPhi(postOrder.get(i));
        }
        return false;
    }
    private void processPhi(BasicBlock bb){
        if (!(bb.getHead() instanceof PhiNode)) {
            return;
        }
        for (var pred : bb.getPredecessors()) {
            var breakEdgeBB=function.addBB("critEdge");
            var predTerm=pred.getTerminator();
            if (((BranchInst) predTerm).isConditional()) {
                ((BranchInst) predTerm).setConditional(breakEdgeBB, bb);
            } else {
                ((BranchInst) predTerm).setUnconditional(breakEdgeBB);
            }
            for (var phi = bb.getHead(); phi instanceof PhiNode; phi = phi.getNext()) {
                var src=((PhiNode) phi).findValue(pred);
                breakEdgeBB.addInst(new MovInst(src,phi));
            }
            breakEdgeBB.addInst(new BranchInst(bb, null, null));

        }
    }
    private void calcPostOrder(BasicBlock cur,ArrayList<BasicBlock> bb, HashSet<BasicBlock> visited){
        if (visited.contains(cur)) {
            return;
        }
        visited.add(cur);
        for (var suc : cur.getSuccessors()) {
            calcPostOrder(suc, bb, visited);
        }
        bb.add(cur);
    }
}
