package optim;

import IR.BasicBlock;
import IR.Function;
import IR.Instruction;
import IR.instructions.*;

import java.util.HashSet;
import java.util.LinkedList;

public class ADCE extends FunctionPass {
    private LinkedList<Instruction> workList=new LinkedList<>();
    private HashSet<Instruction> markedSet =new HashSet<>();
    private HashSet<BasicBlock> usefulBB =new HashSet<>();
    private DominatorAnalysis dominatorAnalysis;
    private void markInst(Instruction instruction){
        if (!markedSet.contains(instruction)) {
            markedSet.add(instruction);
            workList.addLast(instruction);
        }
    }
    private void markTerminator(BasicBlock bb){
       markInst(bb.getTerminator());
    }
    private void markBBUseful(BasicBlock bb){
        usefulBB.add(bb);
    }
    public ADCE(Function function,DominatorAnalysis dominatorAnalysis) {
        super(function);
        this.dominatorAnalysis=dominatorAnalysis;
    }

    @Override
    public boolean run() {
        return adce();
    }
    private boolean adce(){
        initialize();
        mark();
        return sweep();
    }

    private void mark() {
        while (!workList.isEmpty()) {
            var inst=workList.pollLast();
            if (inst instanceof PhiNode) {
                for (int i = 0; i < inst.getOperands().size() / 2; i++) {
                    markTerminator(((PhiNode) inst).getBB(i));
                }
            }
            for (var use : inst.getOperands()) {
                var val=use.getVal();
                if (val instanceof Instruction) {
                    markInst((Instruction) val);
                }
            }
            var rdf=dominatorAnalysis.postDomFrontier.get(inst.getParent());
            for (var bb : rdf) {
                markTerminator(bb);
            }
        }
    }

    private BasicBlock findNearestUsefulBB(BasicBlock basicBlock){
        for (var node = dominatorAnalysis.postDomTree.get(basicBlock); node != null; node = node.idom) {
            if (usefulBB.contains(node.basicBlock)) {
                return node.basicBlock;
            }
        }
        return null;
    }
    private boolean sweep(){
        boolean changed=false;
        for (var bb = function.getHead(); bb != null; bb = bb.getNext()) {
            for (var inst = bb.getHead(); inst != null;) {
                var tmp= inst.getNext();
                if (!markedSet.contains(inst)) {
                    if (inst instanceof BranchInst && ((BranchInst) inst).isConditional()) {
                        changed=true;
                        ((BranchInst) inst).setUnconditional(findNearestUsefulBB(bb));
                    } else if (!(inst instanceof BranchInst)) {
                        //no need to transfer use
                        inst.delete();
                        changed=true;
                    }
                }
                inst=tmp;
            }
        }
        return changed;
    }
    private void initialize() {
        for (var bb = function.getHead(); bb != null; bb = bb.getNext()) {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if(isCritical(inst)){
                    markInst(inst);
                    markBBUseful(bb);
                }
            }
        }
    }

    private boolean isCritical(Instruction inst) {
        //todo: deal with functions that don't write to memory
        return inst instanceof StoreInst || inst instanceof ReturnInst || inst instanceof CallInst;
    }
}
