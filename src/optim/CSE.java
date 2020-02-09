package optim;

import IR.Function;
import IR.Instruction;
import IR.Module;
import IR.instructions.BinaryOpInst;
import IR.instructions.CastInst;
import IR.instructions.GetElementPtrInst;
import IR.instructions.IcmpInst;

import java.util.ArrayList;
import java.util.HashMap;

public class CSE extends FunctionPass {
    private DominatorAnalysis dominatorAnalysis;
    private HashMap<Expr,Instruction> subexprMap=new HashMap<>();
    public CSE(Function function,DominatorAnalysis dominatorAnalysis) {
        super(function);
        this.dominatorAnalysis=dominatorAnalysis;
    }

    @Override
    public boolean run() {
        subexprMap.clear();
        return CommonSubExpressionElimination();
    }
    public static void runOnModule(Module module,DominatorAnalysis dominatorAnalysis){
        for (var func : module.getFunctionList()) {
            if (!func.isExternalLinkage()) {
                var cse=new CSE(func,dominatorAnalysis);
                cse.run();
            }
        }
    }
    private static class Expr{
        String op;
        ArrayList<String> operand;

        public Expr(String op, ArrayList<String> operand) {
            this.op = op;
            this.operand = operand;
        }

        @Override
        public boolean equals(Object obj) {
            assert obj instanceof Expr;
            var expr=(Expr)obj;
            if (!expr.op.equals(op)) {
                return false;
            }
            if (expr.operand.size() != operand.size()) {
                return false;
            }
            for (int i = 0; i < operand.size(); i++) {
                if (!expr.operand.get(i).equals(operand.get(i))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashcode=op.hashCode();
            for (var str : operand) {
                hashcode^=str.hashCode();
            }
            return hashcode;
        }
    }

    private boolean isCommutative(Instruction.Opcode opcode) {
        return opcode== Instruction.Opcode.add || opcode== Instruction.Opcode.mul ||
                opcode== Instruction.Opcode.and || opcode== Instruction.Opcode.or ||
                opcode== Instruction.Opcode.xor || opcode== Instruction.Opcode.EQ ||
                opcode== Instruction.Opcode.NE;
    }
    private ArrayList<Expr> convertInstToExpr(Instruction instruction){
        ArrayList<Expr> exprs=new ArrayList<>();
        if (instruction instanceof BinaryOpInst) {
            ArrayList<String> operands=new ArrayList<>();
            ArrayList<String> operands2=new ArrayList<>();
            String op;
            switch (instruction.getOpcode()) {
                case add: op="add";break;
                case sub: op="sub" ;break;
                case div: op="sdiv";break;
                case mul: op="mul";break;
                case rem: op="srem";break;
                case and: op="and";break;
                case or: op="or";break;
                case xor: op="xor";break;
                case shl:op="shl";break;
                case shr:op="ashr";break;
                default:op=null;
            }
            if (isCommutative(instruction.getOpcode())) {
                operands2.add(((BinaryOpInst)instruction).getRhs().toString());
                operands2.add(((BinaryOpInst) instruction).getLhs().toString());
            }
            operands.add(((BinaryOpInst) instruction).getLhs().toString());
            operands.add(((BinaryOpInst)instruction).getRhs().toString());
            exprs.add(new Expr(op,operands));
            if (operands2.size() != 0) {
                exprs.add(new Expr(op,operands2));
            }
        } else if (instruction instanceof IcmpInst) {
            ArrayList<String> operands=new ArrayList<>();
            ArrayList<String> operands2=new ArrayList<>();
            String op="";
            switch (instruction.getOpcode()) {
                case EQ:op="eq";break;
                case NE:op="ne";break;
                case LT:op="slt";break;
                case LE:op="sle";break;
                case GT:op="sgt";break;
                case GE:op="sge";break;
            }
            if (isCommutative(instruction.getOpcode())) {
                operands2.add(((IcmpInst)instruction).getRhs().toString());
                operands2.add(((IcmpInst) instruction).getLhs().toString());
            }
            operands.add(((IcmpInst) instruction).getLhs().toString());
            operands.add(((IcmpInst)instruction).getRhs().toString());
            exprs.add(new Expr(op,operands));
            if (operands2.size() != 0) {
                exprs.add(new Expr(op,operands2));
            }
        } else if (instruction instanceof GetElementPtrInst) {
            String op="gep";
            ArrayList<String> operands=new ArrayList<>();
            for (var val : instruction.getOperands()) {
                operands.add(val.getVal().toString());
            }
            exprs.add(new Expr(op,operands));
        } else if (instruction instanceof CastInst) {
            String op="cast";
            ArrayList<String> operands=new ArrayList<>();
            operands.add(instruction.getType().toString());
            operands.add(instruction.getOperands().get(0).getVal().toString());
            exprs.add(new Expr(op,operands));
        }
        return exprs;
    }
    private boolean CommonSubExpressionElimination(){
        boolean changed=false;
        for (var bb = function.getHead(); bb != null; bb = bb.getNext()) {
            for (var inst = bb.getHead(); inst != null;) {
                var tmp=inst.getNext();
                var exprs= convertInstToExpr(inst);
                if (exprs.size() != 0) {
                    var prevExpr=subexprMap.get(exprs.get(0));
                    if (prevExpr!=null) {
                        var prevExprNode=dominatorAnalysis.DominantTree.get(prevExpr.getParent());
                        var thisNode=dominatorAnalysis.DominantTree.get(bb);
                        if(prevExprNode.dominate(thisNode)) {
                            inst.transferUses(prevExpr);
                            inst.delete();
                            changed=true;
                        }
                    } else {
                        for (var expr : exprs) {
                            subexprMap.put(expr, inst);
                        }
                    }
                }
                inst=tmp;
            }
        }
        return changed;
        //todo: eliminate useless gep ,cast(inst combine will do it)  and load
        // alias analysis
    }

}
