package backend;

import Riscv.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class AsmPrinter implements Visitor {
    private String prefix="";
//    private FileWriter writer;
//    BufferedWriter bufferedWriter;
    private void indent(){prefix+="\t";}
    private void dedent(){prefix=prefix.substring(0,prefix.length()-1);}
    private void print(String str){
        System.out.println(prefix+str);
//        try {
//            bufferedWriter.write(prefix + str + "\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
    public AsmPrinter(MachineModule module,String filename) throws IOException {
//        writer=new FileWriter("tmp/"+filename);
//        bufferedWriter=new BufferedWriter(writer,8096);
        visitModule(module);
//        bufferedWriter.flush();
    }
    @Override
    public void visitBranch(Branch inst) {
        print(inst.getOpcode().toString()+"\t"+inst.getRs()+", "+inst.getRt()+", "+inst.getTarget());
    }

    @Override
    public void visitCall(Call inst) {
        print("call\t"+inst.getFunction());
    }

    @Override
    public void visitGlobalVar(GlobalVar gvar) {
        print(".globl\t"+gvar.getName()+"\t\t\t\t\t#@"+gvar.getName());
        if(!gvar.isString() && gvar.getSize()==4)
        print(".p2align\t2");
        dedent();
        print(gvar.getName()+":");
        indent();
        if (!gvar.isString()) {
            if (gvar.getSize() == 1) {
                print(".byte\t" + gvar.getVal());
            } else if (gvar.getSize() == 4) {
                print(".word\t" + gvar.getVal());
            }
        } else {
            print(".asciz\t"+ gvar.getStr());
        }

    }

    @Override
    public void visitI_type(I_Type inst) {
        if (inst.getOp() != I_Type.Opcode.sltiu) {
            print(inst.getOp() + "\t" + inst.getRd() + ", " + inst.getRs1() + ", " + inst.getImm());
        } else {
            print("seqz\t"+inst.getRd()+", "+inst.getRs1());
        }
    }

    @Override
    public void visitJump(Jump inst) {
        print("j\t"+inst.getTarget());
    }

    @Override
    public void visitLI(LI inst) {
        print("li\t"+inst.getRd()+", "+inst.getImm());
    }

    @Override
    public void visitLA(LA inst) {
        print("la\t"+inst.getRd()+", "+inst.getSymbol());
    }

    @Override
    public void visitLoad(Load inst) {
        String opcode="";
        switch (inst.getSize()) {
            case 1:
                opcode="lb";
                break;
            case 2:
                opcode="lh";
                break;
            case 4:
                opcode="lw";
                break;
            default:
                assert false;
        }
        String src="";
        if (inst.getSrc() instanceof Register) {
            src = "0(" + inst.getSrc() + ")";
        } else {
            src=inst.getSrc().toString();
        }
        print(opcode+"\t"+inst.getRd()+", "+src);
    }

    @Override
    public void visitBB(MachineBasicBlock bb) {
        dedent();
        print(bb.name+":");
        indent();
        for (var inst =bb.getHead();inst!=null;inst=inst.getNext()) {
            visit(inst);
        }
    }

    @Override
    public void visitFunction(MachineFunction function) {
        //todo: reschedule block
        var name=function.getName();
        print(".globl\t"+name+"\t\t\t\t\t # -- Begin function "+name);
        print(".p2align\t2");
        print(".type\t"+name+",@function");
        dedent();
        print(name+":");
        indent();
        for (var bb : function.getBasicBlocks()) {
            visitBB(bb);
        }
        print("\t\t\t\t\t # -- End function");

    }

    @Override
    public void visitModule(MachineModule module) {
        indent();
        print(".text\n");
        for (var func : module.getFunctions()) {
            if (!func.isExternalLinkage()) {
                visitFunction(func);
            }
        }
        print(".section\t.sbss,\"aw\",@nobits");
        for (var globl : module.getGlobalVars()) {
            if(!globl.isString()) {
                visitGlobalVar(globl);
            }
        }
        print(".section\t.sdata,\"aw\",@progbits");
        for (var globl : module.getGlobalVars()) {
            if (globl.isString()) {
                visitGlobalVar(globl);
            }
        }
    }

    @Override
    public void visitMove(Move inst) {
        print("mv\t"+inst.getRd()+", "+inst.getRs());
    }

    @Override
    public void visitReturn(Return inst) {
        print("ret ");
    }

    @Override
    public void visitR_type(R_Type inst) {
        if (inst.getOpcode() != R_Type.Opcode.sltu) {
            print(inst.getOpcode() + "\t" + inst.getRd() + ", " + inst.getRs1() + ", " + inst.getRs2());
        } else {
            print("snez\t"+inst.getRd()+", "+inst.getRs2());
        }
    }

    @Override
    public void visitStore(Store inst) {
        String opcode="";
        switch (inst.getSize()) {
            case 1:
                opcode="sb";
                break;
            case 2:
                opcode="sh";
                break;
            case 4:
                opcode="sw";
                break;
            default:
                assert false;
        }
        String ptr="";
        if (inst.getPtr() instanceof Register) {
            ptr=("0(" + inst.getPtr() + ")");
        } else if (inst.getPtr() instanceof StackLocation) {
            ptr = inst.getPtr().toString();
        } else {
            ptr="%lo("+inst.getPtr()+")("+inst.helperReg+")";
        }
        print(opcode+"\t"+inst.getSrc()+","+ptr);
    }

    @Override
    public void visitLUI(LUI inst) {
        print("lui\t"+inst.getRt()+", %hi("+inst.getSymbol()+")");
    }

    @Override
    public void visit(MachineInstruction inst) {
        inst.accept(this);
    }
}
