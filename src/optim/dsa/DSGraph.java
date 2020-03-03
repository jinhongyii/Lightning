package optim.dsa;

import IR.Function;
import IR.Types.PointerType;
import IR.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DSGraph {
    HashSet<DSNode> nodes=new HashSet<>();
    //there's no unresolved call sites ,so it will be only added but not be modified
    ArrayList<DSCallNode> callSites=new ArrayList<>();
    HashMap<Value, DSHandle> scalarMap=new HashMap<>();
    //namely pi
    HashMap<Function,DSHandle> retNodes =new HashMap<>();

    public DSGraph(Function function){
        retNodes.put(function,new DSHandle(null,0));
        GraphBuilder graphBuilder=new GraphBuilder(this);
        graphBuilder.run(function);
    }
    public void removeDeadNodes(){
        for (var node : nodes) {
            for (var outgoing : node.outGoingEdge) {
                outgoing.getNode();
            }
        }
        for (var handle : scalarMap.values()) {
            handle.getNode();
        }
        ArrayList<DSNode> toDelete=new ArrayList<>();
        for (var node : nodes) {
            if (node.isDeleted()) {
                toDelete.add(node);
            }
        }
        for (var del : toDelete) {
            nodes.remove(del);
        }
    }


    void cloneGraphInto(DSGraph other, HashMap<Function, DSHandle> clonedretNodes, boolean cloneCallSite, HashMap<DSNode, DSNode> nodeMap, int mask){
        for (var node : other.nodes) {
            var newNode=new DSNode(node,this);
            newNode.flag &=mask;
            nodeMap.put(node,newNode);
        }
        for (var node : nodes) {
            for (var outEdge : node.outGoingEdge) {
                DSNode cloneNode = nodeMap.get(outEdge.getNode());
                if (cloneNode != null) {
                    outEdge.setNode(cloneNode);
                }
            }
        }
        for (var entry : other.scalarMap.entrySet()) {
            var correspondingNode=nodeMap.get(entry.getValue().getNode());
            var entryInCurMap = scalarMap.get(entry.getKey());
            if (entryInCurMap != null) {
//                assert entry.getKey() instanceof GlobalVariable;
                scalarMap.put(entry.getKey(), DSHandle.mergeCells(new DSHandle(correspondingNode,entry.getValue().field), entryInCurMap));
            } else {
                scalarMap.put(entry.getKey(),new DSHandle(correspondingNode,entry.getValue().field));
            }
        }

        for (var entry : other.retNodes.entrySet()) {
            var correspondingNode = nodeMap.get(entry.getValue().getNode());
            clonedretNodes.put(entry.getKey(), new DSHandle(correspondingNode, entry.getValue().field));
        }
        if (cloneCallSite) {
            for (var callSite : other.callSites) {
                callSites.add(new DSCallNode(callSite,nodeMap));
            }
        }
    }
    public void resolveCallee(DSGraph callee,Function calleeF,DSCallNode callSite){
//        if (calleeF.isExternalLinkage()) {
//            return;
//        }
        if (callee != this) {
            var clonedRetNodes=new HashMap<Function,DSHandle>();
            cloneGraphInto(callee, clonedRetNodes, false, new HashMap<>(), ~0);
            DSHandle.mergeCells(clonedRetNodes.get(calleeF), callSite.returnValue);
        } else {
            DSHandle.mergeCells(retNodes.get(calleeF), callSite.returnValue);
        }
        int j=0;
        for (int i=0;i<callSite.arguments.size();i++) {
            if (calleeF.getArguments().get(i).getType() instanceof PointerType) {
                DSHandle.mergeCells(callSite.arguments.get(j), scalarMap.get(calleeF.getArguments().get(i)));
                j++;
            }
        }
    }

    public void resolveCaller(DSGraph caller,Function calleeF,DSCallNode callSite){
//        if (calleeF.isExternalLinkage()) {
//            return;
//        }
        assert caller!=this;

        var clonedRetNodes=new HashMap<Function,DSHandle>();
        var NodeMap=new HashMap<DSNode,DSNode>();
        cloneGraphInto(caller, clonedRetNodes, false, NodeMap, ~0x30);
        DSHandle.mergeCells(new DSHandle(NodeMap.get(callSite.returnValue.getNode()), callSite.returnValue.field), retNodes.get(calleeF));
        int j=0;
        for (int i=0;i<callSite.arguments.size();i++) {
            if (calleeF.getArguments().get(i).getType() instanceof PointerType) {
                var origArg=callSite.arguments.get(j);
                var newArg=new DSHandle(NodeMap.get(origArg.getNode()),origArg.field);
                DSHandle.mergeCells(newArg, scalarMap.get(calleeF.getArguments().get(i)));
                j++;
            }
        }
    }
    //wrong function, but not needed
    public void markIncomplete(boolean localPhase){
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
