package Riscv;

public interface  Visitor {
      void visitBranch(Branch inst);
      void visitCall(Call inst);
      void visitGlobalVar(GlobalVar gvar);
      void visitI_type(I_Type inst);
      void visitJump(Jump inst);
      void visitLI(LI inst);
      void visitLA(LA inst);
      void visitLoad(Load inst);
      void visitBB(MachineBasicBlock bb);
      void visitFunction(MachineFunction function);
      void visitModule(MachineModule module);
      void visitMove(Move inst);
      void visitReturn(Return inst);
      void visitR_type(R_Type inst);
      void visitStore(Store inst);
      void visitLUI(LUI inst);
      void visit(MachineInstruction inst);
}
