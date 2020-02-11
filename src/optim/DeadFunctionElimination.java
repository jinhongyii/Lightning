package optim;
import IR.Function;
import IR.Instruction;
import IR.Module;

import java.util.ArrayList;

public class DeadFunctionElimination implements Pass {
    Module module;
    public DeadFunctionElimination(Module module){
        this.module=module;
    }

    public boolean run(){
        boolean global_changed=false;
        boolean changed=true;
        while(changed) {
            ArrayList<Function> toDelete=new ArrayList<>();
            changed=false;
            for (var func : module.getFunctionList()) {
                changed|=dfe(toDelete, func);
            }
            module.getFunctionList().removeAll(toDelete);
            global_changed|=changed;
        }

        return global_changed;
    }

    private boolean dfe(ArrayList<Function> toDelete, Function func) {
        if (func.getName().equals("main")) {
            return false;
        }
        boolean hasUseOutsideFunc=false;
        for (var use = func.getUse_head(); use != null; use = use.getNext()) {
            if (((Instruction) use.getUser()).getParent().getParent() != func) {
                hasUseOutsideFunc = true;
                break;
            }
        }
        if (!hasUseOutsideFunc) {
            toDelete.add(func);
            for (var bb = func.getHead(); bb != null; ) {
                var tmp=bb.getNext();
                bb.delete();
                bb=tmp;
            }
        }
        return !hasUseOutsideFunc;
    }
}
