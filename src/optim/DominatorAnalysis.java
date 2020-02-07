package optim;

import IR.BasicBlock;
import IR.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DominatorAnalysis extends FunctionPass {
    public Node treeRoot;
    public HashMap<BasicBlock,Node> DominantTree=new HashMap<>();
    public HashMap<BasicBlock,HashSet<BasicBlock>> DominatorFrontier=new HashMap<>();
    public Node postTreeRoot;
    public HashMap<BasicBlock,Node> postDomTree=new HashMap<>();
    public HashMap<BasicBlock,HashSet<BasicBlock>> postDomFrontier=new HashMap<>();
    private BasicBlock[] vertex;
    private BasicBlock[] semi;
    private BasicBlock[] ancestor;
    private BasicBlock[] idom;
    private BasicBlock[] parent;
    private BasicBlock[] samedom;
    private BasicBlock[] best;
    private HashSet<BasicBlock>[] bucket;
    private int cnt=0;
    private void dfs(BasicBlock p, BasicBlock n){
        if (n.dfsnum == 0) {
            cnt++;
            n.dfsnum=cnt;
            vertex[cnt]=n;
            parent[n.dfsnum]=p;
            for (var suc : n.getSuccessors()) {
                dfs(n,suc);
            }
        }
    }
    private void postDfs(BasicBlock p,BasicBlock n){
        if (n.dfsnum == 0) {
            cnt++;
            n.dfsnum=cnt;
            vertex[cnt]=n;
            parent[n.dfsnum]=p;
            for (var pred : n.getPredecessors()) {
                postDfs(n,pred);
            }
        }
    }
    private void link(BasicBlock p, BasicBlock n){
        ancestor[n.dfsnum]=p;
        best[n.dfsnum]=n;
    }
    private BasicBlock ancestorWithLowestSemi(BasicBlock v){
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
    private void getPostIdoms(BasicBlock root){
        postDfs(null,root);
        for (int i = cnt; i >= 2; i--) {
            var n=vertex[i];
            var par=parent[n.dfsnum];
            var min=par;//parent on the tree is certainly a candidate
            for (var pred : n.getSuccessors() ) {
                if(pred.dfsnum!=0) {
                    BasicBlock candidate;
                    if (pred.dfsnum <= n.dfsnum) {
                        candidate = pred;
                    } else {
                        candidate = semi[ancestorWithLowestSemi(pred).dfsnum];
                    }
                    if (candidate.dfsnum < min.dfsnum) {
                        min = candidate;
                    }
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
    private void getIdoms(BasicBlock root){
        dfs(null,root);
        for (int i = cnt; i >= 2; i--) {
            var n=vertex[i];
            var par=parent[n.dfsnum];
            var min=par;//parent on the tree is certainly a candidate
            for (var pred : n.getPredecessors() ) {
                if(pred.dfsnum!=0) {
                    BasicBlock candidate;
                    if (pred.dfsnum <= n.dfsnum) {
                        candidate = pred;
                    } else {
                        candidate = semi[ancestorWithLowestSemi(pred).dfsnum];
                    }
                    if (candidate.dfsnum < min.dfsnum) {
                        min = candidate;
                    }
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
            var tmp=other;
            while (tmp != null && tmp != this) {
                tmp=tmp.idom;
            }
            return tmp!=null;
//            while (other.idom != null && other.idom!=this) {
//                other=other.idom;
//            }
//            return other.idom!=null;
        }
    }
    private Node buildDominantTree(){

        for (int i = 1; i <= cnt; i++) {
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
    private Node buildPostDomTree(){
        for (int i = 1; i <= cnt; i++) {
            //use dfs order
            if (i == 1) {
                postDomTree.put(vertex[i],new Node(vertex[i], null) );
            } else {
                postDomTree.put(vertex[i],new Node(vertex[i], postDomTree.get(idom[i])));
                postDomTree.get(idom[i]).addChildren(postDomTree.get(vertex[i]));
            }
        }
        return postDomTree.get(vertex[1]);
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
    private HashSet<BasicBlock> buildPostDomFrontierHelper(Node node){
        BasicBlock basicBlock=node.basicBlock;
        var df=new HashSet<BasicBlock>();
        postDomFrontier.put(basicBlock,df);
        for (var suc : basicBlock.getPredecessors()) {
            if (idom[suc.dfsnum] != basicBlock) {
                df.add(suc);
            }
        }
        for (var child : node.children) {
            var childdf=buildPostDomFrontierHelper(child);
            for (var cdfEle : childdf) {
                Node tmp=postDomTree.get(cdfEle);
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
    private void BuildPostDomFrontier(){
        buildPostDomFrontierHelper(postTreeRoot);
    }
    public boolean run(){
        treeRoot=null;
        DominantTree.clear();
        DominatorFrontier.clear();
        cnt=0;
        int bbnum=0;
        for (var i = function.getHead(); i != null; i=i.getNext()) {
            bbnum++;
            i.dfsnum=0;
        }
        initializeArrays(bbnum);
        getIdoms(function.getEntryBB());
        treeRoot=buildDominantTree();
        BuildDominantFrontier();
        cnt=0;
        for (var i = function.getHead(); i != null; i=i.getNext()) {
            i.dfsnum=0;
        }
        initializeArrays(bbnum);
        getPostIdoms(function.getLastBB());
        postTreeRoot=buildPostDomTree();
        BuildPostDomFrontier();
        return false;
    }

    public void initializeArrays(int bbnum) {
        vertex = new BasicBlock[bbnum + 1];
        semi = new BasicBlock[bbnum + 1];
        ancestor = new BasicBlock[bbnum + 1];
        idom = new BasicBlock[bbnum + 1];
        samedom = new BasicBlock[bbnum + 1];
        parent = new BasicBlock[bbnum + 1];
        best = new BasicBlock[bbnum + 1];
        bucket = new HashSet[bbnum + 1];
        for (int i = 1; i < bbnum + 1; i++) {
            bucket[i] = new HashSet<>();
        }
    }

    public DominatorAnalysis(Function function){
        super(function);
    }
}
