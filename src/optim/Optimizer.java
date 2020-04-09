package optim;

import IR.Function;
import IR.IRPrinter;

import java.io.IOException;

public class Optimizer extends FunctionPass{
    private CFGSimplifier cfgSimplifier;
    private DominatorAnalysis dominatorAnalysis;
    public LoopAnalysis loopAnalysis;
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
        this.aa=aa;
        cfgSimplifier=new CFGSimplifier(function);
        dominatorAnalysis=new DominatorAnalysis(function);
        mem2reg=new Mem2reg(function,dominatorAnalysis);
        adce=new ADCE(function,dominatorAnalysis,aa);
        sccp=new SCCP(function);
        instCombine=new InstCombine(function,dominatorAnalysis);
        loopAnalysis=new LoopAnalysis(function,dominatorAnalysis,aa);
        licm=new LICM(function,loopAnalysis,dominatorAnalysis,aa);
        strengthReduction=new StrengthReduction(function,loopAnalysis,dominatorAnalysis,aa);
        cse=new CSE(function,dominatorAnalysis);
        loadElimination=new RedundantLoadElimination(function,dominatorAnalysis,aa);
    }
    public void domUpdate(){
        dominatorAnalysis.run();
    }
    public boolean sccp(){
        return sccp.run();
    }
    public boolean cse(){
        return cse.run();
    }
    public boolean adce(){
        return adce.run();
    }
    public boolean redundantLoadElim(){
        return loadElimination.run();
    }
    public void loopAnalysis(){
        loopAnalysis.run();
    }
    public boolean strengthReduce(){
        return strengthReduction.run();
    }
    public boolean licm(){
        return licm.run();
    }
    public boolean instCombine(){
        return instCombine.run();
    }
    public boolean CFGSimplify(){
        return cfgSimplifier.run();
    }
    public void mem2reg(){
        mem2reg.run();
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
            dominatorAnalysis.run();
            changed|=cse.run();
            aa.run(function.getParent());
            changed|=adce.run();
            changed|=loadElimination.run();
            loopAnalysis.run();
            aa.run(function.getParent());
            changed|=strengthReduction.run();
            aa.run(function.getParent());
            changed|=licm.run();
            instCombine.run();
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
}
