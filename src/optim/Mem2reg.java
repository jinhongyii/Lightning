package optim;

import IR.*;
import IR.Module;
import IR.Types.PointerType;
import IR.instructions.AllocaInst;
import IR.instructions.LoadInst;
import IR.instructions.PhiNode;
import IR.instructions.StoreInst;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Mem2reg extends FunctionPass {
    private DominatorAnalysis dominatorAnalyzer=new DominatorAnalysis(function);
    private HashMap<BasicBlock,HashMap<AllocaInst, PhiNode>> newPhiInsts=new HashMap<>();
    public Mem2reg(Function function){
        super(function);
    }
    @Override
    public void run() {
        dominatorAnalyzer.run();
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
            HashSet<BasicBlock> deadset = new HashSet<>();
            while (!defBB.isEmpty()) {
               var bb=defBB.pollLast();
               var df=dominatorAnalyzer.DominatorFrontier.get(bb);
                for (var i : df) {
                    newPhiInsts.computeIfAbsent(i, k -> new HashMap<>());
                    if (newPhiInsts.get(i).get(alloca) == null) {
                        newPhiInsts.get(i).put(alloca,new PhiNode(alloca.getName(), ((PointerType)alloca.getType()).getPtrType()) );
                        defBB.addLast(i);
                    }
                    deadset.add(i);
                }
            }
            vis.clear();
            for (var bb : useBB) {
                checkDeadPhi(bb,deadset, alloca);
            }
            for (var dead : deadset) {
                newPhiInsts.get(dead).remove(alloca);
            }
            vis.clear();
            renameVars(function.getEntryBB(), null,null,alloca);

            alloca.delete();
        }

        for (var i : newPhiInsts.entrySet()) {
            for(var phi:i.getValue().values()) {
                i.getKey().addInstToFirst(phi);
            }
        }
        fixNullValueInPhi();

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
                        if (phi.getValue().getType().equals(Type.TheInt1)) {
                            ops.get(i).setValue(new ConstantBool(false));
                        } else if (phi.getValue().getType().equals(Type.TheInt64)) {
                            ops.get(i).setValue(new ConstantInt(0));
                        } else {
                            ops.get(i).setValue(new ConstantNull());
                        }
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

    private void checkDeadPhi(BasicBlock basicBlock, HashSet<BasicBlock> deadset, AllocaInst allocaInst){
        for(var node=dominatorAnalyzer.DominantTree.get(basicBlock);node!=null;node=node.idom) {
            basicBlock=node.basicBlock;
            if (newPhiInsts.containsKey(basicBlock) && newPhiInsts.get(basicBlock).containsKey(allocaInst) && deadset.contains(basicBlock)) {
                deadset.remove(basicBlock);
                for (var pred : basicBlock.getPredecessors()) {
                    checkDeadPhi(pred, deadset, allocaInst);
                }
            }
        }


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
                inst.delete();
            }
        }
        for (var suc : basicBlock.getSuccessors()) {
            renameVars(suc, basicBlock,value,alloca);
        }
    }
    public static void runOnModule(Module module) {
        for (var func : module.getFunctionList()) {
            if (!func.isExternalLinkage()) {
                Mem2reg mem2reg=new Mem2reg(func);
                mem2reg.run();
            }
        }
    }
}
