package optim;

import IR.Module;
import IR.*;
import IR.Types.PointerType;
import IR.instructions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Mem2reg extends FunctionPass {
    private DominatorAnalysis dominatorAnalyzer;
    private HashMap<BasicBlock,HashMap<AllocaInst, PhiNode>> newPhiInsts=new HashMap<>();
    public Mem2reg(Function function, DominatorAnalysis dominatorAnalysis){
        super(function);
        this.dominatorAnalyzer=dominatorAnalysis;
    }
    @Override
    public boolean run() {
//        dominatorAnalyzer.run();
        newPhiInsts.clear();
        vis.clear();
        ArrayList<AllocaInst> allocaInsts=new ArrayList<>();
        BasicBlock entryBlock=function.getEntryBB();
        for (var inst =entryBlock.getHead();inst!=null;inst=inst.getNext()) {
            if (inst instanceof AllocaInst) {
                if (inst.getUse_tail()==null) {
//                    inst.delete();
                }else {
                    allocaInsts.add((AllocaInst) inst);
//                    inst.delete();
                }
            }
        }
        for (var alloca : allocaInsts) {
            LinkedList<BasicBlock> defBB = new LinkedList<>();
            LinkedList<BasicBlock> useBB =new LinkedList<>();
            for (var defOrUse =alloca.getUse_head();defOrUse!=null;defOrUse=defOrUse.getNext()) {
                if (defOrUse.getUser() instanceof StoreInst) {
                    defBB.add(((Instruction)defOrUse.getUser()).getParent());
                } else if (defOrUse.getUser() instanceof LoadInst) {
                    useBB.addLast(((Instruction)defOrUse.getUser()).getParent());
                }
            }
            HashSet<BasicBlock> defbbSet = new HashSet<>(defBB);
            HashSet<BasicBlock> phiset = new HashSet<>();
            while (!defBB.isEmpty()) {
               var bb=defBB.pollLast();
               var df=dominatorAnalyzer.DominatorFrontier.get(bb);
                for (var i : df) {
                    newPhiInsts.computeIfAbsent(i, k -> new HashMap<>());
                    if (newPhiInsts.get(i).get(alloca) == null) {
                        newPhiInsts.get(i).put(alloca,new PhiNode(alloca.getName(), ((PointerType)alloca.getType()).getPtrType()) );
                        defBB.addLast(i);
                    }
                    phiset.add(i);
                }
            }

            var liveset= getLiveSet(alloca,defbbSet,useBB);
            for (var bb : phiset) {
                if (!liveset.contains(bb)) {
                    newPhiInsts.get(bb).remove(alloca);
                }
            }
            vis.clear();
            renameVars(function.getEntryBB(), null,null,alloca);
            //don't transfer use because load and stores have been deleted
            alloca.delete();
        }

        for (var i : newPhiInsts.entrySet()) {
            for(var phi:i.getValue().values()) {
                i.getKey().addInstToFirst(phi);
            }
        }
        fixNullValueInPhi();
        return true;
    }

    private void fixNullValueInPhi() {
        for (var bbPhi : newPhiInsts.entrySet()) {
            for (var phi : bbPhi.getValue().entrySet()) {
                int notnullcnt=0;
                int theOnlyNotNull = 0;
                var ops=phi.getValue().getOperands();
                for(int i=0;i<ops.size();i+=2){
                    //check whether this constant null fits type
                    if(ops.get(i).getVal() ==null){
                        ops.get(i).setValue(Type.getNull(phi.getValue().getType()));
                    }else {
                        notnullcnt++;
                        theOnlyNotNull = i;
                    }
                }
                if (notnullcnt == 1) {
                    phi.getValue().transferUses(ops.get(theOnlyNotNull).getVal());
                    phi.getValue().delete();
                }
            }
        }
    }

    private HashSet<BasicBlock> getLiveSet(AllocaInst allocaInst, HashSet<BasicBlock> defbb, LinkedList<BasicBlock> usebb){
        HashSet<BasicBlock> liveSet=new HashSet<>();
        LinkedList<BasicBlock> worklist=new LinkedList<>();
        while (!usebb.isEmpty()) {
            var bb=usebb.pollLast();
            if (defbb.contains(bb)) {
                boolean DefBeforeUse=false;
                for (var inst = bb.getHead(); bb != null; inst=inst.getNext()) {
                    if (inst instanceof StoreInst && ((StoreInst) inst).getPtr() == allocaInst) {
                        DefBeforeUse=true;
                        break;
                    } else if (inst instanceof LoadInst && ((LoadInst) inst).getLoadTarget() == allocaInst) {
                        break;
                    }
                }
                if (!DefBeforeUse) {
                    worklist.addLast(bb);
                }
            } else {
                worklist.addLast(bb);
            }
        }
        while (!worklist.isEmpty()) {
            var bb=worklist.pollLast();

            if (!liveSet.contains(bb)) {
                liveSet.add(bb);
            } else {
                continue;
            }
            for (var pred : bb.getPredecessors()) {
                if (!defbb.contains(bb)) {
                    worklist.addLast(pred);
                }
            }
        }
        return liveSet;

    }
    private HashSet<BasicBlock> vis=new HashSet<>();
    private void renameVars(BasicBlock basicBlock, BasicBlock pred, Value value,AllocaInst alloca){
        var tmp=newPhiInsts.get(basicBlock);
        if(tmp!=null) {
            var phi = tmp.get(alloca);
            if (phi != null) {
                phi.addIncoming(value,pred);
                value=phi;
            }
        }
        if (vis.contains(basicBlock)) {
            return;
        }
        vis.add(basicBlock);
        for (var inst = basicBlock.getHead(); inst != null; inst = inst.getNext()) {
            if (inst instanceof LoadInst &&((LoadInst) inst).getLoadTarget()==alloca) {
                inst.transferUses(value);
                inst.delete();
            } else if (inst instanceof StoreInst && ((StoreInst) inst).getPtr() == alloca) {
                value=((StoreInst) inst).getStoreVal();
                //no transfer use because there's no use
                inst.delete();
            }
        }
        for (var suc : basicBlock.getSuccessors()) {
            renameVars(suc, basicBlock,value,alloca);
        }
    }
    public static void runOnModule(Module module,DominatorAnalysis dominatorAnalysis) {
        for (var func : module.getFunctionList()) {
            if (!func.isExternalLinkage()) {
                Mem2reg mem2reg=new Mem2reg(func, dominatorAnalysis);
                mem2reg.run();
            }
        }
    }
}
