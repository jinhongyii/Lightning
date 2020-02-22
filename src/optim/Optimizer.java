package optim;

import IR.Function;
import IR.IRPrinter;

import java.io.IOException;

public class Optimizer extends FunctionPass{
    private CFGSimplifier cfgSimplifier;
    private DominatorAnalysis dominatorAnalysis;
    private LoopAnalysis loopAnalysis;
    private Mem2reg mem2reg;
    private ADCE adce;
    private SCCP sccp;
    private CSE cse;
    private InstCombine instCombine;
    private LICM licm;
    private StrengthReduction strengthReduction;
    private AliasAnalysis aa;
    private RedundantLoadElimination loadElimination;
    public Optimizer(Function function,AliasAnalysis aa) {
        super(function);
        cfgSimplifier=new CFGSimplifier(function);
        dominatorAnalysis=new DominatorAnalysis(function);
        mem2reg=new Mem2reg(function,dominatorAnalysis);
        adce=new ADCE(function,dominatorAnalysis);
        sccp=new SCCP(function);
        instCombine=new InstCombine(function,dominatorAnalysis);
        loopAnalysis=new LoopAnalysis(function,dominatorAnalysis);
        licm=new LICM(function,loopAnalysis,dominatorAnalysis,aa);
        strengthReduction=new StrengthReduction(function,loopAnalysis,dominatorAnalysis);
        cse=new CSE(function,dominatorAnalysis);
        loadElimination=new RedundantLoadElimination(function,dominatorAnalysis,aa);
    }

    @Override
    public boolean run() {
        cfgSimplifier.run();
        dominatorAnalysis.run();
        mem2reg.run();
        boolean global_changed=false;
        boolean changed=true;
        while(changed) {
            changed=false;
            dominatorAnalysis.run();
            changed|=sccp.run();
            changed|=adce.run();
            loopAnalysis.run();
            dominatorAnalysis.run();
            changed|=strengthReduction.run();
            changed|=instCombine.run();
            changed|=cse.run();
            changed|=loadElimination.run();
            changed|=cse.run();
            changed|=licm.run();
            cfgSimplifier.run();
            global_changed|=changed;
            try {
                IRPrinter ssaPrinter=new IRPrinter(function.getParent(),"ssa.ll");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return global_changed;
    }
    public void runPart(){
        cfgSimplifier.run();
        dominatorAnalysis.run();
        mem2reg.run();
        boolean changed=true;
        for(int i=0;i<1;i++) {
            changed=false;
            dominatorAnalysis.run();
            changed|=sccp.run();
            changed|=adce.run();
            changed|=cse.run();
            changed|=instCombine.run();
//            changed|=cfgSimplifier.run();
        }
        return ;
    }
}
