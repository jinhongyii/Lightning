package optim;

import IR.BasicBlock;
import IR.Function;
import IR.instructions.AllocaInst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Mem2reg implements FunctionPass {
    private DominatorAnalysis dominatorAnalyzer=new DominatorAnalysis();

    @Override
    public void runOnFunction(Function function) {
        dominatorAnalyzer.runOnFunction(function);
        ArrayList<AllocaInst> allocaInsts=new ArrayList<>();
        BasicBlock entryBlock=function.getEntryBB();
        for (var inst =entryBlock.getHead();inst!=null;inst=inst.getNext()) {
            if (inst instanceof AllocaInst) {
                allocaInsts.add((AllocaInst) inst);
            }
        }
//        for(var )
    }

}
