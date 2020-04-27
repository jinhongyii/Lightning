package optim.dsa;

import IR.Function;
import IR.Module;
import IR.Value;
import IR.instructions.CallInst;
import optim.AliasAnalysis;

import java.util.HashMap;

public class DSA extends AliasAnalysis {
    private Local local;
    private BottomUp bottomUp;
    private TopDown topDown;
    private Module module;
    private HashMap<DSGraph,ModRef> modRefHashMap;
    public DSA(Module module){
        super(module);
        this.module=module;
    }
    @Override
    public AliasResult alias(Value v1, Value v2) {
        if (v1 == v2) {
            return AliasResult.MustAlias;
        }
        if (!v1.getType().equals(v2.getType())) {
            return AliasResult.NoAlias;
        }
        var mainGraph=bottomUp.graphs.get((Function)module.getSymbolTable().get("main"));
        var handle1=mainGraph.scalarMap.get(v1);
        var handle2=mainGraph.scalarMap.get(v2);
        if (handle1 == null || handle2 == null) {
            return super.alias(v1,v2);
        }
        var node1=handle1.getNode();
        var node2=handle2.getNode();

        if (node1 != node2 || handle1.field!=handle2.field) {
            return AliasResult.NoAlias;
        } else {
            if (node1.isArray()) {
                return super.alias(v1,v2);
            }
            if (node1.globalValue.size() == 1) {
                return AliasResult.MustAlias;
            }
        }
        return super.alias(v1,v2);
    }
//    DSGraph getGraph(Value value){
//        if (value instanceof Instruction) {
//            return graphs.get(((Instruction) value).getParent().getParent());
//        } else if (value instanceof Argument) {
//            return graphs.get(((Argument) value).getParent());
//        }
//        return null;
//    }
    public void run(Module module){
        local=new Local();
        local.run(module);
        bottomUp=new BottomUp(local);
        bottomUp.run(module);
        topDown=new TopDown(bottomUp);
        topDown.run(module);
        updateModRefMap();
    }

    private void updateModRefMap() {
        modRefHashMap=new HashMap<>();

        for (var graph : topDown.graphs.values()) {
            boolean ref=false,mod=false;
            for (var node : graph.nodes) {
                if (node.isRef()) {
                    ref=true;
                }
                if (node.isMod()) {
                    mod=true;
                }
                if (ref && mod) {
                    break;
                }
            }
            ModRef result;
            if (ref && mod) {
                result= ModRef.ModRef;
            } else if (ref) {
                result= ModRef.Ref;
            } else if (mod) {
                result = ModRef.Mod;
            } else {
                result= ModRef.NoModRef;
            }
            modRefHashMap.put(graph,result);
        }
    }

    @Override
    public ModRef getCallModRefInfo(CallInst callInst, Value value) {

        var calleeF=callInst.getCallee();
        if (calleeF.isExternalLinkage()) {
            return super.getCallModRefInfo(callInst,value);
        }
        var graph = topDown.graphs.get(calleeF);
        var handle=graph.scalarMap.get(value);
        if (handle != null) {
            var node=handle.getNode();
            if (node.isMod() && node.isRef()) {
                return ModRef.ModRef;
            } else if (node.isMod()) {
                return ModRef.Mod;
            } else if (node.isRef()) {
                return ModRef.Ref;
            }else {
                return ModRef.NoModRef;
            }
        } else {
            return ModRef.NoModRef;
        }
    }

    @Override
    public ModRef getFunctionModRefInfo(Function function) {
//        if(function.isExternalLinkage()) {
//            return super.getFunctionModRefInfo(function);
//        }
        var graph=topDown.graphs.get(function);
        return modRefHashMap.get(graph);
    }
}
