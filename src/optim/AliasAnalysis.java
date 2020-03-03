package optim;

import IR.*;
import IR.Module;
import IR.instructions.CallInst;
import IR.instructions.GetElementPtrInst;
import IR.instructions.LoadInst;
import IR.instructions.StoreInst;

//an extremely simplified version but enough
public class AliasAnalysis implements Pass {
    public enum AliasResult{MustAlias,MayAlias,NoAlias}
    public AliasResult alias(Value v1,Value v2){
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
    public ModRef getFunctionModRefInfo(Function function){
        if (function.isExternalLinkage()) {
            switch (function.getName()) {
                case "print":
                case "println":
                case "printlnInt":
                case "printInt":
                    return ModRef.Mod;
                case "getString":
                case "getInt":
                case "malloc"://malloc can't be hoisted but can be eliminated so we can mark it ref
                    return ModRef.Ref;
                case "toString":
                case "string_length"://because it references a constant memory location:
                case "string_substring":
                case "string_parseInt":
                case "string_ord":
                case "_array_size":
                case "string_add":
                case "string_eq":
                case "string_ne":
                case "string_gt":
                case "string_ge":
                case "string_lt":
                case "string_le":
                    return ModRef.NoModRef;
                default:
                    throw new Error("wrong external function");
            }
        }
        return ModRef.ModRef;
    }
    public enum ModRef{ModRef,Mod,Ref,NoModRef}
    public ModRef getCallModRefInfo(CallInst callInst,Value value){
        //todo: external functions may reference memory location
        if (callInst.getCallee().isExternalLinkage()) {
            return ModRef.NoModRef;
        }
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
    public AliasAnalysis(Module module){

    }
    public void run(Module module){

    }
}
