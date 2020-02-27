package optim;

import IR.Argument;
import IR.Instruction;
import IR.Value;
import IR.instructions.CallInst;
import IR.instructions.GetElementPtrInst;
import IR.instructions.LoadInst;
import IR.instructions.StoreInst;
import IR.Module;

//an extremely simplified version but enough
public class AliasAnalysis implements Pass {
    public enum AliasResult{MustAlias,MayAlias,NoAlias}
    public AliasResult alias(Value v1,Value v2){
        //todo
        if (v1 == v2) {
            return AliasResult.MustAlias;
        }
//        if (v1 instanceof GetElementPtrInst && v2 instanceof GetElementPtrInst && ((GetElementPtrInst) v1).getOperands().size()==((GetElementPtrInst) v2).getOperands().size()) {
//            var ptr1=((GetElementPtrInst) v1).getOperands().get(0).getVal();
//            var ptr2=((GetElementPtrInst) v2).getOperands().get(0).getVal();
//            for (int i = 1; i < ((GetElementPtrInst) v1).getOperands().size(); i++) {
//                if (((GetElementPtrInst) v1).getOperands().get(i).getVal() != ((GetElementPtrInst) v2).getOperands().get(i).getVal()) {
//                    return AliasResult.MayAlias;
//                }
//            }
//            if (alias(ptr1, ptr2) == AliasResult.MustAlias) {
//                return AliasResult.MustAlias;
//            } else {
//                return AliasResult.MayAlias;
//            }
//        }
        if (!v1.getType().equals(v2.getType())) {
            return AliasResult.NoAlias;
        }
        return AliasResult.MayAlias;
    }
    public enum ModRef{ModRef,Mod,Ref,NoModRef}
    private ModRef getCallModRefInfo(CallInst callInst,Value value){
        //todo
        return ModRef.ModRef;
    }
    private ModRef getLoadModRefInfo(LoadInst loadInst,Value value){
        if (alias(loadInst.getLoadTarget() , value)!=AliasResult.NoAlias) {
            return ModRef.Ref;
        }
        return ModRef.NoModRef;
    }
    private ModRef getStoreModRefInfo(StoreInst storeInst,Value value){
        if (alias((storeInst.getPtr()), value)!=AliasResult.NoAlias) {
            return ModRef.Mod;
        }
        return ModRef.NoModRef;
    }
    public ModRef getModRefInfo(Instruction instruction,Value value){
        if (instruction instanceof CallInst) {
            return getCallModRefInfo((CallInst) instruction,value);
        }else if(instruction instanceof LoadInst){
            return getLoadModRefInfo((LoadInst)instruction, value);
        }else if(instruction instanceof StoreInst){
            return getStoreModRefInfo((StoreInst)instruction,value);
        }
        return ModRef.NoModRef;
    }
    public AliasAnalysis(){

    }
    public void run(Module module){

    }
}
