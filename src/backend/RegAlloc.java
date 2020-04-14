package backend;

import Riscv.*;
import optim.LoopAnalysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Stack;

public class RegAlloc {
    private static class Edge{
        VirtualRegister from;
        VirtualRegister to;

        public Edge(VirtualRegister from, VirtualRegister to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object obj) {
            assert obj instanceof Edge;
            return this.from==((Edge) obj).from && this.to==((Edge) obj).to;
        }

        @Override
        public int hashCode() {
            return from.toString().hashCode()^to.toString().hashCode();
        }

        @Override
        public String toString() {
            return "Edge{" +
                    "from=" + from +
                    ", to=" + to +
                    '}';
        }
    }
    private void debug(String message){
        //System.out.println(message);
    }
    private MachineFunction function;
    private LoopAnalysis loopAnalysis;
    private HashSet<VirtualRegister> precoloured=new HashSet<>();
    private HashSet<VirtualRegister> initial=new HashSet<>();
    private HashSet<VirtualRegister> simplifyWorklist=new HashSet<>();
    private HashSet<VirtualRegister> freezeWorklist=new HashSet<>();
    private HashSet<VirtualRegister> spillWorklist=new HashSet<>();
    private HashSet<VirtualRegister> spilledNodes=new HashSet<>();
    private HashSet<VirtualRegister> coalescedNodes=new HashSet<>();
    private HashSet<VirtualRegister> coloredNodes=new HashSet<>();
    private Stack<VirtualRegister> selectStack=new Stack<>();

    private HashSet<Move> coalescedMoves=new HashSet<>();
    private HashSet<Move> constrainedMoves=new HashSet<>();
    private HashSet<Move> frozenMoves=new HashSet<>();
    private HashSet<Move> worklistMoves=new HashSet<>();
    private HashSet<Move> activeMoves=new HashSet<>();

    private HashSet<Edge> adjSet=new HashSet<>();
    private final int K= TargetInfo.AllocableRegister.length;
    public RegAlloc(MachineFunction function,LoopAnalysis loopAnalysis){
        this.function=function;
        this.loopAnalysis=loopAnalysis;
    }
    private void removeRedundantMove(){
        for (var bb : function.getBasicBlocks()) {
            for (var inst = bb.getHead(); inst != null;) {
                var tmp=inst.getNext();
                if (inst instanceof Move) {
                    if (((Move) inst).getRd() .equals (((Move) inst).getRs())) {
                        inst.delete();
                    }
                }
                inst=tmp;
            }
        }
    }
    private void color(){
        for (var bb : function.getBasicBlocks()) {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                inst.color();
            }
        }
    }
    public void alloc(){
        run();
        color();
        removeRedundantMove();
        modifySP();
    }
    private void modifySP(){
        if(function.getStackSize()!=0) {
            int stackSize=function.getRealStackSize();
            function.getBasicBlocks().getFirst().getHead().addInstBefore(new I_Type(I_Type.Opcode.addi, new PhysicalRegister("sp"), new PhysicalRegister("sp"), new Imm(-stackSize)));
            for (var bb : function.getBasicBlocks()) {
                if (bb.getTail() instanceof Return) {
                    bb.getTail().addInstBefore(new I_Type(I_Type.Opcode.addi, new PhysicalRegister("sp"), new PhysicalRegister("sp"), new Imm(stackSize)));
                }
            }
        }
    }
    private void init(){
        precoloured.clear();
        initial.clear();
        simplifyWorklist.clear();
        freezeWorklist.clear();
        spillWorklist.clear();
        spilledNodes.clear();
        coalescedNodes.clear();
        coloredNodes.clear();
        selectStack.clear();
        coalescedMoves.clear();
        constrainedMoves.clear();
        frozenMoves.clear();
        worklistMoves.clear();
        activeMoves.clear();
        adjSet.clear();

        precoloured.addAll(TargetInfo.vPhysicalReg.values());
        for (var bb : function.getBasicBlocks()) {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                initial.addAll(inst.getDef());
                initial.addAll(inst.getUse());
            }
        }
        initial.removeAll(precoloured);
        for (var vreg : initial) {
            vreg.setAlias(null);
            vreg.degree=0;
            vreg.getAdjList().clear();
            vreg.getMoveList().clear();
            vreg.setColor(null);
            vreg.spillCost=0;
        }
        for (var precolor : precoloured) {
            precolor.degree=100000000;
        }
        calculateSpillCost();
    }
    private void run(){
        init();
        var livenessAnalysis=new LivenessAnalysis(function);
        livenessAnalysis.run();
        build();
        makeWorklist();
        do {
            if (!simplifyWorklist.isEmpty()) {
                simplify();
            }else if (!worklistMoves.isEmpty()) {
                coalesce();
            }else if (!freezeWorklist.isEmpty()) {
                freeze();
            }else if (!spillWorklist.isEmpty()) {
                selectSpill();
            }
        } while (!simplifyWorklist.isEmpty() || !worklistMoves.isEmpty() || !freezeWorklist.isEmpty() || !spillWorklist.isEmpty());
        assignColors();
        if (!spilledNodes.isEmpty()) {
            rewriteProgram();
            run();
        }

    }

    private void build(){
        for (var bb : function.getBasicBlocks()) {
            var live=new HashSet<>(bb.liveOut);
            for (var inst = bb.getTail(); inst != null; inst = inst.getPrev()) {
                if (inst instanceof Move) {
                    live.removeAll(inst.getUse());
                    for (var node : inst.getDef()) {
                        node.getMoveList().add((Move) inst);
                    }
                    for (var node : inst.getUse()) {
                        node.getMoveList().add((Move) inst);
                    }
                    worklistMoves.add((Move) inst);
                }
                live.addAll(inst.getDef());
                for (var d : inst.getDef()) {
                    for (var l : live) {
                        addEdge(l,d);
                    }
                }
                live.removeAll(inst.getDef());
                live.addAll(inst.getUse());
            }
        }
    }
    private void addEdge(VirtualRegister u,VirtualRegister v){
        var zero=TargetInfo.vPhysicalReg.get("zero");
        if (u != v && !adjSet.contains(new Edge(u, v)) && u!=zero && v!=zero) {
            adjSet.add(new Edge(u,v));
            adjSet.add(new Edge(v,u));
            if (!precoloured.contains(u)) {
                u.getAdjList().add(v);
                u.degree++;
            }
            if (!precoloured.contains(v)) {
                v.getAdjList().add(u);
                v.degree++;
            }
        }
    }
    private void makeWorklist(){
        for (var n : initial) {
            if (n.degree >= K) {
                spillWorklist.add(n);
            } else if (moveRelated(n)) {
                freezeWorklist.add(n);
            } else {
                simplifyWorklist.add(n);
            }
        }
    }
    private HashSet<VirtualRegister> adjacent(VirtualRegister n){
        var set= new HashSet<>(n.getAdjList());
        set.removeAll(selectStack);
        set.removeAll(coalescedNodes);
        return set;
    }

    private HashSet<Move> NodeMoves(VirtualRegister n) {
        var set=new HashSet<Move>();
        for (var mov : n.getMoveList()) {
            if (activeMoves.contains(mov) || worklistMoves.contains(mov)) {
                set.add(mov);
            }
        }
        return set;
    }
    private boolean moveRelated(VirtualRegister n){
        return !NodeMoves(n).isEmpty();
    }

    private void simplify(){
        var n=simplifyWorklist.iterator().next();
        simplifyWorklist.remove(n);
        selectStack.push(n);
        for (var m : adjacent(n)) {
            decrementDegree(m);
        }
    }
    private void decrementDegree(VirtualRegister m){
        int d=m.degree;
        m.degree--;
        if (d == K) {
            var tmp=new HashSet<>(adjacent(m));
            tmp.add(m);
            enableMoves(tmp);
            spillWorklist.remove(m);
            if (moveRelated(m)) {
                freezeWorklist.add(m);
            } else {
                simplifyWorklist.add(m);
            }
        }
    }

    private void enableMoves(HashSet<VirtualRegister> nodes) {
        for (var n : nodes) {
            for (var m : NodeMoves(n)) {
                if (activeMoves.contains(m)) {
                    activeMoves.remove(m);
                    worklistMoves.add(m);
                }
            }
        }
    }
    private void coalesce(){
        var m=worklistMoves.iterator().next();
        var x=getAlias((VirtualRegister) m.getRd());
        var y=getAlias((VirtualRegister) m.getRs());
        VirtualRegister u,v;
        if (precoloured.contains(y)) {
            u = y;
            v = x;
        } else {
            u=x;
            v=y;
        }
        worklistMoves.remove(m);
        if (u == v) {
            coalescedMoves.add(m);
            addWorkList(u);
        } else if (precoloured.contains(v) || adjSet.contains(new Edge(u, v))) {
            constrainedMoves.add(m);
            addWorkList(u);
            addWorkList(v);
        }else{
            var precolouredHeuristic=true;
            for (var t : adjacent(v)) {
                precolouredHeuristic&=OK(t,u);
            }
            var adjacents=new HashSet<>(adjacent(u));
            adjacents.addAll(adjacent(v));
            var conservativeHeuristic=conservative(adjacents);
            if ((precoloured.contains(u) && precolouredHeuristic) || (!precoloured.contains(u) && conservativeHeuristic)) {
                coalescedMoves.add(m);
                combine(u, v);
                addWorkList(u);
            } else {
                activeMoves.add(m);
            }
        }
    }
    private boolean OK(VirtualRegister t, VirtualRegister r) {
        return t.degree<K || precoloured.contains(t) || adjSet.contains(new Edge(t,r));
    }
    private void addWorkList(VirtualRegister u) {
        if (!precoloured.contains(u) && !moveRelated(u) && u.degree < K) {
            freezeWorklist.remove(u);
            simplifyWorklist.add(u);
        }
    }
    private boolean conservative(HashSet<VirtualRegister> nodes){
        int k=0;
        for (var n : nodes) {
            if (n.degree >= K) {
                k++;
            }
        }
        return k<K;
    }
    private VirtualRegister getAlias(VirtualRegister n) {
        if (coalescedNodes.contains(n)) {
            var alias=getAlias(n.getAlias());
            n.setAlias(alias);
            return alias;
        } else {
            return n;
        }
    }
    private void combine(VirtualRegister u,VirtualRegister v){
        if (freezeWorklist.contains(v)) {
            freezeWorklist.remove(v);
        } else {
            spillWorklist.remove(v);
        }
        coalescedNodes.add(v);
        v.setAlias(u);
        u.getMoveList().addAll(v.getMoveList());
        var v_Set=new HashSet<VirtualRegister>();
        v_Set.add(v);
        enableMoves(v_Set);
        for (var t : adjacent(v)) {
            addEdge(t,u);
            decrementDegree(t);
        }
        if (u.degree >= K && freezeWorklist.contains(u)) {
            freezeWorklist.remove(u);
            spillWorklist.add(u);
        }
    }
    private void freeze(){
        var u=freezeWorklist.iterator().next();
        freezeWorklist.remove(u);
        simplifyWorklist.add(u);
        freezeMoves(u);
    }
    private void freezeMoves(VirtualRegister u){
        VirtualRegister v;
        for (var m : NodeMoves(u)) {
            var x=m.getRd();
            var y=m.getRs();
            if (getAlias((VirtualRegister) y) == getAlias(u)) {
                v = getAlias((VirtualRegister) x);
            } else {
                v=getAlias((VirtualRegister) y);
            }
            activeMoves.remove(m);
            frozenMoves.add(m);
            if (NodeMoves(v).isEmpty() && v.degree < K) {
                freezeWorklist.remove(v);
                simplifyWorklist.add(v);
            }
        }
    }
    private void selectSpill(){
        var m=spillHeuristic();
        spillWorklist.remove(m);
        simplifyWorklist.add(m);
        freezeMoves(m);
    }
    private VirtualRegister spillHeuristic(){
        VirtualRegister min = null;
        var iter=spillWorklist.iterator();
        while(iter.hasNext()){
            min=iter.next();
            if (!min.spillTemporary) {
                break;
            }
        }
        while (iter.hasNext()) {
            var vreg=iter.next();
            if (!vreg.spillTemporary && vreg.getRealSpillCost() < min.getRealSpillCost()) {
                min=vreg;
            }
        }
        return min;
    }
    private void assignColors(){
        while (!selectStack.empty()) {
            var n=selectStack.pop();
            var okColors=new HashSet<PhysicalRegister>();
            for (var name : TargetInfo.AllocableRegister) {
                okColors.add(new PhysicalRegister(name));
            }
            for (var w : n.getAdjList()) {
                if (coloredNodes.contains(getAlias(w)) || precoloured.contains(getAlias(w))) {
                    okColors.remove(getAlias(w).getColor());
                }
            }
            if (okColors.isEmpty()) {
                spilledNodes.add(n);
            } else {
                coloredNodes.add(n);
                //assign caller-saved register first
                HashSet<PhysicalRegister> callerSavedReg=new HashSet<>();
                for (var colorLeft : okColors) {
                    if (TargetInfo.callerSavedRegister.contains(colorLeft.getRegName())) {
                        callerSavedReg.add(colorLeft);
                    }
                }
                if (callerSavedReg.isEmpty()) {
                    n.setColor(okColors.iterator().next());
                } else {
                    n.setColor(callerSavedReg.iterator().next());
                }
            }
        }
        for (var n : coalescedNodes) {
            n.setColor(getAlias(n).getColor());
        }
    }
    private void rewriteProgram(){
        debug("spilled: "+spilledNodes);
        Rewriter rewriter=new Rewriter(function);
        rewriter.run();
    }
    private class Rewriter implements Visitor {
        private HashMap<VirtualRegister,StackLocation> spillAddr=new HashMap<>();
        private MachineFunction function;
        public Rewriter(MachineFunction function){
            this.function=function;
        }
        public void run(){
            visitFunction(function);
        }
        private boolean canSpill(MachineOperand register){
            return register instanceof VirtualRegister && spilledNodes.contains(register);
        }
        private StackLocation getAddr(VirtualRegister vreg){
            if (canSpill(vreg)) {
                if (spillAddr.containsKey(vreg)) {
                    return spillAddr.get(vreg);
                } else {
                    var newLoc = new StackLocation(function);
                    spillAddr.put(vreg, newLoc);
                    return newLoc;
                }
            } else {
                return null;
            }
        }
        private VirtualRegister spillUse(MachineInstruction inst,VirtualRegister vreg){
            var tmp=new VirtualRegister("spill_use");
            tmp.spillTemporary=true;
            inst.addInstBefore(new Load(getAddr(vreg),tmp));
            return tmp;
        }

        private void spillDef(MachineInstruction inst, VirtualRegister vreg) {
            inst.addInstAfter(new Store(getAddr(vreg),vreg,4,null));
        }
        @Override
        public void visitBranch(Branch inst) {
            if (canSpill(inst.getRs())) {
                var tmp = spillUse(inst, (VirtualRegister) inst.getRs());
                inst.setRs(tmp);
            }
            if (canSpill(inst.getRt())) {
                var tmp= spillUse(inst, (VirtualRegister) inst.getRt());
                inst.setRt(tmp);
            }
        }
        @Override
        public void visitCall(Call inst) {

        }

        @Override
        public void visitGlobalVar(GlobalVar gvar) {

        }

        @Override
        public void visitI_type(I_Type inst) {
            if (canSpill(inst.getRs1())) {
                var tmp=spillUse(inst, (VirtualRegister) inst.getRs1());
                inst.setRs1(tmp);
            }
            if (canSpill(inst.getRd())) {
                spillDef(inst, (VirtualRegister) inst.getRd());
            }
        }

        @Override
        public void visitJump(Jump inst) {

        }

        @Override
        public void visitLI(LI inst) {
            if (canSpill(inst.getRd())) {
                spillDef(inst, (VirtualRegister) inst.getRd());
            }
        }

        @Override
        public void visitLA(LA inst) {
            if (canSpill(inst.getRd())) {
                spillDef(inst, (VirtualRegister) inst.getRd());
            }
        }

        @Override
        public void visitLoad(Load inst) {
            if (canSpill(inst.getSrc())) {
                var tmp=spillUse(inst, (VirtualRegister) inst.getSrc());
                inst.setSrc(tmp);
            }
            if (canSpill(inst.getRd())) {
                spillDef(inst, (VirtualRegister) inst.getRd());
            }
        }

        @Override
        public void visitBB(MachineBasicBlock bb) {
            for (var inst = bb.getHead(); inst != null; ) {
                var tmp=inst.getNext();
                visit(inst);
                inst=tmp;
            }
        }

        @Override
        public void visitFunction(MachineFunction function) {
            for (var bb : function.getBasicBlocks()) {
                visitBB(bb);
            }
        }

        @Override
        public void visitModule(MachineModule module) {
            for (var function : module.getFunctions()) {
                visitFunction(function);
            }
        }

        @Override
        public void visitMove(Move inst) {
            if (canSpill(inst.getRs())) {
                var tmp=spillUse(inst, (VirtualRegister) inst.getRs());
                inst.setRs(tmp);
            }
            if (canSpill(inst.getRd())) {
                spillDef(inst, (VirtualRegister) inst.getRd());
            }
        }

        @Override
        public void visitReturn(Return inst) {

        }

        @Override
        public void visitR_type(R_Type inst) {
            if (canSpill(inst.getRs1())) {
                var tmp = spillUse(inst, (VirtualRegister) inst.getRs1());
                inst.setRs1(tmp);
            }
            if (canSpill(inst.getRs2())) {
                var tmp = spillUse(inst, (VirtualRegister) inst.getRs2());
                inst.setRs2(tmp);
            }
            if (canSpill(inst.getRd())) {
                spillDef(inst, (VirtualRegister) inst.getRd());
            }
        }

        @Override
        public void visitStore(Store inst) {
            if (canSpill(inst.getPtr())) {
                var tmp=spillUse(inst, (VirtualRegister) inst.getPtr());
                inst.setPtr(tmp);
            }
            if (canSpill(inst.getSrc())) {
                var tmp = spillUse(inst, (VirtualRegister) inst.getSrc());
                inst.setSrc(tmp);
            }
        }

        @Override
        public void visitLUI(LUI inst) {
            if (canSpill(inst.getRt())) {
                spillDef(inst, (VirtualRegister) inst.getRt());
            }
        }

        @Override
        public void visit(MachineInstruction inst) {
            inst.accept(this);
        }
    }
    private void calculateSpillCost(){
        for (var bb : function.getBasicBlocks()) {
            int depth=loopAnalysis.getLoopDepth(bb.getIRBasicBlock());
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                for (var use : inst.getUse()) {
                    use.spillCost+=Math.pow(10,depth);
                }
                for (var def : inst.getDef()) {
                    def.spillCost+=Math.pow(10,depth);
                }
            }
        }
    }
}
