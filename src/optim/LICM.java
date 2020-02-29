package optim;

import IR.*;
import IR.Module;
import IR.instructions.*;
import backend.IRBuilder;

import java.util.HashSet;

public class LICM extends FunctionPass implements IRVisitor {
    LoopAnalysis loopAnalysis;
    AliasAnalysis aliasAnalysis;
    DominatorAnalysis dominatorAnalysis;
    private HashSet<Instruction> invariableSet =new HashSet<>();
    private LoopAnalysis.Loop curLoop;
    private boolean changed;
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
        var headerNode=dominatorAnalysis.DominantTree.get(loop.header);
        hoistBB(headerNode);
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
        if (isInvariable(lhs) && isInvariable(rhs)) {
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
        //todo:hoist functions with no side effect
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
        //todo:move useless load inst out
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
        return null;
    }

    @Override
    public Object visit(Value value) {
        return value.accept(this);
    }
}
