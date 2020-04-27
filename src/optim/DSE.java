package optim;

import IR.BasicBlock;
import IR.Function;
import IR.instructions.StoreInst;


import java.util.HashSet;


public class DSE extends FunctionPass {
    AliasAnalysis aa;
    DominatorAnalysis dominatorAnalysis;
    Function function;
    boolean changed;
    public DSE(Function function,DominatorAnalysis dominatorAnalysis,AliasAnalysis aa) {
        super(function);
        this.function=function;
        this.aa=aa;
        this.dominatorAnalysis=dominatorAnalysis;
    }

    @Override
    public boolean run() {
        changed=false;
        runOnBasicblock(dominatorAnalysis.postTreeRoot.basicBlock,new HashSet<>());
        return changed;
    }

    private void runOnBasicblock(BasicBlock bb, HashSet<BasicBlock> visited){
        if (visited.contains(bb)) {
            return;
        }
        visited.add(bb);
        for (var inst = bb.getTail(); inst != null; ) {
            var tmp=inst;
            if (inst instanceof StoreInst) {
                if (checkDeadStore(bb, (StoreInst) inst, new HashSet<>())) {
                    inst.delete();
                    changed=true;
                }
            }
            inst=tmp.getPrev();
        }
        var node=dominatorAnalysis.postDomTree.get(bb);
        for (var son : node.children) {
            runOnBasicblock(son.basicBlock,visited);
        }
    }
    private boolean checkDeadStore(BasicBlock startBB, StoreInst store, HashSet<BasicBlock> visited) {

        var curBB=store.getParent();
        if (visited.contains(curBB)) {
            return true;
        }else {
            visited.add(curBB);
        }
        if (curBB == startBB) {
            for (var inst = store.getNext(); inst != null; inst = inst.getNext()) {
                if (aa.getModRefInfo(inst, store.getPtr()) != AliasAnalysis.ModRef.NoModRef) {
                    return false;
                }
            }
        } else {
            for (var inst = curBB.getHead(); inst != null; inst = inst.getNext()) {
                if (aa.getModRefInfo(inst, store.getPtr()) != AliasAnalysis.ModRef.NoModRef) {
                    return false;
                }
            }
        }
        for (var sub : curBB.getSuccessors()) {
            if (!checkDeadStore(sub, store, visited)) {
                return false;
            }
        }
        return true;

    }
}
