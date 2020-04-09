package optim;

import IR.BasicBlock;
import IR.Function;
import IR.Instruction;
import IR.instructions.BranchInst;
import IR.instructions.MovInst;
import IR.instructions.PhiNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

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
        for (var bb = function.getHead(); bb != null; bb = bb.getNext()) {
            parallel2seq(bb);
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
                breakEdgeBB.addPCopy(new MovInst(src,phi));
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
    private void parallel2seq(BasicBlock basicBlock){
        var seq=new LinkedList<MovInst>();
        while (!check(basicBlock)) {
            var processed=false;
            for (var copy : basicBlock.getPcopys()) {
                var b=copy.getTo();
                boolean flag = true;
                for (var other : basicBlock.getPcopys()) {
                    if (b == other.getFrom()) {
                        flag=false;
                        break;
                    }
                }
                if(flag) {
                    seq.add(copy);
                    basicBlock.getPcopys().remove(copy);
                    processed=true;
                    break;
                }
            }
            if (processed) {
                continue;
            }
            MovInst copy=null;
            for (var i : basicBlock.getPcopys()) {
                if (i.getTo() != i.getFrom()) {
                    copy = i;
                }
            }
            assert copy != null;
            var tmpLocal=new Instruction();
            seq.add(new MovInst(copy.getFrom(),tmpLocal));
            basicBlock.getPcopys().remove(copy);
            basicBlock.addPCopy(new MovInst(tmpLocal,copy.getTo()));
        }
        while (!seq.isEmpty()) {
            basicBlock.addInstToFirst(seq.removeLast());
        }
    }
    private boolean check(BasicBlock basicBlock){
        for (var copy : basicBlock.getPcopys()) {
            if (copy.getFrom() != copy.getTo()) {
                return false;
            }
        }
        return true;
    }
}
