package optim.dsa;

import IR.Function;
import IR.Types.PointerType;
import IR.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DSGraph {
    HashSet<DSNode> nodes=new HashSet<>();
    ArrayList<DSCallNode> callSites=new ArrayList<>();
    HashMap<Value, DSHandle> scalarMap=new HashMap<>();
    DSHandle pi;

    public DSGraph() {
        pi=new DSHandle(null,0);
    }

    static void cloneGraphInto(DSGraph graph1, DSGraph graph2){

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Object clone = super.clone();
        var nodes=this.nodes.clone();
        return null;
//        var callSites=new
    }
    public void markIncomplete(){
        for (var callSite : callSites) {
            Function function=callSite.callee;
            if (!function.getName().equals("main") && !function.isExternalLinkage()) {
                for (var arg : function.getArguments()) {
                    if (arg.getType() instanceof PointerType) {
                        markIncomplete(scalarMap.get(arg).getNode());
                    }
                }
            }
            markIncomplete(callSite);
        }
        for (var node : nodes) {
            if (node.isGlobal()) {
                markIncomplete(node);
            }
        }
    }
    private void markIncomplete(DSNode node){
        if (node.isInComplete()) {
            return;
        }
        node.setInComplete();
        for (var out : node.outGoingEdge) {
            if (out.getNode() != null) {
                markIncomplete(out.getNode());
            }
        }
    }

    private void markIncomplete(DSCallNode callNode) {
        markIncomplete(callNode.returnValue.getNode());
        for (var args : callNode.arguments) {
            markIncomplete(args.getNode());
        }
    }
}
