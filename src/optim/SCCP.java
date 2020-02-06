package optim;

import IR.Module;
import IR.*;
import IR.instructions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class SCCP extends FunctionPass implements IRVisitor {
    enum ValState{undefined,constant,multidefined}
    private static class State {
        ValState valState;
        Value constant;

        public ValState getValState() {
            return valState;
        }

        public State(ValState valState, Value constant) {
            this.valState = valState;
            this.constant = constant;
        }

        public void setValState(ValState valState) {
            this.valState = valState;
        }

        public Value getConstant() {
            return constant;
        }

        public void setConstant(Value constant) {
            this.constant = constant;
        }

        public State(ValState valState) {
            this.valState = valState;
            this.constant=null;
        }
    }
    private HashMap<Value, State> valueState=new HashMap<>();
    private HashSet<BasicBlock> bbState=new HashSet<>();
    private LinkedList<Instruction> instWorkList=new LinkedList<>();
    private LinkedList<BasicBlock> bbWorkList=new LinkedList<>();
    private boolean isExecutable(BasicBlock basicBlock){
        return bbState.contains(basicBlock);
    }
    private void makeMultiDefine(Instruction instruction) {
        var prevState=getState(instruction);
        if (prevState.valState != ValState.multidefined) {
            valueState.get(instruction).setValState(ValState.multidefined);
            instWorkList.addLast(instruction);
        }
    }
    private void makeConstant(Instruction instruction,Value constant){
        var prevState=getState(instruction);
        assert prevState.valState!=ValState.multidefined;
        if(prevState.valState==ValState.constant) {
            assert prevState.constant.equals(constant);
        }else {
            valueState.get(instruction).setValState(ValState.constant);
            valueState.get(instruction).setConstant(constant);
            instWorkList.addLast(instruction);
        }
    }
    private void makebbExecutable(BasicBlock basicBlock){
        if (bbState.contains(basicBlock)) {
            for (var phi = basicBlock.getHead(); phi instanceof PhiNode; phi = phi.getNext()) {
                visit(phi);
            }
        } else {
            bbState.add(basicBlock);
            bbWorkList.addLast(basicBlock);
        }
    }
    private State getState(Value value){
        if (valueState.containsKey(value)) {
            return valueState.get(value);
        }
        if (value.getValueType() == Value.ValueType.ConstantVal) {
            return new State(ValState.constant,value);
        } else if (value instanceof Argument || value instanceof GlobalVariable) {
            return new State(ValState.multidefined);
        } else {
            valueState.put(value,new State(ValState.undefined));
            return valueState.get(value);
        }

    }
    public SCCP(Function function) {
        super(function);
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
        for (var inst = basicBlock.getHead(); inst != null; inst = inst.getNext()) {
            visit(inst);
        }
        return null;
    }

    @Override
    public Object visitGlobalVariable(GlobalVariable globalVariable) {
        return null;
    }

    @Override
    public Object visitAllocaInst(AllocaInst allocaInst) {
        makeMultiDefine(allocaInst);
        return null;
    }

    @Override
    public Object visitBinaryOpInst(BinaryOpInst binaryOpInst) {
        var op1State=getState(binaryOpInst.getLhs());
        var op2State=getState(binaryOpInst.getRhs());
        if (op1State.valState == ValState.constant && op2State.valState == ValState.constant) {
            var constFold=ConstantFolding.constFoldBinaryInst(binaryOpInst.getOpcode(), null,op1State.getConstant(), op2State.getConstant());
            makeConstant(binaryOpInst,constFold);
        } else if (op1State.valState == ValState.multidefined || op2State.valState == ValState.multidefined) {
            makeMultiDefine(binaryOpInst);
        }
        return null;
    }

    @Override
    public Object visitBranchInst(BranchInst branchInst) {
        if (!branchInst.isConditional()) {
            makebbExecutable(branchInst.getDstThen());
        } else {
            var condState=getState(branchInst.getCondition());
            if (condState.valState == ValState.multidefined) {
                makebbExecutable(branchInst.getDstThen());
                makebbExecutable(branchInst.getDstElse());
            } else if (condState.valState == ValState.constant) {
                if (((ConstantBool) condState.constant).isTrue()) {
                    makebbExecutable(branchInst.getDstThen());
                } else {
                    makebbExecutable(branchInst.getDstElse());
                }
            }
        }
        return null;
    }

    @Override
    public Object visitCallInst(CallInst callInst) {
        makeMultiDefine(callInst);
        return  null;
    }

    @Override
    public Object visitCastInst(CastInst castInst) {
        var srcState=getState(castInst.getSource());
        if (srcState.valState == ValState.constant) {
            var newconst =ConstantFolding.constFoldCastInst(castInst.getType(), null,srcState.constant);
            makeConstant(castInst,newconst);
        } else if (srcState.valState == ValState.multidefined) {
            makeMultiDefine(castInst);
        }
        return null;
    }

    @Override
    public Object visitGEPInst(GetElementPtrInst GEPInst) {
        makeMultiDefine(GEPInst);
        return null;
    }

    @Override
    public Object visitIcmpInst(IcmpInst icmpInst) {
        var op1State=getState(icmpInst.getLhs());
        var op2State=getState(icmpInst.getRhs());
        if (op1State.valState == ValState.constant && op2State.valState == ValState.constant) {
            var constFold=ConstantFolding.constFoldIcmpInst(icmpInst.getOpcode(), null,op1State.getConstant(), op2State.getConstant());
            makeConstant(icmpInst,constFold);
        } else if (op1State.valState == ValState.multidefined || op2State.valState == ValState.multidefined) {
            makeMultiDefine(icmpInst);
        }
        return null;
    }

    @Override
    public Object visitLoadInst(LoadInst loadInst) {
        makeMultiDefine(loadInst);
        return null;
    }

    @Override
    public Object visitPhiNode(PhiNode phiNode) {
        Value theOnlyConstant=null;
        int cnt=0;
        for (int i = 0; i < phiNode.getOperands().size() / 2; i++) {
            var bb=phiNode.getBB(i);
            var value=phiNode.getValue(i);
            if (isExecutable(bb)) {
                var state=getState(value);
                if (state.valState == ValState.multidefined) {
                    makeMultiDefine(phiNode);
                    return null;
                } else if (state.valState == ValState.constant) {
                    cnt++;
                    if (theOnlyConstant == null) {
                        theOnlyConstant = state.constant;
                    } else {
                        if(!theOnlyConstant.equals(state.constant)){
                            makeMultiDefine(phiNode);
                        }
                    }
                }
            }
        }
        if(cnt==1) {
            makeConstant(phiNode, theOnlyConstant);
        }
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

    @Override
    public boolean run() {
        bbWorkList.clear();
        instWorkList.clear();
        valueState.clear();
        bbState.clear();
        boolean changed=false;
        makebbExecutable(function.getEntryBB());
//        bbWorkList.addLast(function.getEntryBB());
        while (!bbWorkList.isEmpty() || !instWorkList.isEmpty()) {
            while (!instWorkList.isEmpty()) {
                var lastinst=instWorkList.pollLast();
                for (var use = lastinst.getUse_head(); use != null; use = use.getNext()) {
                    visit(use.getUser());
                }
            }
            while (!bbWorkList.isEmpty()) {
                var bb=bbWorkList.pollLast();
                visit(bb);
            }
        }
        for (var bb = function.getHead(); bb != null; bb = bb.getNext()) {
            for (var inst = bb.getHead(); inst != null; ) {
                var tmp=inst.getNext();
                if (getState(inst).valState == ValState.constant) {
                    inst.transferUses(valueState.get(inst).constant);
                    inst.delete();
                    changed=true;
                }
                inst=tmp;
            }
        }
        return changed;
    }
}
