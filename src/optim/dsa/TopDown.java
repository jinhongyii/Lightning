package optim.dsa;

import IR.Function;
import IR.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
public class TopDown {
    HashMap<Function,DSGraph> graphs;

    public TopDown(BottomUp bottomUp) {
        this.graphs=bottomUp.graphs;
    }
    private void calcPostOrder(ArrayList<DSGraph> postOrder, HashSet<DSGraph> visited,DSGraph curGraph){
        if (visited.contains(curGraph)) {
            return;
        }
        visited.add(curGraph);
        for (var callsite : curGraph.callSites) {
            calcPostOrder(postOrder,visited,graphs.get(callsite.callee));
        }
        postOrder.add(curGraph);
    }
    public void run(Module module){
        var mainGraph=graphs.get((Function)module.getSymbolTable().get("main"));
        var postOrder=new ArrayList<DSGraph>();
        calcPostOrder(postOrder,new HashSet<>(),mainGraph);
        while (!postOrder.isEmpty()) {
            mergeToCallee(postOrder.remove(postOrder.size()-1));
        }
    }
    private void mergeToCallee(DSGraph curGraph){
        HashSet<Function> clonedFunction=new HashSet<>();
        for (var callsite : curGraph.callSites) {
            var calleeG=graphs.get(callsite.callee);
            if (calleeG == null  || calleeG==curGraph) {
                continue;
            }
            calleeG.resolveCaller(curGraph,callsite.callee,callsite,clonedFunction );
            calleeG.removeDeadNodes();
        }
    }
}
