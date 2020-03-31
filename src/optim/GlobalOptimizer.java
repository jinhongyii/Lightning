package optim;

import IR.Function;
import IR.Module;
import optim.dsa.DSA;

import java.util.ArrayList;

public class GlobalOptimizer implements Pass {
    Module module;
    Inliner inliner;
    AliasAnalysis aa;
    DeadFunctionElimination dfe;
    ArrayList<Optimizer> localOptimizers=new ArrayList<>();
    public GlobalOptimizer(Module module){
        this.module=module;
        inliner=new Inliner(module);
        aa=new DSA(module);
        dfe=new DeadFunctionElimination(module);
    }
    public void run(){
        boolean changed=true;
        while (changed) {
            changed=performLocalOptim();
//            changed|=inliner.run();
            dfe.run();
        }
        destructSSA();
    }
    private void destructSSA(){
        for (var func : module.getFunctionList()) {
            if(!func.isExternalLinkage()) {
                SSADestructor destructor = new SSADestructor(func);
                destructor.run();
            }
        }
    }
    private boolean sccp(){
        boolean changed=false;
        for (var optim : localOptimizers) {
            changed|=optim.sccp();
        }
        return changed;
    }
    private void domUpdate(){
        for(var optim:localOptimizers){
            optim.domUpdate();
        }
    }
    private boolean cse(){
        boolean changed=false;
        for (var optim : localOptimizers) {
            changed|=optim.cse();
        }
        return changed;
    }
    private boolean adce(){
        boolean changed=false;
        for (var optim : localOptimizers) {
            changed|=optim.adce();
        }
        return changed;
    }
    private boolean redundantLoadElim(){
        boolean changed=false;
        for (var optim : localOptimizers) {
            changed|=optim.redundantLoadElim();
        }
        return changed;
    }
    private void loopAnalysis(){
        for (var optim : localOptimizers) {
            optim.loopAnalysis();
        }
    }
    private boolean strengthReduce(){
        boolean changed=false;
        for (var optim : localOptimizers) {
            changed|=optim.strengthReduce();
        }
        return changed;
    }
    private boolean licm(){
        boolean changed=false;
        for (var optim : localOptimizers) {
            changed|=optim.licm();
        }
        return changed;
    }
    private boolean instComb(){
        boolean changed=false;
        for (var optim : localOptimizers) {
            changed|=optim.instCombine();
        }
        return changed;
    }
    private void CFGSimplify(){
        for (var optim : localOptimizers) {
            optim.CFGSimplify();
        }
    }
    private void mem2reg(){
        for (var optim : localOptimizers) {
            optim.mem2reg();
        }
    }
    private boolean performLocalOptim(){
        localOptimizers.clear();
        for (var func : module.getFunctionList()) {
            if (!func.isExternalLinkage()) {
                localOptimizers.add(new Optimizer(func,aa));

            }
        }
        CFGSimplify();
        domUpdate();
        mem2reg();
        boolean changed=true;
        boolean globalChanged=false;
        while(changed){
            changed=false;
            domUpdate();
            changed|=sccp();
            changed|=cse();
            aa.run(module);
            changed|=adce();
            changed|=redundantLoadElim();
            loopAnalysis();
            domUpdate();
            aa.run(module);
            changed|=strengthReduce();
            aa.run(module);
//            try {
//                IRPrinter printer=new IRPrinter(module, "step1.ll");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            changed|=licm();
//            try {
//                IRPrinter printer=new IRPrinter(module, "step2.ll");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            instComb();
            CFGSimplify();
            globalChanged|=changed;

        }

        return globalChanged;
    }
}
