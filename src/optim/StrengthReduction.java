package optim;


import IR.*;
import IR.instructions.BinaryOpInst;
import IR.instructions.PhiNode;

import java.util.HashMap;
import java.util.HashSet;

public class StrengthReduction extends FunctionPass {
    LoopAnalysis loopAnalysis;
    DominatorAnalysis dominatorAnalysis;
    AliasAnalysis aa;
//    HashSet<InductionVariable> indVarSet=new HashSet<>();
    HashMap<Value,InductionVariable> indVarMap=new HashMap<>();
    boolean changed=false;
    public class InductionVariable{
        Value start=null;
        Value step=null;
        PhiNode phiNode;
        boolean sub=false;
        public boolean isValid(){
            return start!=null && step!=null;
        }
        public InductionVariable( Value start,Value step){
            this.step=step;
            this.start=start;
        }
        public InductionVariable(PhiNode phiNode, boolean enableSub) {
            this.phiNode=phiNode;
            var loop=loopAnalysis.loopMap.get(phiNode.getParent());
            var preheader=loop.getPreHeader();
            var preheaderTerminator=preheader.getTerminator();
            assert phiNode.getOperands().size()==4;
            assert loop.header==phiNode.getParent();
            if (!phiNode.getType().equals(Type.TheInt64)) {
                return;
            }
            var value0= phiNode.getValue(0);
            var value1=phiNode.getValue(1);
            if (loop.contains(phiNode.getBB(0))) {
                var tmp=value0;
                value0=value1;
                value1=tmp;
            }
            //preheader,backedge
            start=value0;
            if (value1 == phiNode) {
                step = new ConstantInt(0);
            } else {
                if (value1 instanceof BinaryOpInst ) {
                    var lhs=((BinaryOpInst) value1).getLhs();
                    var rhs=((BinaryOpInst) value1).getRhs();
                    if(((BinaryOpInst) value1).getOpcode()== Instruction.Opcode.add) {
                        if (lhs == phiNode && isInvariable(rhs, loop)) {
                            step = rhs;
                        } else if (rhs == phiNode && isInvariable(lhs, loop)) {
                            step = lhs;
                        }
                    } else if (((BinaryOpInst) value1).getOpcode() == Instruction.Opcode.sub) {
                        if (lhs == phiNode && isInvariable(rhs, loop)) {
                            if (!enableSub) {
                                var negInst = new BinaryOpInst("neg", Instruction.Opcode.sub, new ConstantInt(0), rhs);
                                preheader.addInstBefore(preheaderTerminator, negInst);
                                step = negInst;
                            } else {
                                sub=true;
                                step=rhs;
                            }
                        }
                    }
                }
            }
        }
    }
    public StrengthReduction(Function function,LoopAnalysis loopAnalysis,DominatorAnalysis dominatorAnalysis,AliasAnalysis aa) {
        super(function);
        this.dominatorAnalysis=dominatorAnalysis;
        this.loopAnalysis=loopAnalysis;
        this.aa=aa;
    }

    private boolean isInvariable(Value value, LoopAnalysis.Loop loop) {
        if (value instanceof Instruction) {
            return !loop.contains(((Instruction) value).getParent());
        }
        return true;
    }
    @Override
    public boolean run() {
        changed=false;
        for (var loop : loopAnalysis.topLoops) {
            performStrengthReduction(loop);
        }
        cleanup();
        for (var loop : loopAnalysis.topLoops) {
            removeRedundantPhi(loop);
        }
        return changed;
    }
    private void cleanup(){
        ADCE adce =new ADCE(function,dominatorAnalysis,aa);
        SCCP sccp=new SCCP(function);
        CSE cse=new CSE(function,dominatorAnalysis);
        boolean changed=true;
        while(changed) {
            changed = false;
            changed|=sccp.run();
            changed|=adce.run();
            changed|=cse.run();
        }
    }
    private void removeRedundantPhi(LoopAnalysis.Loop loop){
        for (var subLoop : loop.subLoops) {
            removeRedundantPhi(subLoop);
        }
        indVarMap.clear();
        findBasicInductionVariable(loop,true);
        HashSet<InductionVariable> ivs=new HashSet<>();
        for (var entry : indVarMap.entrySet()) {
            var newIV=entry.getValue();
            var newPhi=entry.getKey();
            boolean flag=true;
            for (var iv : ivs) {
                if (iv.step.equals(newIV.step) && iv.start.equals(newIV.start) && iv.sub == newIV.sub) {
                    newPhi.transferUses(iv.phiNode);
                    ((PhiNode) newPhi).delete();
                    changed=true;
                    flag=false;
                    break;
                }
            }
            if (flag) {
                ivs.add(newIV);
            }
        }
    }
    private void performStrengthReduction(LoopAnalysis.Loop loop){
        for (var subLoop : loop.subLoops) {
            performStrengthReduction(subLoop);
        }
        indVarMap.clear();
        findBasicInductionVariable(loop, false);
        findDerivedInductionVariable(dominatorAnalysis.DominantTree.get(loop.header),loop);
        for (var entry : indVarMap.entrySet()) {
            reduceStrength((Instruction) entry.getKey(),entry.getValue(),loop);
        }
    }
    private void findBasicInductionVariable(LoopAnalysis.Loop loop, boolean enableSub){
        var header=loop.getHeader();
        for (var inst = header.getHead(); inst instanceof PhiNode; inst = inst.getNext()) {
            var indVar=new InductionVariable((PhiNode) inst, enableSub);
            if (indVar.isValid()) {
                indVarMap.put(inst,indVar);
            }
        }
    }


    private void findDerivedInductionVariable(DominatorAnalysis.Node node, LoopAnalysis.Loop loop) {
        var bb=node.basicBlock;
        var preheader=loop.getPreHeader();
        var preheaderTerminator=preheader.getTerminator();
        if (!loop.contains(bb) || loopAnalysis.loopMap.get(bb)!=loop) {
            return;
        }
        for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
            if (inst instanceof BinaryOpInst) {
                var lhs=((BinaryOpInst) inst).getLhs();
                var rhs=((BinaryOpInst) inst).getRhs();
                InductionVariable oldIndVar = null;
                Instruction newStart = null;
                Value newStep = null;
                if (inst.getOpcode() == Instruction.Opcode.add) {
                    if (indVarMap.containsKey(lhs) && isInvariable(rhs,loop)) {
                        oldIndVar = indVarMap.get(lhs);
                        newStart = new BinaryOpInst("start", Instruction.Opcode.add, oldIndVar.start, rhs);
                    }else if(indVarMap.containsKey(rhs) && isInvariable(lhs,loop)){
                        oldIndVar=indVarMap.get(rhs);
                        newStart = new BinaryOpInst("start", Instruction.Opcode.add, oldIndVar.start, lhs);
                    }
                    if(oldIndVar!=null) {
                        preheader.addInstBefore(preheaderTerminator, newStart);
                        newStep=oldIndVar.step;
                        var newIndVar = new InductionVariable(newStart, newStep);
                        indVarMap.put(inst, newIndVar);
                    }
                } else if (inst.getOpcode() == Instruction.Opcode.sub) {
                    if (indVarMap.containsKey(lhs) && isInvariable(rhs, loop)) {
                        oldIndVar=indVarMap.get(lhs);
                        newStart=new BinaryOpInst("start", Instruction.Opcode.sub,oldIndVar.start,rhs);
                        preheader.addInstBefore(preheaderTerminator, newStart);
                        newStep=oldIndVar.step;
                        var newIndVar = new InductionVariable(newStart, newStep);
                        indVarMap.put(inst, newIndVar);
                    }
                } else if (inst.getOpcode() == Instruction.Opcode.mul) {
                    if (indVarMap.containsKey(lhs) && isInvariable(rhs, loop)) {
                        oldIndVar=indVarMap.get(lhs);
                        newStart=new BinaryOpInst("start", Instruction.Opcode.mul,oldIndVar.start,rhs);
                        newStep=new BinaryOpInst("step", Instruction.Opcode.mul,oldIndVar.step,rhs);
                    } else if (indVarMap.containsKey(rhs) && isInvariable(lhs, loop)) {
                        oldIndVar=indVarMap.get(rhs);
                        newStart=new BinaryOpInst("start", Instruction.Opcode.mul,oldIndVar.start,lhs);
                        newStep=new BinaryOpInst("step", Instruction.Opcode.mul,oldIndVar.step,lhs);
                    }
                    if (oldIndVar != null) {
                        preheader.addInstBefore(preheaderTerminator,newStart);
                        preheader.addInstBefore(preheaderTerminator, (Instruction) newStep);
                        var newIndVar = new InductionVariable(newStart, newStep);
                        indVarMap.put(inst, newIndVar);
                    }
                } else if (inst.getOpcode() == Instruction.Opcode.shl) {
                    if (indVarMap.containsKey(lhs) && rhs instanceof ConstantInt) {
                        oldIndVar=indVarMap.get(lhs);
                        var rhsVal=((ConstantInt) rhs).getVal();
                        newStart=new BinaryOpInst("start", Instruction.Opcode.mul,oldIndVar.start,new ConstantInt(1<<rhsVal));
                        newStep=new BinaryOpInst("step", Instruction.Opcode.mul,oldIndVar.step,new ConstantInt(1<<rhsVal));
                        preheader.addInstBefore(preheaderTerminator,newStart);
                        preheader.addInstBefore(preheaderTerminator, (Instruction) newStep);
                        var newIndVar = new InductionVariable(newStart, newStep);
                        indVarMap.put(inst, newIndVar);
                    }
                }
            }
        }
        for (var child : node.children) {
            findDerivedInductionVariable(child,loop);
        }
    }
    private void reduceStrength(Instruction original,InductionVariable indVar, LoopAnalysis.Loop loop){
        var header=loop.getHeader();
        var preheader=loop.getPreHeader();
        var backedge=loop.getBackEdge();
        if (indVar.phiNode == null && original.getOpcode()== Instruction.Opcode.mul) {
            changed=true;
            var newPhi=new PhiNode("indVar", Type.TheInt64);
            header.addInstToFirst(newPhi);
            newPhi.addIncoming(indVar.start, preheader);
            var stepInst=new BinaryOpInst("indvar_step", Instruction.Opcode.add,indVar.step,newPhi);
            header.addInstBefore(header.getTerminator(),stepInst);
            original.transferUses(newPhi);
            original.delete();
            newPhi.addIncoming(stepInst,backedge);
        }
    }
}
