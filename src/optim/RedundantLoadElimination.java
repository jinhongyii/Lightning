package optim;

import IR.BasicBlock;
import IR.Function;
import IR.Instruction;
import IR.Value;
import IR.instructions.LoadInst;
import IR.instructions.StoreInst;
import edu.princeton.cs.algs4.BinaryStdIn;

import java.util.ArrayList;
import java.util.HashSet;

public class RedundantLoadElimination extends  FunctionPass {
    AliasAnalysis aliasAnalysis;
    DominatorAnalysis dominatorAnalysis;
    boolean changed;
    public RedundantLoadElimination(Function function,DominatorAnalysis dominatorAnalysis,AliasAnalysis aliasAnalysis) {
        super(function);
        this.dominatorAnalysis=dominatorAnalysis;
        this.aliasAnalysis=aliasAnalysis;
    }

    @Override
    public boolean run() {
        changed=false;
        handleNode(dominatorAnalysis.treeRoot);
        return changed;
    }
    private void getValidLoad(Instruction startPoint,Value targetPtr,ArrayList<LoadInst> validLoad){
        for (var use = targetPtr.getUse_head(); use != null; use = use.getNext()) {
            if (use.getUser() instanceof LoadInst && use.getUser()!=startPoint) {
                if (!checkMod(startPoint, (LoadInst) use.getUser(), targetPtr)) {
                    validLoad.add((LoadInst) use.getUser());
                }
            }
        }
    }
    private boolean checkMod(Instruction startPoint,LoadInst endPoint,Value targetPtr){
        if (dominatorAnalysis.dominate(startPoint, endPoint)) {
            var startBB=startPoint.getParent();
            var endBB=(endPoint).getParent();
            if (startBB != endBB) {
                for (var inst = startPoint.getNext(); inst != null; inst = inst.getNext()) {
                    var modRef = aliasAnalysis.getModRefInfo(inst, targetPtr);
                    if (modRef == AliasAnalysis.ModRef.Mod || modRef == AliasAnalysis.ModRef.ModRef) {
                        return true;
                    }
                }
                for (var inst = endBB.getHead(); inst != endPoint; inst = inst.getNext()) {
                    var modRef = aliasAnalysis.getModRefInfo(inst, targetPtr);
                    if (modRef == AliasAnalysis.ModRef.Mod || modRef == AliasAnalysis.ModRef.ModRef) {
                        return true;
                    }
                }
                HashSet<BasicBlock> visited = new HashSet<>();
                if (checkMod(startBB, endBB, targetPtr, visited)) {
                    return true;
                }
            } else {
                for (var inst = startPoint.getNext(); inst != endPoint; inst = inst.getNext()) {
                    var modRef = aliasAnalysis.getModRefInfo(inst, targetPtr);
                    if (modRef == AliasAnalysis.ModRef.Mod || modRef == AliasAnalysis.ModRef.ModRef) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }
    private boolean checkMod(BasicBlock startPoint, BasicBlock curBlock, Value ptr, HashSet<BasicBlock> visited){
        if (visited.contains(curBlock) || startPoint == curBlock) {
            return false;
        }
        visited.add(curBlock);
        for (var inst = curBlock.getHead(); inst != null; inst = inst.getNext()) {
            var modRef=aliasAnalysis.getModRefInfo(inst,ptr);
            if(modRef== AliasAnalysis.ModRef.Mod || modRef== AliasAnalysis.ModRef.ModRef){
                return true;
            }
        }
        for (var pred : curBlock.getPredecessors()) {
            if(checkMod(startPoint,pred,ptr,visited)){
                return true;
            }
        }
        return false;
    }
    private ArrayList<LoadInst> getEquivalentValue(LoadInst inst){
        var ptr=inst.getLoadTarget();
        ArrayList<LoadInst> validEquivalentLoad=new ArrayList<>();
        getValidLoad(inst,ptr,validEquivalentLoad);
        return validEquivalentLoad;
    }
    private ArrayList<LoadInst> getEquivalentValue(StoreInst inst){
        var ptr=inst.getPtr();
        ArrayList<LoadInst> validEquivalentLoad=new ArrayList<>();
        getValidLoad(inst,ptr,validEquivalentLoad);
        return validEquivalentLoad;
    }
    private void handleNode(DominatorAnalysis.Node node){
        var bb=node.basicBlock;
        for(var inst=bb.getHead();inst!=null;){
            var tmp=inst.getNext();
            if (inst instanceof LoadInst) {
                var equiv=getEquivalentValue((LoadInst) inst);
                for (var load : equiv) {
                    load.transferUses(inst);
                    load.delete();
                    changed=true;
                }
            } else if (inst instanceof StoreInst) {
                var equiv = getEquivalentValue((StoreInst) inst);
                for (var load : equiv) {
                    load.transferUses(((StoreInst) inst).getStoreVal());
                    load.delete();
                    changed=true;
                }
            }
            inst=tmp;
        }
        for (var child : node.children) {
            handleNode(child);
        }
    }
}
