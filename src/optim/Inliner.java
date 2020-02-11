package optim;

import IR.BasicBlock;
import IR.Function;
import IR.Value;
import IR.instructions.BranchInst;
import IR.instructions.CallInst;
import IR.instructions.ReturnInst;
import IR.Module;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Inliner implements Pass {
    private Module module;
    private final int instNumThreshold=100;
    private final int inlineDepth=2;
    private HashMap<Function,Integer> instNum=new HashMap<>();
    private HashSet<Function> recursiveSet=new HashSet<>();
    public Inliner(Module module){
        this.module=module;
    }
    public boolean run(){
        boolean changed=false;
        instNum.clear();
        recursiveSet.clear();
        for (var func : module.getFunctionList()) {
            if (!func.isExternalLinkage()) {
                getInstNum(func);
            }
        }
        for (var func : module.getFunctionList()) {
            changed|=doFunctionInline(func);
        }
        return changed;
    }
    private boolean doFunctionInline(Function function){
        boolean changed=false;
        if (function.isExternalLinkage()) {
            return false;
        }
        //deal with non-recursive function

        for (var bb = function.getHead(); bb != null; bb = bb.getNext()) {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if (inst instanceof CallInst) {
                    var callee = ((CallInst) inst).getCallee();
                    if (!callee.isExternalLinkage() && instNum.get(callee) < instNumThreshold && callee!=function && !recursiveSet.contains(callee)) {
                        changed = true;
                        doFunctionInline((CallInst) inst);
                    }
                }
            }
        }
        //deal with recursive function
        var lastBB=function.getLastBB();
        for(int i=0;i<inlineDepth;i++) {
            for (var bb = function.getHead(); bb != lastBB.getNext(); bb = bb.getNext()) {
                for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                    if (inst instanceof CallInst) {
                        var callee = ((CallInst) inst).getCallee();
                        if (!callee.isExternalLinkage() && instNum.get(callee) < instNumThreshold && callee==function) {
                            changed = true;
                            doFunctionInline((CallInst) inst);
                        }
                    }
                }
            }
        }
        getInstNum(function);
        return changed;
    }
    //return the exit block of the cloned function
    private BasicBlock cloneFunction(Function src, Function dst, ArrayList<Value> parameters){
        HashMap<Value,Value> renameMap=new HashMap<>();
        for (int i = 0; i < parameters.size(); i++) {
            renameMap.put(src.getArguments().get(i), parameters.get(i));
        }
        var src_tail=src.getTail();
        for (var origBB = src.getHead(); origBB != src_tail.getNext(); origBB = origBB.getNext()) {
            var newBB=dst.addBB(origBB.getName());
            renameMap.put(origBB,newBB);
            for (var inst = origBB.getHead(); inst != null; inst = inst.getNext()) {
                var cloneInst=inst.cloneInst();
                newBB.addInst(cloneInst);
                renameMap.put(inst,cloneInst);
            }
        }
        for (var origBB = src.getHead(); origBB != src_tail.getNext(); origBB = origBB.getNext()) {
            var newBB=(BasicBlock)renameMap.get(origBB);
            for (var inst = newBB.getHead(); inst != null; inst = inst.getNext()) {
                for (var use : inst.getOperands()) {
                    Value.ValueType valueType = use.getVal().getValueType();
                    if (valueType != Value.ValueType.ConstantVal && valueType != Value.ValueType.GlobalVariableVal) {
                        var replace=renameMap.get(use.getVal());
                        if(replace!=null) {
                            use.setValue(replace);
                        }
                    }
                }
            }
        }
        return (BasicBlock) renameMap.get(src.getReturnBB());
    }
    private void getInstNum(Function function){
        int cnt=0;
        for (var bb = function.getHead(); bb != null; bb = bb.getNext()) {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                cnt++;
                if (inst instanceof CallInst && ((CallInst) inst).getCallee() == function) {
                    recursiveSet.add(function);
                }
            }
        }
        instNum.put(function,cnt);
    }
    private void doFunctionInline(CallInst inst) {
        var originalBB=inst.getParent();
        Function function = originalBB.getParent();
        var splitBB=originalBB.split(inst);
        var lastBB=function.getLastBB();
        var newReturnBB=cloneFunction(inst.getCallee(), function,inst.getParams());
        var newEntryBB=lastBB.getNext();
        ((BranchInst)originalBB.getTerminator()).setUnconditional(newEntryBB);
        ReturnInst returnInst = (ReturnInst) newReturnBB.getTerminator();
        if (returnInst.hasRetValue()) {
            inst.transferUses(returnInst.getRetValue());
        }
        returnInst.delete();
        newReturnBB.addInst(new BranchInst(splitBB,null,null));
        inst.delete();
    }

}
