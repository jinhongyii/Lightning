package optim.dsa;

import IR.Function;
import IR.Module;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class BottomUp {
    HashMap<Function,DSGraph> graphs;
    HashMap<Function,Integer> dfn=new HashMap<>();
    HashMap<Function,Integer> low=new HashMap<>();
    int nextID;

    public BottomUp(Local local) {
        this.graphs=local.graphs;
    }
    void run(Module module){
        low.clear();
        dfn.clear();
        for (var func : graphs.keySet()) {
            dfn.put(func,0);
        }
        nextID=0;
        var mainFunc=module.getSymbolTable().get("main");
        TarjanVisitNode((Function) mainFunc,new Stack<>());
    }
    private void TarjanVisitNode(Function function, Stack<Function> stack){
        nextID++;
        dfn.put(function,nextID);
        low.put(function,nextID);
        stack.push(function);
        for (var callSite : graphs.get(function).callSites) {
            Function fc = callSite.callee;
            if (!fc.isExternalLinkage()) {
                if (dfn.get(fc) == 0) {
                    TarjanVisitNode(fc, stack);
                    low.put(function, Math.min(low.get(function), low.get(fc)));
                } else {
                    low.put(function,Math.min(low.get(function), dfn.get(fc)));
                }
            }
        }
        HashSet<Function> scc=new HashSet<>();
        if (dfn.get(function).equals(low.get(function))) {
            while(true) {
                var top = stack.pop();
                dfn.put(function,Integer.MAX_VALUE);
                scc.add(top);
                if (top == function) {
                    break;
                }
            }
            ProcessSCC(scc);

        }
    }
    private void ProcessSCC(HashSet<Function> SCC ){
        for (Function function : SCC) {
            for (var callSite : graphs.get(function).callSites) {
                if(!SCC.contains(callSite.callee)) {
                    graphs.get(function).resolveCallee(graphs.get(callSite.callee), callSite.callee, callSite);
                }
            }
        }
        DSGraph SCCGraph=null;
        for (var func : SCC) {
            if (SCCGraph == null) {
                SCCGraph = graphs.get(func);
            } else {
                SCCGraph.cloneGraphInto(graphs.get(func), SCCGraph.retNodes, true, new HashMap<>(), ~0);
                graphs.put(func,SCCGraph);
            }
        }
        for (Function function : SCC) {
            for (var callSite : graphs.get(function).callSites) {
                if(SCC.contains(callSite.callee)) {
                    graphs.get(function).resolveCallee(graphs.get(callSite.callee), callSite.callee, callSite);
                }
            }
        }
        SCCGraph.removeDeadNodes();
    }
}
