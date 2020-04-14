package optim;

import IR.Function;
import IR.IRPrinter;
import IR.Module;
import optim.dsa.DSA;

import java.io.IOException;
import java.util.HashMap;

public class GlobalOptimizer implements Pass {
    Module module;
    Inliner inliner;
    AliasAnalysis aa;
    DeadFunctionElimination dfe;
    public HashMap<Function,Optimizer> localOptimizers=new HashMap<>();
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
            changed|=inliner.run();
            dfe.run();
//            try {
//                new IRPrinter(module,"inline.ll",true);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
//        try {
//            IRPrinter finalPrinter=new IRPrinter(module,"final.ll", true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        destructSSA();
//        try {
//            new IRPrinter(module,"after_phi_elim.ll",true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        updateLoopAnalysis();
    }
    private void updateLoopAnalysis(){
        for (var optim : localOptimizers.values()) {
            optim.loopAnalysis.runWithoutModify();
        }
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
        for (var optim : localOptimizers.values()) {
            changed|=optim.sccp();
        }
        return changed;
    }
    private void domUpdate(){
        for(var optim:localOptimizers.values()){
            optim.domUpdate();
        }
    }
    private boolean cse(){
        boolean changed=false;
        for (var optim : localOptimizers.values()) {
            changed|=optim.cse();
        }
        return changed;
    }
    private boolean adce(){
        boolean changed=false;
        for (var optim : localOptimizers.values()) {
            changed|=optim.adce();
        }
        return changed;
    }
    private boolean redundantLoadElim(){
        boolean changed=false;
        for (var optim : localOptimizers.values()) {
            changed|=optim.redundantLoadElim();
        }
        return changed;
    }
    private void loopAnalysis(){
        for (var optim : localOptimizers.values()) {
            optim.loopAnalysis();
        }
    }
    private boolean strengthReduce(){
        boolean changed=false;
        for (var optim : localOptimizers.values()) {
            changed|=optim.strengthReduce();
        }
        return changed;
    }
    private boolean licm(){
        boolean changed=false;
        for (var optim : localOptimizers.values()) {
            changed|=optim.licm();
        }
        return changed;
    }
    private boolean instComb(){
        boolean changed=false;
        for (var optim : localOptimizers.values()) {
            changed|=optim.instCombine();
        }
        return changed;
    }
    private void CFGSimplify(){
        for (var optim : localOptimizers.values()) {
            optim.CFGSimplify();
        }
    }
    private void mem2reg(){
        for (var optim : localOptimizers.values()) {
            optim.mem2reg();
        }
    }
    private boolean performLocalOptim(){
        localOptimizers.clear();
        for (var func : module.getFunctionList()) {
            if (!func.isExternalLinkage()) {
                localOptimizers.put(func,new Optimizer(func,aa));

            }
        }
        CFGSimplify();
        domUpdate();
        mem2reg();
        boolean changed=true;
        boolean globalChanged=false;
        while(changed){
            changed=false;

            changed|=sccp();
            CFGSimplify();
            domUpdate();
            changed|=cse();
            aa.run(module);
            changed|=adce();
            changed|=redundantLoadElim();
            loopAnalysis();
            domUpdate();
            aa.run(module);
            changed|=strengthReduce();
            aa.run(module);
            changed|=licm();
            instComb();
            CFGSimplify();
            domUpdate();
            adce();
            globalChanged|=changed;


        }

        return globalChanged;
    }
}
