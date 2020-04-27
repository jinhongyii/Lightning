package optim;


import IR.*;
import IR.instructions.BranchInst;
import IR.instructions.PhiNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class LoopAnalysis extends FunctionPass {
    public static class Loop{
        Loop parent;
        ArrayList<Loop> subLoops;
        HashSet<BasicBlock> basicBlocks;
        HashSet<BasicBlock> exitBlocks;
        BasicBlock header;

        public HashSet<BasicBlock> getExitBlocks() {
            return exitBlocks;
        }

        public Loop(BasicBlock head) {
            this.subLoops = new ArrayList<>();
            this.basicBlocks=new HashSet<>();
            exitBlocks=new HashSet<>();
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
    AliasAnalysis aa;
    public LoopAnalysis(Function function,DominatorAnalysis dominatorAnalysis,AliasAnalysis aa) {
        super(function);
        this.dominatorAnalysis=dominatorAnalysis;
        this.aa=aa;
    }

    @Override
    public boolean run() {
        topLoops.clear();
        loopMap.clear();
        dfs(dominatorAnalysis.treeRoot);
        addPreHeader();
        addBackedgeBB();
        rewriteExitBlock();
        cleanUp();
        updateLoopStatus();
        return false;
    }
    private void updateLoopStatus(){
        for (var bb = function.getHead(); bb != null; bb = bb.getNext()) {
            if (!loopMap.containsKey(bb)) {
                bb.setNotInLoop(true);
            }
        }
    }
    public void runWithoutModify(){
        topLoops.clear();
        loopMap.clear();
        dfs(dominatorAnalysis.treeRoot);
    }
    private void rewriteExitBlock(){
        for (var loop : topLoops) {
            recursiveRewriteExitBlock(loop);
        }
    }
    private void recursiveRewriteExitBlock(Loop loop){
        for (var subLoop : loop.subLoops) {
            recursiveRewriteExitBlock(subLoop);
        }
        var headerNode=dominatorAnalysis.DominantTree.get(loop.header);
        var exitBlocks=new HashSet<>(loop.getExitBlocks());
        for (var exitBlock : exitBlocks) {
            var exitNode=dominatorAnalysis.DominantTree.get(exitBlock);
            if (!headerNode.dominate(exitNode)) {
                rewriteExitBlock(loop,exitBlock);
            }
        }
    }
    private void rewriteExitBlock(Loop loop,BasicBlock exitBlock){
        ArrayList<BasicBlock> inLoopPred=new ArrayList<>();
        for (var pred : exitBlock.getPredecessors()) {
            if (loop.contains(pred)) {
                inLoopPred.add(pred);
            }
        }
        if(inLoopPred.isEmpty()){
            return;
        }
        var newBB=splitBlock(exitBlock,inLoopPred);
        if (loop.parent != null) {
            loopMap.put(newBB,loop.parent);
            var l=loop.parent;
            while (l != null) {
                l.basicBlocks.add(newBB);
                l=l.parent;
            }
        }
        changeExitBlock(loop,exitBlock,newBB);
    }
    private void changeExitBlock(Loop loop,BasicBlock predExit, BasicBlock newExit){
        if (loop.exitBlocks.contains(predExit)) {
            loop.exitBlocks.remove(predExit);
            loop.exitBlocks.add(newExit);
        }
        for (var subLoop : loop.subLoops) {
            changeExitBlock(subLoop,predExit,newExit);
        }
    }

    private BasicBlock splitBlock(BasicBlock basicBlock,ArrayList<BasicBlock> preds) {
        var newBB=function.addBB("loop.exit");
        newBB.addInst(new BranchInst(basicBlock,null,null));
        for (var phi = basicBlock.getHead(); phi instanceof PhiNode; phi = phi.getNext()) {
            var newPhi = new PhiNode("phi", phi.getType());
            newBB.addInstBefore(newBB.getTerminator(),newPhi);
            for (var pred : preds) {
                newPhi.addIncoming(((PhiNode) phi).findValue(pred),pred);
                ((PhiNode) phi).removeIncoming(pred);
            }
            ((PhiNode) phi).addIncoming(newPhi,newBB);
        }
        for (var pred : preds) {
            var term=(BranchInst)pred.getTerminator();
            if (term.isConditional()) {
                term.setConditional(newBB, basicBlock);
            } else {
                term.setUnconditional(newBB);
            }
        }
        return newBB;
    }
    private void cleanUp(){
        ADCE adce=new ADCE(function,dominatorAnalysis,aa);
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
        for (var basicblock : loop.basicBlocks) {
            for (var suc : basicblock.getSuccessors()) {
                if (!loop.basicBlocks.contains(suc)) {
                    loop.exitBlocks.add(suc);
                }
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
        changeExitBlock(loop,header,backedgeBB);
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
    public int getLoopDepth(BasicBlock basicBlock){
        var deepLoop=loopMap.get(basicBlock);
        int cnt=0;
        while (deepLoop!= null) {
            deepLoop=deepLoop.parent;
            cnt++;
        }
        return cnt;
    }
    public int getTotalLoopDepth(){
        int depth=0;
        for (var loop : topLoops) {
            depth=Integer.max(depth,getTotalLoopDepth(loop,0));
        }
        return depth;
    }
    private int getTotalLoopDepth(Loop curLoop,int depth){
        if (curLoop.subLoops.isEmpty()) {
            return depth;
        }
        int maxDepth=-1;
        for (var subLoop : curLoop.subLoops) {
            maxDepth=Integer.max(maxDepth,getTotalLoopDepth(subLoop,depth+1));
        }
        return maxDepth;
    }
}
