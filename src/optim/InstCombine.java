package optim;

import IR.Module;
import IR.*;
import IR.instructions.*;
import Riscv.Call;

import java.util.ArrayList;
import java.util.LinkedList;

public class InstCombine extends FunctionPass implements IRVisitor {
    private LinkedList<Instruction> workList=new LinkedList<>();
    DominatorAnalysis dominatorAnalysis;
    public InstCombine(Function function,DominatorAnalysis dominatorAnalysis) {
        super(function);
        this.dominatorAnalysis=dominatorAnalysis;
    }

    @Override
    public boolean run() {
        initialize();
        boolean changed=false;

        while (!workList.isEmpty()) {
            var inst=workList.pollLast();
            if (visit(inst) != inst) {
                changed=true;
            }
        }
        return changed;
    }

    public void cleanInstInList(Instruction inst) {
        while(true) {
            var removed=workList.remove(inst);
            if (!removed) {
                break;
            }
        }
    }

    private void initialize(){
        workList.clear();
        for (var bb = function.getHead(); bb != null; bb = bb.getNext()) {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                workList.addLast(inst);
            }
        }
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
        //there shouldn't be any alloca
        return null;
    }
    //const<unary<binary
    private int getPriority(Value value){
        if (value.getValueType() == Value.ValueType.ConstantVal) {
            return 1;
        }
        if (value instanceof BinaryOperation) {
            if (((BinaryOperation) value).isNeg() || ((BinaryOperation) value).isNot()) {
                return 2;
            }
            return 3;
        }
        return 2;
    }
    private boolean hasOnlyOneUse(Value value){
        return value.getUse_head()==value.getUse_tail();
    }
    private void addInstBefore(Instruction prevInst,Instruction newInst){
        prevInst.getParent().addInstBefore(prevInst,newInst);
        workList.addLast(newInst);
    }
    private Value replace(Instruction prev,Value now){
        for (var use = prev.getUse_head(); use != null; use = use.getNext()) {
            workList.addLast((Instruction) use.getUser());
        }
        prev.transferUses(now);
        prev.delete();
        cleanInstInList(prev);
        return now;
    }
    private Value simplifyWithoutAddInst(Value lhs, Value rhs, Instruction.Opcode opcode){
        switch (opcode) {
            case add:
                if (rhs instanceof ConstantInt && ((ConstantInt) rhs).getVal() == 0) {
                    return lhs;
                }
                if (lhs instanceof ConstantInt && ((ConstantInt) lhs).getVal() == 0) {
                    return rhs;
                }
                break;
            case sub:
                if (lhs == rhs) {
                    return new ConstantInt(0) ;
                }
                //x-0=0
                if (rhs instanceof ConstantInt && ((ConstantInt) rhs).getVal() == 0) {
                    return  lhs;
                }
                break;
        }
        return null;
    }
    // put unary,const to right hand side
    private Instruction handleCommutativityAndAssociativity(BinaryOperation inst){
        Instruction replaceInst=null;
        var lhs= inst.getLhs();
        var rhs= inst.getRhs();
        if (getPriority(lhs) < getPriority(rhs)) {
            inst.swapLhsAndRhs();
        }
        if (lhs instanceof BinaryOperation) {
            var binaryLhs=(BinaryOperation)lhs;
            if (binaryLhs.getOpcode() == inst.getOpcode() && binaryLhs.getRhs().getValueType() == Value.ValueType.ConstantVal) {
                if (rhs.getValueType() == Value.ValueType.ConstantVal) {
                    var newRhs=ConstantFolding.constFoldBinaryOperation(inst.getOpcode(), binaryLhs.getRhs(),rhs);
                    replaceInst = new BinaryOpInst(inst.getName(),inst.getOpcode() , binaryLhs.getLhs(), newRhs);
                } else if (rhs instanceof BinaryOperation) {
                    var binaryRhs=(BinaryOperation)rhs;
                    if (binaryRhs.getOpcode() == inst.getOpcode() && binaryRhs.getRhs().getValueType() == Value.ValueType.ConstantVal && hasOnlyOneUse(lhs) && hasOnlyOneUse( rhs)) {
                        var newLhs=BinaryOperation.create("l1_add_l2", inst.getOpcode(), binaryLhs.getLhs(),binaryRhs.getLhs());
                        addInstBefore(inst,newLhs);
                        var newRhs=ConstantFolding.constFoldBinaryOperation(inst.getOpcode(), binaryLhs.getRhs(), binaryRhs.getRhs());
                        replaceInst= new BinaryOpInst(inst.getName(),inst.getOpcode(),newLhs,newRhs);
                    }
                }
            }
        }
        if (replaceInst != null) {
            addInstBefore(inst,replaceInst);
            return (Instruction) replace(inst,replaceInst);
        }
        return inst;
    }
    public Object visitAdd(BinaryOpInst inst){
        inst= (BinaryOpInst) handleCommutativityAndAssociativity(inst);
        //x+0=x
        Value lhs = inst.getLhs();
        Value rhs = inst.getRhs();
        if (rhs instanceof ConstantInt && ((ConstantInt) rhs).getVal() == 0) {
            return replace(inst, lhs);
        }
        //x+x=x<<1
        if (lhs == rhs) {
            var shiftInst=new BinaryOpInst(inst.getName(), Instruction.Opcode.shl, lhs,new ConstantInt(1));
            addInstBefore(inst,shiftInst);
            return replace(inst,shiftInst);
        }
        //-a+b=b-a
        if (isNeg(lhs)) {
            var a=((BinaryOperation) lhs).getRhs();
            var subInst=new BinaryOpInst(inst.getName(), Instruction.Opcode.sub, rhs,a);
            addInstBefore(inst,subInst);
            return replace(inst,subInst);
        }
        //a+(-b)=a-b
        if (isNeg(rhs)) {
            var b=((BinaryOperation) rhs).getRhs();
            var subInst=new BinaryOpInst(inst.getName(), Instruction.Opcode.sub, lhs,b);
            addInstBefore(inst,subInst);
            return replace(inst,subInst);
        }
        if (lhs instanceof BinaryOpInst ) {
            var binaryLhs=(BinaryOpInst)lhs;
            var a_add_c=simplifyWithoutAddInst(binaryLhs.getLhs(), rhs, Instruction.Opcode.add);
            var b_add_c=simplifyWithoutAddInst(binaryLhs.getRhs(),rhs, Instruction.Opcode.add);
            var c_sub_b=simplifyWithoutAddInst(rhs,binaryLhs.getRhs(), Instruction.Opcode.sub);
            if (binaryLhs.getOpcode() == Instruction.Opcode.add) {
                //(a+b)+c
                if (a_add_c != null) {
                    var newAddInst=new BinaryOpInst("add", Instruction.Opcode.add,a_add_c,binaryLhs.getRhs());
                    addInstBefore(inst,newAddInst);
                    return replace(inst,newAddInst);
                }
                if (b_add_c != null) {
                    var newAddInst= new BinaryOpInst("add", Instruction.Opcode.add,b_add_c,binaryLhs.getLhs());
                    addInstBefore(inst,newAddInst);
                    return replace(inst,newAddInst);
                }
            } else if (binaryLhs.getOpcode() == Instruction.Opcode.sub) {
                //(a-b)+c
                if (a_add_c != null) {
                    var newSubInst = new BinaryOpInst("sub", Instruction.Opcode.sub, a_add_c, binaryLhs.getRhs());
                    addInstBefore(inst, newSubInst);
                    return replace(inst, newSubInst);
                } else if(c_sub_b!=null) {
                    var newAddInst=new BinaryOpInst("add", Instruction.Opcode.add,binaryLhs.getLhs(),c_sub_b);
                    addInstBefore(inst,newAddInst);
                    return replace(inst,newAddInst);
                }
            }
        }
        if (rhs instanceof BinaryOpInst) {
            var binaryRhs=(BinaryOpInst) rhs;
            var a_add_b=simplifyWithoutAddInst(lhs,binaryRhs.getLhs(), Instruction.Opcode.add);
            var a_add_c=simplifyWithoutAddInst(lhs,binaryRhs.getRhs(), Instruction.Opcode.add);
            var a_sub_c=simplifyWithoutAddInst(lhs,binaryRhs.getRhs(), Instruction.Opcode.sub);
            if (binaryRhs.getOpcode() == Instruction.Opcode.add) {
                //a+(b+c)
                if (a_add_b != null) {
                    var newAddInst=new BinaryOpInst("add", Instruction.Opcode.add,a_add_b,binaryRhs.getRhs());
                    addInstBefore(inst,newAddInst);
                    return replace(inst,newAddInst);
                } else if (a_add_c != null) {
                    var newAddInst=new BinaryOpInst("add", Instruction.Opcode.add,a_add_c,binaryRhs.getLhs());
                    addInstBefore(inst,newAddInst);
                    return replace(inst,newAddInst);
                }
            } else if (binaryRhs.getOpcode() == Instruction.Opcode.sub) {
                //a+(b-c)
                if (a_add_b != null) {
                    var newSubInst=new BinaryOpInst("sub", Instruction.Opcode.sub,a_add_b,binaryRhs.getRhs());
                    addInstBefore(inst,newSubInst);
                    return replace(inst,newSubInst);
                } else if (a_sub_c != null) {
                    var newAddInst=new BinaryOpInst("add", Instruction.Opcode.add,a_sub_c,binaryRhs.getLhs());
                    addInstBefore(inst,newAddInst);
                    return replace(inst,newAddInst);
                }
            }
        }
        return inst;
    }

    private boolean isNeg(Value rhs) {
        return rhs instanceof BinaryOperation && ((BinaryOperation) rhs).isNeg();
    }

    public Object visitSub(BinaryOpInst inst){
        Value lhs = inst.getLhs();
        Value rhs = inst.getRhs();
        //x-x=0
        if (lhs == rhs) {
            return replace(inst,new ConstantInt(0) );
        }
        //x-0=0
        if (rhs instanceof ConstantInt && ((ConstantInt) rhs).getVal() == 0) {
            return replace(inst, lhs);
        }else if(rhs instanceof ConstantInt){
            //x-c=x+(-c)
            var addInst=new BinaryOpInst(inst.getName(), Instruction.Opcode.add,lhs,ConstantFolding.constFoldBinaryOpInst(Instruction.Opcode.sub,new ConstantInt(0),rhs));
            addInstBefore(inst,addInst);
            return replace(inst,addInst);
        }
        //x-(-a)=x+a
        if (isNeg(rhs)) {
            var a=((BinaryOperation)rhs).getRhs();
            var addInst=new BinaryOpInst(inst.getName(), Instruction.Opcode.add,lhs,a);
            addInstBefore(inst,addInst);
            return replace(inst,addInst);
        }
        //-1-x=~x
        if (lhs instanceof ConstantInt && ((ConstantInt) lhs).getVal() == -1 ) {
            var notInst=new BinaryOpInst(inst.getName(), Instruction.Opcode.xor,rhs,new ConstantInt(-1));
            addInstBefore(inst,notInst);
            return replace(inst,notInst);
        }
        if (lhs instanceof BinaryOpInst) {
            var binaryLhs=(BinaryOpInst)lhs;
            var a_sub_c=simplifyWithoutAddInst(binaryLhs.getLhs(),rhs, Instruction.Opcode.sub);
            var b_sub_c=simplifyWithoutAddInst(binaryLhs.getRhs(),rhs, Instruction.Opcode.sub);
            var b_add_c=simplifyWithoutAddInst(binaryLhs.getRhs(),rhs, Instruction.Opcode.add);
            if (binaryLhs.getOpcode() == Instruction.Opcode.add) {
                //(a+b)-c
                if (a_sub_c != null) {
                    var newAddInst=new BinaryOpInst("add", Instruction.Opcode.add,a_sub_c,binaryLhs.getRhs());
                    addInstBefore(inst,newAddInst);
                    return replace(inst,newAddInst);
                } else if (b_sub_c != null) {
                    var newAddInst=new BinaryOpInst("add", Instruction.Opcode.add,b_sub_c,binaryLhs.getLhs());
                    addInstBefore(inst,newAddInst);
                    return replace(inst,newAddInst);
                }
            } else if (binaryLhs.getOpcode() == Instruction.Opcode.sub) {
                //(a-b)-c
                if (a_sub_c != null) {
                    var newSubInst=new BinaryOpInst("sub", Instruction.Opcode.sub,a_sub_c,binaryLhs.getRhs());
                    addInstBefore(inst,newSubInst);
                    return replace(inst,newSubInst);
                } else if (b_add_c != null) {
                    var newSubInst=new BinaryOpInst("sub", Instruction.Opcode.sub,binaryLhs.getLhs(),b_add_c);
                    addInstBefore(inst,newSubInst);
                    return replace(inst,newSubInst);
                }
            }
        }
        if (rhs instanceof BinaryOpInst) {
            var binaryRhs=(BinaryOpInst)rhs;
            var a_sub_b=simplifyWithoutAddInst(lhs,binaryRhs.getLhs(), Instruction.Opcode.sub);
            var a_sub_c=simplifyWithoutAddInst(lhs,binaryRhs.getRhs(), Instruction.Opcode.sub);
            var a_add_c=simplifyWithoutAddInst(lhs,binaryRhs.getRhs(), Instruction.Opcode.add);
            if (binaryRhs.getOpcode() == Instruction.Opcode.add) {
                //a-(b+c)
                if (a_sub_b != null) {
                    var newSubInst=new BinaryOpInst("sub", Instruction.Opcode.sub,a_sub_b,binaryRhs.getRhs());
                    addInstBefore(inst,newSubInst);
                    return replace(inst,newSubInst);
                } else if (a_sub_c != null) {
                    var newSubInst=new BinaryOpInst("sub", Instruction.Opcode.sub,a_sub_c,binaryRhs.getLhs());
                }
            } else if (binaryRhs.getOpcode() == Instruction.Opcode.sub) {
                //a-(b-c)
                if (a_sub_b != null) {
                    var newAddInst=new BinaryOpInst("add", Instruction.Opcode.add,a_sub_b,binaryRhs.getRhs());
                    addInstBefore(inst,newAddInst);
                    return replace(inst,newAddInst);
                } else if (a_add_c != null) {
                    var newSubInst=new BinaryOpInst("sub", Instruction.Opcode.sub,a_add_c,binaryRhs.getLhs());
                    addInstBefore(inst,newSubInst);
                    return replace(inst,newSubInst);
                }
            }
        }
        return inst;
    }
    //return -1 means not power of 2;
    private static int isPowerOf2(int num){
        if (num < 0) {
            num=-num;
        }
        if (num == 0) {
            return -1;
        }
        int cnt=0;
        while (num > 1) {
            if ((num & 1) == 1) {
                return -1;
            }
            num>>=1;
            cnt++;
        }
        return cnt;
    }
    public Object visitMul(BinaryOpInst inst){

        inst= (BinaryOpInst) handleCommutativityAndAssociativity(inst);
        Value lhs = inst.getLhs();
        Value rhs = inst.getRhs();
        if (rhs instanceof ConstantInt) {
            var rhsVal=((ConstantInt) rhs).getVal();
            if (rhsVal == 0) {
                return replace(inst,new ConstantInt(0));
            } else if (rhsVal == 1) {
                return replace(inst,lhs);
            } else if (rhsVal == -1) {
                var subInst=new BinaryOpInst(inst.getName(), Instruction.Opcode.sub,new ConstantInt(0),lhs);
                addInstBefore(inst,subInst);
                return replace(inst,subInst);
            } else {
                var log2RhsVal=isPowerOf2(rhsVal);
                if (log2RhsVal != -1) {
                    var shlInst = new BinaryOpInst(inst.getName(), Instruction.Opcode.shl, lhs, new ConstantInt(log2RhsVal));
                    addInstBefore(inst, shlInst);
                    if (rhsVal > 0) {
                        return replace(inst, shlInst);
                    } else {
                        var negInst=new BinaryOpInst("neg", Instruction.Opcode.sub,new ConstantInt(0),shlInst);
                        addInstBefore(inst, negInst);
                        return replace(inst,negInst);
                    }
                }
            }
        }
        return inst;
    }
    public Object visitDiv(BinaryOpInst inst){
        Value lhs = inst.getLhs();
        Value rhs = inst.getRhs();
        if (lhs instanceof ConstantInt && ((ConstantInt) lhs).getVal() == 0) {
            return replace(inst,new ConstantInt(0));
        }
        if (rhs instanceof ConstantInt) {
            var rhsVal=((ConstantInt) rhs).getVal();
            if (rhsVal == 1) {
                return replace(inst,lhs);
            } else if (rhsVal == -1) {
                var subInst = new BinaryOpInst(inst.getName(), Instruction.Opcode.sub, new ConstantInt(0), lhs);
                addInstBefore(inst, subInst);
                return replace(inst, subInst);
            }
        }
        return inst;
    }
    public Object visitRem(BinaryOpInst inst){
        Value lhs = inst.getLhs();
        Value rhs = inst.getRhs();
        if (lhs instanceof ConstantInt && ((ConstantInt) lhs).getVal() == 0) {
            return replace(inst,new ConstantInt(0));
        }
        if (rhs instanceof ConstantInt) {
            var rhsVal=((ConstantInt) rhs).getVal();
            if (rhsVal == 1 || rhsVal== -1) {
                return replace(inst,new ConstantInt(0));
            }
        }
        return inst;
    }
    public Object visitAnd(BinaryOpInst inst){
        inst= (BinaryOpInst) handleCommutativityAndAssociativity(inst);
        Value lhs = inst.getLhs();
        Value rhs = inst.getRhs();
        if (lhs == rhs) {
            return replace(inst,lhs);
        }
        if (rhs instanceof ConstantInt ) {
            if(((ConstantInt) rhs).getVal()==0) {
                return replace(inst, new ConstantInt(0));
            } else if (((ConstantInt) rhs).getVal() == -1) {
                return replace(inst,lhs);
            }
        } else if (rhs instanceof ConstantBool) {
            if (((ConstantBool) rhs).isTrue()) {
                return replace(inst, lhs);
            } else {
                return replace(inst,new ConstantBool(false));
            }
        }
        return inst;
    }
    public Object visitOr(BinaryOpInst inst){
        inst= (BinaryOpInst) handleCommutativityAndAssociativity(inst);
        Value lhs = inst.getLhs();
        Value rhs = inst.getRhs();
        if (lhs == rhs) {
            return replace(inst,lhs);
        }
        if (rhs instanceof ConstantInt ) {
            if(((ConstantInt) rhs).getVal()==0) {
                return replace(inst, lhs);
            } else if (((ConstantInt) rhs).getVal() == -1) {
                return replace(inst,new ConstantInt(-1));
            }
        }else if (rhs instanceof ConstantBool) {
            if (((ConstantBool) rhs).isTrue()) {
                return replace(inst, new ConstantBool(true));
            } else {
                return replace(inst,lhs);
            }
        }
        return inst;
    }
    public Object visitXor(BinaryOpInst inst){
        inst= (BinaryOpInst) handleCommutativityAndAssociativity(inst);
        Value lhs = inst.getLhs();
        Value rhs = inst.getRhs();
        if (lhs == rhs) {
            return replace(inst,new ConstantInt(0));
        }
        if (rhs instanceof ConstantInt ) {
            if(((ConstantInt) rhs).getVal()==0) {
                return replace(inst, lhs);
            }
        }else if (rhs instanceof ConstantBool) {
            if (!((ConstantBool) rhs).isTrue()) {
                return replace(inst, lhs);
            }
        }
        return inst;
    }
    public Object visitShl(BinaryOpInst inst){
        Value lhs = inst.getLhs();
        Value rhs = inst.getRhs();
        if (lhs instanceof ConstantInt && ((ConstantInt) lhs).getVal() == 0) {
            return replace(inst,new ConstantInt(0));
        }
        if (rhs instanceof ConstantInt) {
            var rhsVal=((ConstantInt) rhs).getVal();
            if(rhsVal==0) {
                return replace(inst, lhs);
            }
            if (lhs instanceof BinaryOpInst) {
                var lhs_lhs=((BinaryOpInst) lhs).getLhs();
                var lhs_rhs=((BinaryOpInst) lhs).getRhs();
                if( ((BinaryOpInst) lhs).getOpcode() == Instruction.Opcode.mul) {
                    if (lhs_rhs instanceof ConstantInt) {
                        var newInst = new BinaryOpInst(inst.getName(), Instruction.Opcode.mul, lhs_lhs, ConstantFolding.constFoldBinaryOpInst(Instruction.Opcode.shl, lhs_rhs, rhs));
                        addInstBefore(inst, newInst);
                        return replace(inst, newInst);
                    }
                } else if (((BinaryOpInst) lhs).getOpcode() == Instruction.Opcode.shl) {
                    if (lhs_rhs instanceof ConstantInt) {
                        var newInst=new BinaryOpInst(inst.getName(), Instruction.Opcode.shl,lhs_lhs,ConstantFolding.constFoldBinaryOpInst(Instruction.Opcode.add,lhs_rhs,rhs));
                        addInstBefore(inst, newInst);
                        return replace(inst, newInst);
                    }
                }
            }
        }
        return inst;
    }
    public Object visitShr(BinaryOpInst inst){
        Value lhs = inst.getLhs();
        Value rhs = inst.getRhs();
        if (lhs instanceof ConstantInt && ((ConstantInt) lhs).getVal() == 0) {
            return replace(inst,new ConstantInt(0));
        }
        if (rhs instanceof ConstantInt) {
            var rhsVal=((ConstantInt) rhs).getVal();
            if(rhsVal==0) {
                return replace(inst, lhs);
            }
            if (lhs instanceof BinaryOpInst) {
                var lhs_lhs=((BinaryOpInst) lhs).getLhs();
                var lhs_rhs=((BinaryOpInst) lhs).getRhs();
                if (((BinaryOpInst) lhs).getOpcode() == Instruction.Opcode.shr) {
                    if (lhs_rhs instanceof ConstantInt) {
                        var newInst=new BinaryOpInst(inst.getName(), Instruction.Opcode.shr,lhs_lhs,ConstantFolding.constFoldBinaryOpInst(Instruction.Opcode.add,lhs_rhs,rhs));
                        addInstBefore(inst, newInst);
                        return replace(inst, newInst);
                    }
                }
            }
        }
        return inst;
    }

    @Override
    public Object visitBinaryOpInst(BinaryOpInst binaryOpInst) {
        switch (binaryOpInst.getOpcode()) {
            case add:return visitAdd(binaryOpInst);
            case sub:return visitSub(binaryOpInst);
            case mul:return visitMul(binaryOpInst);
            case div:return visitDiv(binaryOpInst);
            case rem:return visitRem(binaryOpInst);
            case and:return visitAnd(binaryOpInst);
            case or:return visitOr(binaryOpInst);
            case xor:return visitXor(binaryOpInst);
            case shl:return visitShl(binaryOpInst);
            case shr:return visitShr(binaryOpInst);
        }
        return null;
    }

    @Override
    public Object visitBranchInst(BranchInst inst) {
        if(inst.isConditional()){
            var cond=inst.getCondition();
            if (cond instanceof BinaryOpInst && ((BinaryOpInst) cond).isNot()) {
                inst.setCond(((BinaryOpInst) cond).getNotSrc());
                inst.swapThenElse();
            }
        }
        return inst;
    }

    @Override
    public Object visitCallInst(CallInst callInst) {
        var funcName=callInst.getCallee().getName();
        if (funcName.equals("println") || funcName.equals("print")) {
            var param=callInst.getParams().get(0);
            if (callInst.getParams().size() == 1 && param instanceof CallInst && ((CallInst) param).getCallee().getName().equals("toString")) {
                var newFuncName=funcName+"Int";
                var newFunc=function.getParent().getSymbolTable().get(newFuncName);
                var newParam=new ArrayList<Value>();
                newParam.add(((CallInst) param).getParams().get(0));
                var replaceCall=new CallInst(callInst.getName(), (Function) newFunc,newParam);
                addInstBefore(callInst,replaceCall);
                return replace(callInst,replaceCall);
            }

        }
        return callInst;
    }

    @Override
    public Object visitCastInst(CastInst castInst) {
        if (castInst.getType() .equals(castInst.getSource().getType())) {
            return replace(castInst,castInst.getSource());
        }
        return castInst;
    }

    @Override
    public Object visitGEPInst(GetElementPtrInst GEPInst) {
        boolean isAllZero=true;
        var ptr=GEPInst.getOperands().get(0).getVal();
        for (int i = 1; i < GEPInst.getOperands().size();i++) {
            var idx=GEPInst.getOperands().get(i).getVal();
            if (!(idx instanceof ConstantInt) || ((ConstantInt) idx).getVal() != 0) {
                isAllZero=false;
                break;
            }
        }
        if (isAllZero) {
            if (GEPInst.getOperands().size() == 2) {
                return replace(GEPInst, ptr);
            } else {
                var castInst=new CastInst("cast", GEPInst.getType(),ptr );
                addInstBefore(GEPInst, castInst);
                return replace(GEPInst,castInst);
            }
        }
        ArrayList<Value> offsetIdx=new ArrayList<>();
        ArrayList<Value> referenceIdx=new ArrayList<>();
        for (int i = 2; i < GEPInst.getOperands().size(); i++) {
            referenceIdx.add(GEPInst.getOperands().get(i).getVal());
        }
        var ptrOffset=GEPInst.getOperands().get(1).getVal();
        if (ptrOffset instanceof BinaryOpInst && hasOnlyOneUse(ptrOffset) && dominatorAnalysis.dominate(ptr,ptrOffset)) {
            var lhs=((BinaryOpInst) ptrOffset).getLhs();
            var rhs=((BinaryOpInst) ptrOffset).getRhs();
            if (((BinaryOpInst) ptrOffset).getOpcode() == Instruction.Opcode.add) {
                offsetIdx.add(lhs);
                referenceIdx.add(0,rhs);
                var offsetInst=new GetElementPtrInst("scevgep", ptr,offsetIdx);
                addInstBefore((Instruction) ptrOffset, offsetInst);
                ((BinaryOpInst) ptrOffset).delete();
                var referenceInst=new GetElementPtrInst(GEPInst.getName(),offsetInst,referenceIdx);
                addInstBefore(GEPInst,referenceInst);
                return replace(GEPInst,referenceInst);
            }
        }
        return GEPInst;
    }

    @Override
    public Object visitIcmpInst(IcmpInst icmpInst) {
        Value lhs = icmpInst.getLhs();
        Value rhs = icmpInst.getRhs();
        if(lhs == rhs ){
            switch (icmpInst.getOpcode()){
                case EQ:case LE:case GE:return replace(icmpInst,new ConstantBool(true));
                case NE:case GT:case LT:return replace(icmpInst,new ConstantBool(false));
            }
        }
        //comparisons between reg and imm only have operator < , == and !=
        var opcode=icmpInst.getOpcode();
        if (lhs instanceof ConstantInt) {
            int lhs_val=((ConstantInt) lhs).getVal();
            Instruction newCmp=null;
            if (opcode == Instruction.Opcode.LE) {
                newCmp=new IcmpInst("cmp_codegen", Instruction.Opcode.LT,new ConstantInt(lhs_val-1), rhs);
            } else if (opcode == Instruction.Opcode.GT) {
                newCmp=new IcmpInst("cmp_codegen", Instruction.Opcode.LT,rhs,lhs);
            } else if (opcode == Instruction.Opcode.GE) {
                newCmp=new IcmpInst("cmp_codegen", Instruction.Opcode.LT,rhs,new ConstantInt(lhs_val+1));
            }
            if (newCmp != null) {
                addInstBefore(icmpInst,newCmp);
                return replace(icmpInst,newCmp);
            }
        } else if (rhs instanceof ConstantInt) {
            int rhs_val=((ConstantInt) rhs).getVal();
            Instruction newCmp=null;
            if (opcode == Instruction.Opcode.LE) {
                newCmp=new IcmpInst("cmp_codegen", Instruction.Opcode.LT,lhs,new ConstantInt(rhs_val+1));
            } else if (opcode == Instruction.Opcode.GE) {
                newCmp=new IcmpInst("cmp_codegen", Instruction.Opcode.LT,new ConstantInt(rhs_val-1),lhs);
            } else if (opcode == Instruction.Opcode.GT) {
                newCmp=new IcmpInst("cmp_codegen", Instruction.Opcode.LT,rhs,lhs);
            }
            if (newCmp != null) {
                addInstBefore(icmpInst,newCmp);
                return replace(icmpInst,newCmp);
            }
        }
        return icmpInst;
    }

    @Override
    public Object visitLoadInst(LoadInst loadInst) {
        return loadInst;
    }

    @Override
    public Object visitPhiNode(PhiNode phiNode) {
        if (phiNode.getOperands().size() == 2) {
            return replace(phiNode,phiNode.getValue(0));
        }
        return phiNode;
    }

    @Override
    public Object visitReturnInst(ReturnInst returnInst) {
        return returnInst;
    }

    @Override
    public Object visitStoreInst(StoreInst storeInst) {
        return storeInst;
    }

    @Override
    public Object visitMovInst(MovInst movInst) {
        return null;
    }

    @Override
    public Object visit(Value value) {
        return value.accept(this);
    }
}
