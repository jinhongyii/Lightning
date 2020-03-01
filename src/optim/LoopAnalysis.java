package optim;


import IR.*;
import IR.instructions.BranchInst;
import IR.instructions.PhiNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class LoopAnalysis extends FunctionPass {
    public static class Loop{
        Loop parent;
        ArrayList<Loop> subLoops;
        HashSet<BasicBlock> basicBlocks;
        BasicBlock header;
        public Loop(BasicBlock head) {
            this.subLoops = new ArrayList<>();
            this.basicBlocks=new HashSet<>();
            basicBlocks.add(head);
            this.header=head;
        }
        public void addSubLoop(Loop subLoop){
            subLoops.add(subLoop);
        }

        public void setParent(Loop parent) {
            this.parent=parent;
            parent.subLoops.add(this);
        }
        public BasicBlock getHeader(){
            return header;
        }
        public boolean contains(BasicBlock basicBlock){
            return basicBlocks.contains(basicBlock);
        }
        public BasicBlock getPreHeader(){
            for (var i : header.getPredecessors()) {
                if (!contains(i)) {
                    return i;
                }
            }
            return null;
        }
        public BasicBlock getBackEdge(){
            for (var i : header.getPredecessors()) {
                if (contains(i)) {
                    return i;
                }
            }
            return null;
        }
    }
    DominatorAnalysis dominatorAnalysis;
    ArrayList<Loop> topLoops=new ArrayList<>();
    HashMap<BasicBlock,Loop> loopMap=new HashMap<>();
    public LoopAnalysis(Function function,DominatorAnalysis dominatorAnalysis) {
        super(function);
        this.dominatorAnalysis=dominatorAnalysis;
    }

    @Override
    public boolean run() {
        topLoops.clear();
        loopMap.clear();
        dfs(dominatorAnalysis.treeRoot);
        addPreHeader();
        addBackedgeBB();
        cleanUp();
        return false;
    }
    private void cleanUp(){
        ADCE adce=new ADCE(function,dominatorAnalysis);
        SCCP sccp=new SCCP(function);
        dominatorAnalysis.run();
        boolean changed=true;
        while (changed) {
            changed=false;
            changed|=adce.run();
            changed|=sccp.run();
        }
    }
    private void dfs(DominatorAnalysis.Node node){
        findLoop(node);
        for (var child : node.children) {
            dfs(child);
        }
    }
    private void findLoop(DominatorAnalysis.Node node){
        var bb=node.basicBlock;
        Loop loop=new Loop(bb);

        LinkedList<BasicBlock> workList=new LinkedList<>();
        for (var pred : bb.getPredecessors()) {
            var predNode=dominatorAnalysis.DominantTree.get(pred);
            if (node.dominate(predNode)) {
                workList.addLast(pred);
            }
        }
        if (workList.isEmpty()) {
            return;
        }
        var parentLoop=loopMap.get(bb);
        if (parentLoop != null) {
            loop.setParent(parentLoop);
        } else {
            topLoops.add(loop);
        }
        loopMap.put(bb,loop);
        while (!workList.isEmpty()) {
            var bbInLoop = workList.pollLast();
            if (loop.basicBlocks.contains(bbInLoop) ||!node.dominate(dominatorAnalysis.DominantTree.get(bbInLoop))) {
                continue;
            }
            loop.basicBlocks.add(bbInLoop);
            loopMap.put(bbInLoop,loop);
            for (var pred : bbInLoop.getPredecessors()) {
                workList.addLast(pred);
            }
        }
    }
    private void addPreHeader(){
        for (var loop : topLoops){
            recursiveAddPreHeader(loop);
        }
    }
    private void recursiveAddPreHeader(Loop loop){
        var tmp=curLoop;
        var newPreHeader=addPreHeader(loop);
        if (newPreHeader!=null && curLoop!=null) {
            loopMap.put(newPreHeader,curLoop);
            curLoop.basicBlocks.add(newPreHeader);
        }
        curLoop=loop;
        for (var subLoop : loop.subLoops) {
            recursiveAddPreHeader(subLoop);
        }
        curLoop=tmp;
    }
    private Loop curLoop=null;
    private BasicBlock addPreHeader(Loop loop) {
        var header=loop.getHeader();
        int cnt=0;
        var hasOnlySuc=true;
        for (var pred : header.getPredecessors()) {
            if(!loop.contains(pred)){
                if (pred.getSuccessors().size() > 1) {
                    hasOnlySuc=false;
                }
                cnt++;
            }
        }
        if (cnt <= 1 && hasOnlySuc) {
            return null;
        }
        var preHeader=function.addBB("preHeader");
        preHeader.addInst(new BranchInst(header, null,null));
        for (var phi = header.getHead(); phi instanceof PhiNode;phi=phi.getNext()) {
            var newPhi=new PhiNode("preheader."+phi.getName(), phi.getType());
            preHeader.addInstToFirst(newPhi);
            for (var pred : header.getPredecessors()) {
                if (!loop.contains(pred) && pred != preHeader) {
                    var val=((PhiNode) phi).findValue(pred);
                    ((PhiNode) phi).removeIncoming(pred);
                    newPhi.addIncoming(val,pred);
                }
            }
            ((PhiNode) phi).addIncoming(newPhi,preHeader);
        }

        for (var use = header.getUse_head(); use != null;) {
            var tmp= use.getNext();
            User brInst = use.getUser();
            if (brInst instanceof BranchInst && ((BranchInst) brInst).getParent()!=preHeader && !loop.contains(((BranchInst) brInst).getParent())) {
                use.setValue(preHeader);
            }
            use=tmp;
        }
        return preHeader;

    }
    private void addBackedgeBB(Loop loop){
        ArrayList<Use> backedge=new ArrayList<>();
        var header=loop.header;
        for (var use=header.getUse_head();use!=null;use=use.getNext()) {
            var brInst=use.getUser();
            if(brInst instanceof BranchInst){
                if (loop.contains(((BranchInst) brInst).getParent())) {
                    backedge.add(use);
                }
            }
        }
        if (backedge.size() == 1) {
            return;
        }
        var backedgeBB=function.addBB("backedge");
        backedgeBB.addInst(new BranchInst(header, null,null));
        for (var phi = header.getHead(); phi instanceof PhiNode; phi = phi.getNext()) {
            var newPhi = new PhiNode("phi", phi.getType());
            backedgeBB.addInstToFirst(newPhi);
            for (var pred : header.getPredecessors()) {
                if (loop.contains(pred)) {
                    var val = ((PhiNode) phi).findValue(pred);
                    ((PhiNode) phi).removeIncoming(pred);
                    newPhi.addIncoming(val,pred);
                }
            }
            ((PhiNode) phi).addIncoming(newPhi,backedgeBB);
        }
        for (var use : backedge) {
            use.setValue(backedgeBB);
        }
        loop.basicBlocks.add(backedgeBB);
        loopMap.put(backedgeBB,loop);
    }
    private void addBackedgeBB(){
        for (var loop : topLoops) {
            recursiveAddBackedgeBB(loop);
        }
    }
    private void recursiveAddBackedgeBB(Loop loop){
        for (var subLoop : loop.subLoops) {
            recursiveAddBackedgeBB(subLoop);
        }
        addBackedgeBB(loop);
    }
}
