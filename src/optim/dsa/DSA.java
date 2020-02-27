package optim.dsa;

import IR.*;
import IR.Module;
import optim.AliasAnalysis;

import java.util.HashMap;

public class DSA extends AliasAnalysis {
    private HashMap<Function,DSGraph> graphs=new HashMap<>();
    public DSA(){

    }
    @Override
    public AliasResult alias(Value v1, Value v2) {
        if (v1 == v2) {
            return AliasResult.MustAlias;
        }
        if(v1 instanceof GlobalVariable || v2 instanceof  GlobalVariable){
            return super.alias(v1,v2);
        }
        var graph1=getGraph(v1);
        var graph2=getGraph(v2);
        var handle1=graph1.scalarMap.get(v1);
        var handle2=graph2.scalarMap.get(v2);
        var node1=handle1.getNode();
        var node2=handle2.getNode();
        if (node1.isComplete() || node2.isComplete()) {
            if (node1 != node2) {
                return AliasResult.NoAlias;
            }
        }
        return super.alias(v1,v2);
    }
    DSGraph getGraph(Value value){
        if (value instanceof Instruction) {
            return graphs.get(((Instruction) value).getParent().getParent());
        } else if (value instanceof Argument) {
            return graphs.get(((Argument) value).getParent());
        }
        return null;
    }
    public void run(Module module){
        for (var func : module.getFunctionList()) {
            var newGraph=new DSGraph();
            LocalAnalysis localAnalysis=new LocalAnalysis(newGraph);
            localAnalysis.run(func);
            graphs.put(func,newGraph);
        }
    }
}
