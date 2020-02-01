package optim;

import IR.BasicBlock;
import IR.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DominatorAnalysis implements FunctionPass {
    public Node treeRoot;
    public HashMap<BasicBlock,Node> DominantTree;
    public HashMap<BasicBlock,HashSet<BasicBlock>> DominatorFrontier;
    private BasicBlock[] vertex;
    private BasicBlock[] semi;
    private BasicBlock[] ancestor;
    private BasicBlock[] idom;
    private BasicBlock[] parent;
    private BasicBlock[] samedom;
    private BasicBlock[] best;
    private HashSet<BasicBlock>[] bucket;
    private int cnt=1;
    private void dfs(BasicBlock p, BasicBlock n){
        if (n.dfsnum == 0) {
            n.dfsnum=cnt;
            vertex[cnt]=n;
            parent[n.dfsnum]=p;
            cnt++;
            for (var suc : n.getSuccessors()) {
                dfs(n,suc);
            }
        }
    }

    public void link(BasicBlock p,BasicBlock n){
        ancestor[n.dfsnum]=p;
        best[n.dfsnum]=n;
    }
    public BasicBlock ancestorWithLowestSemi(BasicBlock v){
        var a=ancestor[v.dfsnum];
        if (ancestor[a.dfsnum] != null) {
            var b=ancestorWithLowestSemi(a);
            ancestor[v.dfsnum]=ancestor[a.dfsnum];
            if (semi[b.dfsnum].dfsnum < semi[best[v.dfsnum].dfsnum].dfsnum) {
                best[v.dfsnum]=b;
            }
        }
        return best[v.dfsnum];
    }
    public void getIdoms(BasicBlock root){
        dfs(null,root);
        for (int i = cnt; i >= 2; i--) {
            var n=vertex[i];
            var par=parent[n.dfsnum];
            var min=par;//parent on the tree is certainly a candidate
            for (var pred : n.getPredecessors()) {
                BasicBlock candidate;
                if (pred.dfsnum <= n.dfsnum) {
                    candidate = pred;
                } else {
                    candidate=semi[ancestorWithLowestSemi(pred).dfsnum];
                }
                if (candidate.dfsnum < min.dfsnum) {
                    min=candidate;
                }
            }
            semi[n.dfsnum]=min;
            bucket[min.dfsnum].add(n);//delay calc
            link(par,n);
            for (var v : bucket[par.dfsnum]) {
                var y=ancestorWithLowestSemi(v);
                if (semi[y.dfsnum] == semi[v.dfsnum]) {
                    idom[v.dfsnum] = par;
                } else {
                    samedom[v.dfsnum]=y;//idom[y] has not been calculated
                }
            }
            bucket[par.dfsnum].clear();
        }
        for (int i = 2; i <= cnt; i++) {
            var n=vertex[i];
            if (samedom[n.dfsnum] != null) {
                idom[n.dfsnum]=idom[samedom[n.dfsnum].dfsnum];
            }
        }
    }
    static class Node {
        BasicBlock basicBlock;
        Node idom;
        ArrayList<Node> children=new ArrayList<>();

        public Node(BasicBlock basicBlock, Node idom) {
            this.basicBlock = basicBlock;
            this.idom = idom;
        }
        public void addChildren(Node child){
            children.add(child);
        }
        public boolean dominate(Node other){

            while (other.idom != null && other.idom!=this) {
                other=other.idom;
            }
            return other.idom==null;
        }
    }
    private Node buildDominantTree(int bbnum){

        for (int i = 1; i <= bbnum; i++) {
            //use dfs order
            if (i == 1) {
                DominantTree.put(vertex[i],new Node(vertex[i], null) );
            } else {
                DominantTree.put(vertex[i],new Node(vertex[i], DominantTree.get(idom[i])));
                DominantTree.get(idom[i]).addChildren(DominantTree.get(vertex[i]));
            }
        }
        return DominantTree.get(vertex[1]);
    }
    private HashSet<BasicBlock> buildDominantFrontierHelper(Node node){
        BasicBlock basicBlock=node.basicBlock;
        var df=new HashSet<BasicBlock>();
        DominatorFrontier.put(basicBlock,df);
        for (var suc : basicBlock.getSuccessors()) {
            if (idom[suc.dfsnum] != basicBlock) {
                df.add(suc);
            }
        }
        for (var child : node.children) {
            var childdf=buildDominantFrontierHelper(child);
            for (var cdfEle : childdf) {
                Node tmp=DominantTree.get(cdfEle);
                if (!node.dominate(tmp)|| tmp==node ) {
                    df.add(cdfEle);
                }
            }
        }
        return df;
    }
    private void BuildDominantFrontier(){
        buildDominantFrontierHelper(treeRoot);
    }
    public void runOnFunction(Function function){
        int bbnum=0;
        for (var i = function.getHead(); i != null; i=i.getNext()) {
            bbnum++;
        }
        vertex=new BasicBlock[bbnum+1];
        semi=new BasicBlock[bbnum+1];
        ancestor=new BasicBlock[bbnum+1];
        idom=new BasicBlock[bbnum+1];
        samedom=new BasicBlock[bbnum+1];
        parent=new BasicBlock[bbnum+1];
        best=new BasicBlock[bbnum+1];
        bucket=new HashSet[bbnum+1];
        for(int i=1;i<bbnum+1;i++){
            bucket[i]=new HashSet<>();
        }
        getIdoms(function.getEntryBB());
        treeRoot=buildDominantTree(bbnum);
        BuildDominantFrontier();
    }

}
