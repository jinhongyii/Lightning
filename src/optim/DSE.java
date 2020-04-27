package optim;

import IR.*;
import IR.instructions.CallInst;
import IR.instructions.LoadInst;
import IR.instructions.ReturnInst;
import IR.instructions.StoreInst;
import Riscv.Store;


import java.io.IOException;
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
//        try {
//            new IRPrinter(function.getParent(),"dse.ll",true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
                if (checkDeadStore( (StoreInst) inst)) {
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
    private boolean checkDeadStore( StoreInst store){

        for (var inst = store.getNext(); inst != null; inst = inst.getNext()) {
            var modRef=checkModRef(store,inst);
            if (modRef == AliasAnalysis.ModRef.Ref|| modRef== AliasAnalysis.ModRef.ModRef) {
                return false;
            } else if (modRef == AliasAnalysis.ModRef.Mod) {
                if (inst instanceof StoreInst && aa.alias(((StoreInst) inst).getPtr(), store.getPtr()) == AliasAnalysis.AliasResult.MustAlias) {
                    return true;
                }
            }
            if (inst instanceof ReturnInst) {
                return function.getName().equals("main");
            }
        }
        HashSet<BasicBlock> visited=new HashSet<>();
        for (var suc : store.getParent().getSuccessors()) {
            if (!checkDeadStore(suc, store, visited)) {
                return false;
            }
        }
        return true;
    }
    private boolean checkDeadStore(BasicBlock curBB, StoreInst store, HashSet<BasicBlock> visited) {

        if (visited.contains(curBB)) {
            return true;
        }else {
            visited.add(curBB);
        }

        for (var inst = curBB.getHead(); inst != null; inst = inst.getNext()) {
            if (inst == store || inst instanceof  ReturnInst) {
                return function.getName().equals("main");
            }
            var modRef=checkModRef(store,inst);
            if (modRef == AliasAnalysis.ModRef.Ref|| modRef== AliasAnalysis.ModRef.ModRef) {
                return false;
            } else if (modRef == AliasAnalysis.ModRef.Mod) {
                if (inst instanceof StoreInst && aa.alias(((StoreInst) inst).getPtr(), store.getPtr()) == AliasAnalysis.AliasResult.MustAlias) {
                    return true;
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

    private AliasAnalysis.ModRef checkModRef(StoreInst storeInst, Instruction value) {
        if (value instanceof CallInst ) {
            var funcName=((CallInst) value).getCallee().getName();
            switch (funcName) {
                case "_array_size":
                    return AliasAnalysis.ModRef.Ref;
                case "malloc":
                    return AliasAnalysis.ModRef.NoModRef;
            }
        }
        return aa.getModRefInfo(value, storeInst.getPtr()) ;
    }
}
