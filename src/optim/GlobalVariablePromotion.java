package optim;
import IR.*;
import IR.Module;
import IR.Types.PointerType;
import IR.instructions.AllocaInst;
import IR.instructions.StoreInst;

import java.util.HashSet;

public class GlobalVariablePromotion implements Pass {
    Module module;
    public GlobalVariablePromotion(Module module){
        this.module=module;
    }
    boolean run(){
        HashSet<GlobalVariable> toDelete=new HashSet<>();
        for (var globalVar : module.getGlobalList()) {
            if(!(globalVar.getInitializer() instanceof ConstantString)) {
                var func = hasUseInOnlyFunction(globalVar);
                if (func != null) {
                    toDelete.add(globalVar);
                    Instruction insertPoint;
                    insertPoint = func.getEntryBB().getHead();
                    while (insertPoint instanceof AllocaInst) {
                        insertPoint = insertPoint.getNext();
                    }
                    var replace = new AllocaInst(globalVar.getName(), ((PointerType) globalVar.getType()).getPtrType());
                    func.getEntryBB().addInstToFirst(replace);
                    var init = globalVar.getInitializer();
                    func.getEntryBB().addInstBefore(insertPoint, new StoreInst(init == null ? new ConstantNull() : init, replace));
                    globalVar.transferUses(replace);
                }
            }
        }
        module.getGlobalList().removeAll(toDelete);
        return !toDelete.isEmpty();
    }
    Function hasUseInOnlyFunction(GlobalVariable gv){
        Function onlyFunc=null;
        for (var use = gv.getUse_head(); use != null; use = use.getNext()) {
            var parent=((Instruction) use.getUser()).getParent().getParent();
            if (onlyFunc == null) {
                onlyFunc = parent;
            } else {
                if (onlyFunc != parent) {
                    return null;
                }
            }
        }
        return onlyFunc;
    }
}
