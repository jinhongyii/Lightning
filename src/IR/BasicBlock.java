package IR;

import java.util.ArrayList;
import java.util.LinkedList;

public class BasicBlock extends  Value {
    Function parent;
    LinkedList<Instruction> instructionList=new LinkedList<>();

    public BasicBlock(String name) {
        super(name, Type.TheLabelType,ValueType.BasicBlockVal);

    }
    public void addInst(Instruction inst){
        instructionList.add(inst);
        if(parent!=null) {
            parent.symtab.put(inst.getName(), inst);
        }
    }
    public void addInstToFirst(Instruction inst){
        instructionList.addFirst(inst);
        parent.symtab.put(inst.getName(),inst);

    }
    public void setParent(Function parent) {
        this.parent=parent;
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitBasicBlock(this);
    }

    public LinkedList<Instruction> getInstructionList() {
        return instructionList;
    }
}
