package optim;

import IR.Function;
import IR.Instruction;
import IR.Module;
import IR.instructions.CallInst;
import IR.instructions.StoreInst;

import java.util.LinkedList;

public class DeadCodeElimination extends FunctionPass {

    public DeadCodeElimination(Function function) {
        super(function);
    }

    @Override
    public boolean run() {
        return naiveDeadCodeElimination();
    }
    public static void runOnModule(Module module){
        for (var func : module.getFunctionList()) {
            if (!func.isExternalLinkage()) {
                var dce=new DeadCodeElimination(func);
                dce.run();
            }
        }
    }
    private boolean canSafelyDelete(Instruction inst){
        return inst.getUse_head()==null && !(inst instanceof  CallInst) && !(inst instanceof StoreInst) &&!inst.isTerminator();
    }
    private boolean naiveDeadCodeElimination(){
        boolean changed=false;
        LinkedList<Instruction> workList=new LinkedList<>();
        for (var bb = function.getHead(); bb != null; bb = bb.getNext()) {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                workList.addLast(inst);
            }
        }
        while (!workList.isEmpty()) {
            var inst=workList.pollLast();
            if (canSafelyDelete(inst) ) {
                for (var use : inst.getOperands()) {
                    if (use.getVal() instanceof Instruction) {
                        workList.addLast((Instruction) use.getVal());
                    }
                }
                inst.delete();
                changed=true;
            }
        }
        return changed;
    }
}
