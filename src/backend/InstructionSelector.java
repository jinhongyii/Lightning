package backend;

import IR.*;
import IR.Module;

import IR.instructions.*;
import Riscv.*;

import java.util.HashMap;

public class InstructionSelector implements IRVisitor {
    HashMap<Instruction, VirtualRegister> convertMap=new HashMap<>();
    public MachineModule machineModule;
    private MachineBasicBlock curBB;
    private MachineFunction curFunc;
    private HashMap<Instruction,VirtualRegister> vRegMap=new HashMap<>();
    private HashMap<BasicBlock,MachineBasicBlock> bbMap=new HashMap<>();
    private HashMap<Function,MachineFunction> funcMap=new HashMap<>();
    private HashMap<Argument,VirtualRegister> argMap=new HashMap<>();
    private HashMap<GlobalVariable,GlobalVar> globalVarMap=new HashMap<>();
    private HashMap<VirtualRegister,VirtualRegister> calleeMap;
    private VirtualRegister ra;
    private final int maxImm=(1<<12)-1;
    public InstructionSelector(MachineModule module,Module irModule){
        this.machineModule=module;
        visitModule(irModule);
    }
    @Override
    public Object visitModule(Module module) {
        for (var global_var : module.getGlobalList()) {
            visit(global_var);
        }
        for (var func : module.getFunctionList()) {
            var machineFunc=new MachineFunction(func.getName(),func.isExternalLinkage(), func.getArguments().size(),func);
            funcMap.put(func,machineFunc);
            machineModule.addFunc(machineFunc);
        }
        for (var func : module.getFunctionList()) {
            if(!func.isExternalLinkage()) {
                visit(func);
            }
        }

        return null;
    }

    @Override
    public Object visitFunction(Function function) {
        curFunc=funcMap.get(function);
        calleeMap=new HashMap<>();
        curBB=getMachineBB(function.getHead());
        //save callee-save regs into a local vreg
        for (var name : TargetInfo.calleeSavedRegister) {
            var localReg=new VirtualRegister("backup."+name);
            calleeMap.put(getVPhyReg(name),localReg);
            curBB.addInst(new Move(getVPhyReg(name),localReg));
        }
        ra=new VirtualRegister("backup.ra");
        curBB.addInst(new Move(getVPhyReg("ra"),ra));
        //create vreg for every argument
        int paramNum=function.getArguments().size();
        for (var arg : function.getArguments()) {
            argMap.put(arg,new VirtualRegister(arg.getName()));
        }
        //mov arguments to its vreg
        for (int i = 8; i < paramNum; i++) {
            var argVReg= getOperand(function.getArguments().get(i), false);
            curBB.addInst(new Load(new StackLocation(i-8), (Register) argVReg));
        }
        for (int i = 0; i < Integer.min(paramNum, 8);i++) {
            var argVReg= getOperand(function.getArguments().get(i), false);
            curBB.addInst(new Move(getVPhyReg("a"+i), (Register) argVReg));
        }

        for (var bb = function.getHead(); bb != null; bb = bb.getNext()) {
            visit(bb);
        }
        return null;
    }

    @Override
    public Object visitBasicBlock(BasicBlock basicBlock) {
        curBB=getMachineBB(basicBlock);
        curFunc.addBB(curBB);
        for (var inst = basicBlock.getHead(); inst != null; inst = inst.getNext()) {
            visit(inst);
        }
        return null;
    }

    @Override
    public Object visitGlobalVariable(GlobalVariable globalVariable) {
        globalVarMap.put(globalVariable,machineModule.addGlobal(globalVariable));
        return null;
    }

    @Override
    public Object visitAllocaInst(AllocaInst allocaInst) {
        assert false;
        return null;
    }
    private VirtualRegister getVPhyReg(String name){
        return TargetInfo.vPhysicalReg.get(name);
    }
    private MachineBasicBlock getMachineBB(BasicBlock bb){
        if (bbMap.containsKey(bb)) {
            return bbMap.get(bb);
        } else {
            var newBB=new MachineBasicBlock(bb);
            bbMap.put(bb,newBB);
            return newBB;
        }
    }
    private MachineOperand getOperand(Value value, boolean enableImm){
        if (value instanceof ConstantInt) {
            int val = ((ConstantInt) value).getVal();
            if (val > maxImm || !enableImm) {
                var tmpVReg = new VirtualRegister("tmp");
                curBB.addInst(new LI(tmpVReg,new Imm(val)));
                return tmpVReg;
            } else {
                return new Imm(val);
            }
        } else if(value instanceof ConstantBool) {
            int val=((ConstantBool) value).isTrue()?1:0;
            if (enableImm) {
                return new Imm(val);
            } else {
                var tmpVReg = new VirtualRegister("tmp");
                curBB.addInst(new I_Type(I_Type.Opcode.addi,getVPhyReg("zero"),tmpVReg,new Imm(val)));
                return tmpVReg;
            }
        } else if (value instanceof ConstantNull) {
            if (enableImm) {
                return new Imm(0);
            } else {
                var tmpVReg = new VirtualRegister("tmp");
                curBB.addInst(new I_Type(I_Type.Opcode.addi,getVPhyReg("zero"),tmpVReg,new Imm(0)));
                return tmpVReg;
            }
        } else if (value instanceof Instruction) {
            if (vRegMap.containsKey(value)) {
                return vRegMap.get(value);
            } else {
                var newReg=new VirtualRegister(value.getName());
                vRegMap.put((Instruction)value,newReg);
                return newReg;
            }
        } else if (value instanceof GlobalVariable) {
            return globalVarMap.get(value);
        } else if (value instanceof Argument) {
            return argMap.get(value);
        }
        return null;
    }
    private I_Type.Opcode translateI_opcode(Instruction.Opcode opcode){
        switch (opcode) {
            case add:
                return I_Type.Opcode.addi;
            case shr:
                return I_Type.Opcode.srai;
            case shl:
                return I_Type.Opcode.slli;
            case LT:
                return I_Type.Opcode.slti;
            case and:
                return I_Type.Opcode.andi;
            case xor:
                return I_Type.Opcode.xori;
            case or:
                return I_Type.Opcode.ori;
            default:throw new Error("wrong operator");
        }
    }
    private R_Type.Opcode translateR_opcode(Instruction.Opcode opcode){
        switch (opcode) {
            case add:
                return R_Type.Opcode.add;
            case sub:
                return R_Type.Opcode.sub;
            case mul:
                return R_Type.Opcode.mul;
            case div:
                return R_Type.Opcode.div;
            case rem:
                return R_Type.Opcode.rem;
            case xor:
                return R_Type.Opcode.xor;
            case or:
                return R_Type.Opcode.or;
            case and:
                return R_Type.Opcode.and;
            case LT:
                return R_Type.Opcode.slt;
            case shr:
                return R_Type.Opcode.sra;
            case shl:
                return R_Type.Opcode.sll;

            default:
                throw new Error("invalid  operator");
        }
    }
    MachineInstruction getTranslatedInst(Instruction.Opcode opcode,MachineOperand src1,MachineOperand src2, Register rd ){
        MachineInstruction inst;
        if (src1 instanceof Imm) {
            inst=new I_Type(translateI_opcode(opcode), (VirtualRegister) src2, rd, (Imm) src1);
        } else if (src2 instanceof Imm) {
            inst=new I_Type(translateI_opcode(opcode), (VirtualRegister) src1, rd, (Imm) src2);
        } else {
            inst=new R_Type(translateR_opcode(opcode), (VirtualRegister)src1, (VirtualRegister)src2, rd);
        }
        return inst;
    }
    @Override
    public Object visitBinaryOpInst(BinaryOpInst binaryOpInst) {

        var lhs=binaryOpInst.getLhs();
        var rhs=binaryOpInst.getRhs();
        var opcode=binaryOpInst.getOpcode();
        var enableImm_L=opcode!= Instruction.Opcode.div && opcode!= Instruction.Opcode.rem && opcode!= Instruction.Opcode.sub && opcode!= Instruction.Opcode.shl && opcode!= Instruction.Opcode.shr;
        var enableImm_R= opcode!= Instruction.Opcode.div && opcode!= Instruction.Opcode.rem ;
        var lReg= getOperand(lhs,enableImm_L);
        var rReg= getOperand(rhs,enableImm_R);
        var rd= getOperand(binaryOpInst,false);
        curBB.addInst(getTranslatedInst(opcode,lReg,rReg, (VirtualRegister) rd));
        return null;
    }

    @Override
    public Object visitBranchInst(BranchInst branchInst) {

        //todo: can merge cmp into the branch instruction
        if (branchInst.isConditional()) {
            var thenBB = getMachineBB(branchInst.getDstThen());
            var elseBB = getMachineBB(branchInst.getDstElse());
            var cmp = getOperand(branchInst.getCondition(), false);
            curBB.addInst(new Branch((Register) cmp, getVPhyReg("zero"), Branch.Opcode.bne, thenBB));
            curBB.addInst(new Jump(elseBB));
        } else {
            var nextBB=getMachineBB(branchInst.getDstThen());
            curBB.addInst(new Jump(nextBB));
        }
        return null;

    }

    @Override
    public Object visitCallInst(CallInst callInst) {
        int paramNum=callInst.getParams().size();
        var callee=funcMap.get(callInst.getCallee());
        for (int i = 8; i < paramNum; i++) {
            var vreg= getOperand(callInst.getParams().get(i), false);
            curBB.addInst(new Store(new StackLocation(callee), (Register) vreg));
        }
        for (int i = 0; i < Integer.min(paramNum, 8); i++) {
            var vreg= getOperand(callInst.getParams().get(i), false);
            curBB.addInst(new Move((Register) vreg,getVPhyReg("a"+i)));
        }
        curBB.addInst(new Call(callee));
        if (!callInst.getType().equals(Type.theVoidType)) {
            var rd= getOperand(callInst,false);
            curBB.addInst(new Move(getVPhyReg("a0"), (Register) rd));
        }
        return null;
    }

    @Override
    public Object visitCastInst(CastInst castInst) {
        var from= getOperand(castInst.getSource(), false);
        var to = getOperand(castInst, false);
        if (castInst.getSource() instanceof GlobalVariable && ((GlobalVariable) castInst.getSource()).getInitializer() instanceof ConstantString) {
            curBB.addInst(new LA((Register) to,(GlobalVar)from));
        } else {
            curBB.addInst(new Move((VirtualRegister) from, (VirtualRegister) to));
        }
        return null;
    }

    @Override
    public Object visitGEPInst(GetElementPtrInst GEPInst) {
        //todo: can be merged into the load/store
        var rd= getOperand(GEPInst,false);
        int offset=GEPInst.getOffset();
        var base= getOperand(GEPInst.getOperands().get(0).getVal(), false);
        if (offset == -1) {
            var idx = getOperand(GEPInst.getOperands().get(1).getVal(), false);
            curBB.addInst(getTranslatedInst(Instruction.Opcode.add, idx, base, (Register) rd));
        } else {
            curBB.addInst(getTranslatedInst(Instruction.Opcode.add,new Imm(offset),base, (Register) rd));
        }
        return null;
    }

    @Override
    public Object visitIcmpInst(IcmpInst icmpInst) {
        var opcode=icmpInst.getOpcode();
        var lhs=icmpInst.getLhs();
        var rhs=icmpInst.getRhs();
        var lReg= getOperand(lhs,true);
        var rReg= getOperand(rhs,true);
        var rd= getOperand(icmpInst,false);
        if (opcode == Instruction.Opcode.EQ) {
            var tmp = new VirtualRegister("tmp");
            curBB.addInst(getTranslatedInst(Instruction.Opcode.xor, lReg, rReg, tmp));
            curBB.addInst(new I_Type(I_Type.Opcode.sltiu, tmp, (Register) rd, new Imm(1)));
        } else if (opcode == Instruction.Opcode.NE) {
            var tmp = new VirtualRegister("tmp");
            curBB.addInst(getTranslatedInst(Instruction.Opcode.xor, lReg, rReg, tmp));
            curBB.addInst(new R_Type(R_Type.Opcode.sltu, getVPhyReg("zero"), tmp, (Register) rd));
        } else {
            if (lReg instanceof Imm) {
                assert opcode== Instruction.Opcode.LT;
                var loadImmTmp=new VirtualRegister("imm_tmp");
                curBB.addInst(new LI(loadImmTmp, (Imm) lReg));
                curBB.addInst(getTranslatedInst(Instruction.Opcode.LT,loadImmTmp,rReg, (Register) rd));
            } else if (rReg instanceof Imm) {
                assert opcode == Instruction.Opcode.LT;
                curBB.addInst(getTranslatedInst(Instruction.Opcode.LT, lReg, rReg, (Register) rd));
            } else {
                if (opcode == Instruction.Opcode.LT) {
                    curBB.addInst(getTranslatedInst(Instruction.Opcode.LT,lReg,rReg, (Register) rd));
                } else if (opcode == Instruction.Opcode.LE) {
                    curBB.addInst(getTranslatedInst(Instruction.Opcode.LT,rReg,lReg, (Register) rd));
                    curBB.addInst(getTranslatedInst(Instruction.Opcode.xor,rd,new Imm(1), (Register) rd));
                } else if (opcode == Instruction.Opcode.GT) {
                    curBB.addInst(getTranslatedInst(Instruction.Opcode.GT,rReg,lReg, (Register) rd));
                } else if (opcode == Instruction.Opcode.GE) {
                    curBB.addInst(getTranslatedInst(Instruction.Opcode.LT, lReg, rReg, (Register) rd));
                    curBB.addInst(getTranslatedInst(Instruction.Opcode.xor, rd, new Imm(1), (Register) rd));
                } else {
                    assert false;
                }
            }
        }
        return null;
    }

    @Override
    public Object visitLoadInst(LoadInst loadInst) {
        var rd= getOperand(loadInst, false);
        var src= getOperand(loadInst.getLoadTarget(),false);
        assert src != null;
        if(loadInst.getType().equals(Type.TheInt1)){
            curBB.addInst(new Load(src, (Register) rd,1));
        }else {
            curBB.addInst(new Load(src, (Register) rd,4));
        }
        return null;
    }

    @Override
    public Object visitPhiNode(PhiNode phiNode) {
        return null;
    }

    @Override
    public Object visitReturnInst(ReturnInst returnInst) {

        if (returnInst.hasRetValue()) {
            curBB.addInst(new Move((Register) getOperand(returnInst.getRetValue(),false),getVPhyReg("a0")));
        }
        //recover callee-saved regs
        for (var entry : calleeMap.entrySet()) {
            curBB.addInst(new Move(entry.getValue(),entry.getKey()));
        }
        curBB.addInst(new Move(ra, getVPhyReg("ra")));
        curBB.addInst(new Return());
        return null;
    }

    @Override
    public Object visitStoreInst(StoreInst storeInst) {
        var src= getOperand(storeInst.getStoreVal(), false);
        var ptr= getOperand(storeInst.getPtr(),false);
        assert ptr != null;
        if (storeInst.getStoreVal().getType().equals(Type.TheInt1)) {
            curBB.addInst(new Store(ptr, (Register) src, 1));
        } else {
            curBB.addInst(new Store(ptr, (Register) src, 4));
        }
        return null;
    }

    @Override
    public Object visitMovInst(MovInst movInst) {
        var from= getOperand(movInst.getFrom(),false);
        var to= getOperand(movInst.getTo(),false);
        curBB.addInst(new Move((Register)from,(Register)to));
        return null;
    }

    @Override
    public Object visit(Value value) {
        return value.accept(this);
    }


}
