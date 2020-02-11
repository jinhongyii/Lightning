package IR;

import IR.Types.FunctionType;
import IR.Types.PointerType;
import IR.Types.StructType;
import IR.instructions.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class IRPrinter implements IRVisitor {
    private String prefix="";
    private FileWriter writer;
    BufferedWriter bufferedWriter;
    private void indent(){prefix+="\t";}
    private void dedent(){prefix=prefix.substring(0,prefix.length()-1);}
    private void print(String str){
//        System.out.println(prefix+str);
        try {
            bufferedWriter.write(prefix + str + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public IRPrinter(Module module,String filename) throws IOException {
        writer=new FileWriter("tmp/"+filename);
        bufferedWriter=new BufferedWriter(writer,8096);
        visitModule(module);
        bufferedWriter.flush();
    }
    @Override
    public Object visitModule(Module module) {
        print("target datalayout = \"e-m:e-i64:64-f80:128-n8:16:32:64-S128\"\n" +
                "target triple = \"x86_64-pc-linux-gnu\"");
        for (var i : module.structMap.entrySet()) {
            if (i.getValue() instanceof StructType) {
                print("%" + i.getKey() + " = type " + ((StructType) i.getValue()).getDetailedText());
            } else {
                print("%" + i.getKey() + " = type " + i.getValue());
            }
        }
        for (var i : module.getGlobalList()) {
            visit(i);
        }
        for (var i : module.getFunctionList()) {
            visitFunction(i);
        }
        return null;
    }

    @Override
    public Object visitFunction(Function function) {
        StringBuilder signature= new StringBuilder();
        if (function.externalLinkage) {
            signature.append("declare ");
        } else {
            signature.append("define ");
        }
        signature.append(((FunctionType) function.getType()).getResultType().toString());
        signature.append(function.toString()).append("(");
        boolean flag=false;
        for (var args:function.arguments) {
            signature.append(args.getType()).append(" ").append(args).append(",");
            flag=true;
        }
        if(flag) {
            signature.delete(signature.length() - 1, signature.length());
        }
        signature.append(")");
        if (!function.externalLinkage) {
            signature.append(" {");
            print(signature.toString());
            indent();
//            for (var bb : function.getBasicBlockList()) {
//                visitBasicBlock(bb);
//            }
            for (var bb = function.getEntryBB(); bb != null; bb = bb.next) {
                visit(bb);
            }
            dedent();
            print("}");
        } else {
            print(signature.toString());
        }
        return null;
    }

    @Override
    public Object visitBasicBlock(BasicBlock basicBlock) {
        StringBuilder builder=new StringBuilder();
        builder.append(basicBlock.getName()).append(":\t\t\t\t\t\t\t");
        if(basicBlock.getPredecessors().size()!=0){
            builder.append("; preds = ");
            for (var pred : basicBlock.getPredecessors()) {
                builder.append(pred.toString()).append(",");
            }
            builder.delete(builder.length() - 1, builder.length());
        }
        print(builder.toString());
        indent();
//        for (var inst : basicBlock.instructionList) {
//            visit(inst);
//        }
        for (var i = basicBlock.head; i != null; i = i.next) {
            visit(i);
        }
        dedent();
        return null;
    }

    @Override
    public Object visitGlobalVariable(GlobalVariable globalVariable) {
//        assert globalVariable.initializer.getValueType()== Value.ValueType.ConstantVal ;
        var eleType=((PointerType)globalVariable.getType()).getPtrType();
        if (globalVariable.initializer != null) {
            print(globalVariable.toString() + " =" + "global" + " " + eleType + " " + globalVariable.initializer.toString());
        } else {
            print(globalVariable.toString() + " =" + "global" + " " + eleType+(eleType.getId()==Type.TypeID.PointerType?" null":" 0"));
        }
        return null;
    }

    @Override
    public Object visitAllocaInst(AllocaInst allocaInst) {
        print(allocaInst.toString()+"= alloca "+((PointerType)allocaInst.getType()).getPtrType().toString());
        return null;
    }

    @Override
    public Object visitBinaryOpInst(BinaryOpInst binaryOpInst) {
        String op;
        switch (binaryOpInst.getOpcode()) {
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
        print(binaryOpInst.toString()+" = "+op+" "+binaryOpInst.getType()+" "+binaryOpInst.operands.get(0).val+", "+binaryOpInst.operands.get(1).val);
        return null;
    }

    @Override
    public Object visitBranchInst(BranchInst branchInst) {
        if (branchInst.isConditional()) {
            print("br i1 " + branchInst.operands.get(2).val + " ,label " + branchInst.operands.get(0).val + " ,label " + branchInst.operands.get(1).val);
        } else {
            print("br label "+branchInst.operands.get(0).val);
        }
        return null;
    }

    @Override
    public Object visitCallInst(CallInst callInst) {
        StringBuilder str= new StringBuilder();
        Function function= (Function) callInst.operands.get(0).val;
        if (!callInst.getType().equals(Type.theVoidType)) {
            str.append(callInst.toString()).append(" = ");
        }
        str.append("call ").append(((FunctionType) function.getType()).getResultType()).append(" ");
        str.append(function.toString()).append("(");
        boolean flag=false;
        for (int i = 1; i < callInst.operands.size(); i++) {
            var arg=callInst.operands.get(i).val;
            str.append(((FunctionType)function.getType()).getParamTypes().get(i-1)).append(" ").append(arg.toString()).append(",");
            flag=true;
        }
        if(flag) {
            str.delete(str.length() - 1, str.length());
        }
        str.append(")");
        print(str.toString());
        return null;
    }

    @Override
    public Object visitCastInst(CastInst castInst) {
        var original=castInst.operands.get(0).val;
        print(castInst.toString()+" = bitcast "+original.getType()+" "+original.toString()+" to "+castInst.getType());
        return null;
    }

    @Override
    public Object visitGEPInst(GetElementPtrInst GEPInst) {
        var ptr=GEPInst.operands.get(0).val;
        StringBuilder tmp= new StringBuilder(GEPInst.toString() + "= getelementptr " + ((PointerType)ptr.getType()).getPtrType() + " ," + ptr.getType() + " " + ptr);
        for (int i = 1; i < GEPInst.operands.size(); i++) {
            tmp.append(GEPInst.operands.get(i).val.getValueType()== Value.ValueType.ConstantVal?",i32 ":",i64 ").append(GEPInst.operands.get(i).val);
        }
        print(tmp.toString());
        return null;
    }

    @Override
    public Object visitIcmpInst(IcmpInst icmpInst) {
        String op="";
        switch (icmpInst.getOpcode()) {
            case EQ:op="eq";break;
            case NE:op="ne";break;
            case LT:op="slt";break;
            case LE:op="sle";break;
            case GT:op="sgt";break;
            case GE:op="sge";break;
        }
        var operand1=icmpInst.operands.get(0).val;
        var operand2=icmpInst.operands.get(1).val;
        print(icmpInst.toString()+" = icmp "+op+" "+(operand1.getType().isNull()?operand2.getType().isNull()?"i64*":operand2.getType():operand1.getType())+" "+operand1+","+operand2);
        return null;
    }

    @Override
    public Object visitLoadInst(LoadInst loadInst) {
        var ptr=loadInst.operands.get(0).val;
        print(loadInst.toString()+" = load "+loadInst.getType()+", "+loadInst.getType()+"* "+ptr);
        return null;
    }

    @Override
    public Object visitPhiNode(PhiNode phiNode) {
        StringBuilder builder=new StringBuilder();
        builder.append(phiNode.toString()).append(" = phi ").append(phiNode.getType()).append(" ");
        boolean flag=false;
        for (int i = 0; i < phiNode.operands.size(); i+=2) {
            builder.append("[").append(phiNode.operands.get(i).val).append(", ").append(phiNode.operands.get(i + 1).val).append("]");
            builder.append(",");
            flag=true;
        }
        if(flag) {
            builder.delete(builder.length() - 1, builder.length());
        }
        print(builder.toString());
        return null;
    }

    @Override
    public Object visitReturnInst(ReturnInst returnInst) {
        if (returnInst.hasRetValue()) {
            var val = returnInst.operands.get(0).val;
            print("ret " + val.getType() + " " + val);
        } else {
            print("ret void");
        }
        return null;
    }

    @Override
    public Object visitStoreInst(StoreInst storeInst) {
        var storeVal=storeInst.operands.get(0).val;
        var ptr=storeInst.operands.get(1).val;
        print("store "+(storeVal.getType().isNull()?((PointerType)ptr.getType()).getPtrType():storeVal.getType())+" "+storeVal+", "+ptr.getType()+" "+ptr);
        return null;
    }

    @Override
    public Object visit(Value value) {
        return value.accept(this);
    }
}
