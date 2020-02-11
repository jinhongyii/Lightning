package IR;

public class Instruction extends User {
    public void setParent(BasicBlock parent) {
        this.parent = parent;
    }

    Instruction prev=null;
    Instruction next=null;
    public Instruction(String name, Type type,Opcode opcode) {
        super(name, type, ValueType.InstructionVal);
        this.opcode=opcode;
    }

    public enum Opcode{
        alloca,load,store,ret,br,call,getelementptr,EQ,NE,LE,GE,LT,GT,phi,add,
        sub,mul,div,rem,and,or,xor,shl,shr,cast
    }
    BasicBlock parent;
    private Opcode opcode;

    public BasicBlock getParent() {
        return parent;
    }

    public Opcode getOpcode() {
        return opcode;
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return null;
    }

    public boolean isTerminator(){
        return opcode==Opcode.ret || opcode==Opcode.br;
    }

    public Instruction getPrev() {
        return prev;
    }

    public Instruction getNext() {
        return next;
    }

    public void setNextInstruction(Instruction instruction) {
        this.next=instruction;
        instruction.prev=this;
    }

    //must transfer use before delete
    public void delete(){
        if(this.parent!=null) {
            if (this.prev != null) {
                this.prev.next = this.next;
            } else {
                parent.head = this.next;
            }
            if (this.next != null) {
                this.next.prev = this.prev;
            } else {
                parent.tail = this.prev;
            }
        }
        for (var use = use_head; use != null; use = use.next) {
            use.delete();
        }
        for (var use : operands) {
            use.delete();
        }
    }
    public Instruction cloneInst(){
        return null;
    }
}
