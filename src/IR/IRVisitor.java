package IR;

import IR.instructions.*;

public interface IRVisitor<ReturnType> {
    ReturnType visitModule(Module module);
    ReturnType visitFunction(Function function);
    ReturnType visitBasicBlock(BasicBlock basicBlock);
    ReturnType visitGlobalVariable(GlobalVariable globalVariable);
    ReturnType visitAllocaInst(AllocaInst allocaInst);
    ReturnType visitBinaryOpInst(BinaryOpInst binaryOpInst);
    ReturnType visitBranchInst(BranchInst branchInst);
    ReturnType visitCallInst(CallInst callInst);
    ReturnType visitCastInst(CastInst castInst);
    ReturnType visitGEPInst(GetElementPtrInst GEPInst);
    ReturnType visitIcmpInst(IcmpInst icmpInst);
    ReturnType visitLoadInst(LoadInst loadInst);
    ReturnType visitPhiNode(PhiNode phiNode);
    ReturnType visitReturnInst(ReturnInst returnInst);
    ReturnType visitStoreInst(StoreInst storeInst);
    ReturnType visitMovInst(MovInst movInst);
    ReturnType visit(Value value);
}
