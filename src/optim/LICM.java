package optim;

import IR.Module;
import IR.*;
import IR.Types.PointerType;
import IR.instructions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
public class LICM extends FunctionPass implements IRVisitor {
    LoopAnalysis loopAnalysis;
    AliasAnalysis aliasAnalysis;
    DominatorAnalysis dominatorAnalysis;
    private HashSet<Instruction> invariableSet =new HashSet<>();
    private LoopAnalysis.Loop curLoop;
    private boolean changed;
    private HashMap<Value,AllocaInst> replaceMap =new HashMap<>();
    LICM(Function function,LoopAnalysis loopAnalysis,DominatorAnalysis dominatorAnalysis,AliasAnalysis aliasAnalysis) {
        super(function);
        this.aliasAnalysis=aliasAnalysis;
        invariableSet.clear();
        curLoop=null;
        this.loopAnalysis=loopAnalysis;
        this.dominatorAnalysis=dominatorAnalysis;
    }

    @Override
    public boolean run() {
        changed=false;
        for (var topLoop : loopAnalysis.topLoops) {
            hoistLoop(topLoop);
        }
        return changed;
    }
    private boolean isInvariable(Value value){
        if (invariableSet.contains(value)) {
            return true;
        }
        if (value instanceof Instruction) {
            return !curLoop.contains(((Instruction) value).getParent());
        }
        return true;
    }

    private void hoistLoop(LoopAnalysis.Loop loop) {
        for (var subLoop : loop.subLoops) {
            hoistLoop(subLoop);
        }
        curLoop=loop;
        replaceMap.clear();
        var headerNode=dominatorAnalysis.DominantTree.get(loop.header);
        hoistBB(headerNode);
        doPromotion();
        invariableSet.clear();
    }
    private void hoistBB(DominatorAnalysis.Node node){
        BasicBlock bb = node.basicBlock;
        if (!curLoop.contains(bb)) {
            return;
        }
        if (loopAnalysis.loopMap.get(bb) == curLoop) {
            for (var inst = bb.getHead(); inst != null;) {
                var tmp=inst.getNext();
                visit(inst);
                inst=tmp;
            }
        }
        for (var child : node.children) {
            hoistBB(child);
        }
    }
    private boolean isTrappingInst(Instruction instruction){
        if (instruction instanceof BinaryOpInst) {
            var opcode=instruction.getOpcode();
            if (opcode == Instruction.Opcode.div || opcode == Instruction.Opcode.rem) {
                for (var exitBB : curLoop.exitBlocks) {
                    var domNode=dominatorAnalysis.DominantTree.get(instruction.getParent());
                    if (!domNode.dominate(dominatorAnalysis.DominantTree.get(exitBB))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private void hoist(Instruction instruction){
        changed=true;
        instruction.detach();
        instruction.setPrev(null);
        var preHeader=curLoop.getPreHeader();
        preHeader.attachBefore(preHeader.getTerminator(),instruction);
    }

    @Override
    public Object visitModule(Module module) {
        return null;
    }

    @Override
    public Object visitFunction(Function function) {
        return null;
    }

    @Override
    public Object visitBasicBlock(BasicBlock basicBlock) {
        return null;
    }

    @Override
    public Object visitGlobalVariable(GlobalVariable globalVariable) {
        return null;
    }

    @Override
    public Object visitAllocaInst(AllocaInst allocaInst) {
        return null;
    }

    @Override
    public Object visitBinaryOpInst(BinaryOpInst binaryOpInst) {
        var lhs=binaryOpInst.getLhs();
        var rhs=binaryOpInst.getRhs();
        if (isInvariable(lhs) && isInvariable(rhs) && !isTrappingInst(binaryOpInst)) {
            hoist(binaryOpInst);
            invariableSet.add(binaryOpInst);
        }
        return null;
    }

    @Override
    public Object visitBranchInst(BranchInst branchInst) {
        return null;
    }

    @Override
    public Object visitCallInst(CallInst callInst) {
        var callee= callInst.getCallee();
        if (aliasAnalysis.getFunctionModRefInfo(callee) == AliasAnalysis.ModRef.NoModRef) {
            for (var param : callInst.getParams()) {
                if (!isInvariable(param)) {
                    return null;
                }
            }
            hoist(callInst);
            invariableSet.add(callInst);
        }
        return null;
    }

    @Override
    public Object visitCastInst(CastInst castInst) {
        return null;
    }

    @Override
    public Object visitGEPInst(GetElementPtrInst GEPInst) {
        for (var operand : GEPInst.getOperands()) {
            if (!isInvariable(operand.getVal())) {
                return null;
            }
        }
        hoist(GEPInst);
        invariableSet.add(GEPInst);
        return null;
    }

    @Override
    public Object visitIcmpInst(IcmpInst icmpInst) {
        var lhs=icmpInst.getLhs();
        var rhs=icmpInst.getRhs();
        if (isInvariable(lhs) && isInvariable(rhs)) {
            hoist(icmpInst);
            invariableSet.add(icmpInst);
        }
        return null;
    }



    private boolean NotModifiedInLoop(Value ptr){
        for (var bb : curLoop.basicBlocks) {

                for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                    AliasAnalysis.ModRef modRefInfo = aliasAnalysis.getModRefInfo(inst, ptr);
                    if(modRefInfo == AliasAnalysis.ModRef.ModRef || modRefInfo== AliasAnalysis.ModRef.Mod){
                        return false;
                    }
                }

        }
        return true;
    }

    @Override
    public Object visitLoadInst(LoadInst loadInst) {
        Value ptr = loadInst.getLoadTarget();
        if (isInvariable(ptr) && NotModifiedInLoop(ptr)) {
            hoist(loadInst);
            invariableSet.add(loadInst);
        }
        return null;
    }

    @Override
    public Object visitPhiNode(PhiNode phiNode) {
        return null;
    }

    @Override
    public Object visitReturnInst(ReturnInst returnInst) {
        return null;
    }

    @Override
    public Object visitStoreInst(StoreInst storeInst) {
        var ptr=storeInst.getPtr();
        if (isInvariable(ptr) && !checkMayAlias(ptr)) {
            AllocaInst allocaInst=null;
            for (var v : replaceMap.entrySet()) {
                if (aliasAnalysis.alias(v.getKey(), ptr) == AliasAnalysis.AliasResult.MustAlias) {
                    allocaInst=v.getValue();
                }
            }
            if (allocaInst == null) {
                allocaInst=new AllocaInst("tmp.alloca", ((PointerType)ptr.getType()).getPtrType());
                function.getEntryBB().addInstToFirst(allocaInst);
            }
            replaceMap.put(ptr,allocaInst);
            changed=true;
        }
        return null;
    }

    @Override
    public Object visitMovInst(MovInst movInst) {
        return null;
    }

    private boolean checkMayAlias(Value ptr){
        for (var bb : curLoop.basicBlocks) {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if (inst instanceof LoadInst) {
                    if (aliasAnalysis.alias(ptr, ((LoadInst) inst).getLoadTarget())== AliasAnalysis.AliasResult.MayAlias) {
                        return true;
                    }
                } else if (inst instanceof StoreInst) {
                    if (aliasAnalysis.alias(ptr, ((StoreInst) inst).getPtr()) == AliasAnalysis.AliasResult.MayAlias) {
                        return true;
                    }
                } else if (inst instanceof CallInst) {
                    if (aliasAnalysis.getModRefInfo(inst, ptr) != AliasAnalysis.ModRef.NoModRef) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private void doPromotion(){

        HashMap<AllocaInst,Value> realMemLoc=new HashMap<>();
        HashSet<BasicBlock> exitBB=new HashSet<>();
        var preheader=curLoop.getPreHeader();
        var pre_terminator=preheader.getTerminator();
        for (var entry: replaceMap.entrySet()) {
            if (!realMemLoc.containsKey(entry.getValue()) && isInvariable(entry.getKey())) {
                var loadInst=new LoadInst("promoted_load", entry.getKey());
                preheader.addInstBefore(pre_terminator,loadInst);
                var storeInst=new StoreInst(loadInst,entry.getValue());
                preheader.addInstBefore(pre_terminator,storeInst);
                realMemLoc.put(entry.getValue(),entry.getKey());
            }
        }
        for (var bb : curLoop.basicBlocks) {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if (inst instanceof LoadInst) {
                    for (var entry : replaceMap.entrySet()) {
                        if (aliasAnalysis.alias(entry.getKey(), ((LoadInst) inst).getLoadTarget())== AliasAnalysis.AliasResult.MustAlias) {
                            var alloca=entry.getValue();
                            if(alloca!=null) {
                                inst.getOperands().get(0).setValue(alloca);
                            }
                            break;
                        }
                    }

                } else if (inst instanceof StoreInst) {
                    for (var entry : replaceMap.entrySet()) {
                        if (aliasAnalysis.alias(entry.getKey(), ((StoreInst) inst).getPtr()) == AliasAnalysis.AliasResult.MustAlias) {
                            var alloca = entry.getValue();
                            if (alloca != null) {
                                inst.getOperands().get(1).setValue(alloca);
                            }
                            break;
                        }
                    }

                }
            }
            for (var suc : bb.getSuccessors()) {
                if (curLoop.basicBlocks.contains(suc) || exitBB.contains(suc)) {
                    continue;
                }
                exitBB.add(suc);
                var insertPos=suc.getHead();
                while (insertPos instanceof PhiNode) {
                    insertPos = insertPos.getNext();
                }
                for (var entry : realMemLoc.entrySet()) {
                    var loadInst=new LoadInst("promote_exit", entry.getKey());
                    suc.addInstBefore(insertPos, loadInst);
                    suc.addInstBefore(insertPos,new StoreInst(loadInst,entry.getValue()));
                }
            }

        }

        Mem2reg mem2reg=new Mem2reg(function,dominatorAnalysis);
        mem2reg.run();


    }
    @Override
    public Object visit(Value value) {
        return value.accept(this);
    }
}
