package IR.instructions;

import IR.*;

public class BinaryOperation extends Instruction {
    public BinaryOperation(String name, Type type, Opcode opcode,Value lhs,Value rhs) {
        super(name, type, opcode);
        operands.add(new Use(lhs,this));
        operands.add(new Use(rhs,this));
    }
    public Value getLhs(){
        return operands.get(0).getVal();
    }
    public Value getRhs(){
        return operands.get(1).getVal();
    }
    public boolean isNot(){
        return getOpcode()==Opcode.xor && (getLhs().equals(new ConstantInt(-1)) || getRhs().equals(new ConstantInt(-1)) || getLhs().equals(new ConstantBool(true)) || getRhs().equals(new ConstantBool(true)));
    }
    public Value getNotSrc(){
        if (getLhs().equals(new ConstantInt(-1)) || getLhs().equals(new ConstantBool(true))) {
            return getRhs();
        } else {
            return getLhs();
        }
    }
    public boolean isNeg(){
        return getOpcode()==Opcode.sub && getLhs().equals(new ConstantInt(0));
    }
    public void swapLhsAndRhs(){
        operands.add(operands.remove(0));
    }
    public static BinaryOperation create(String name, Opcode opcode,Value lhs,Value rhs){
        switch (opcode) {
            case EQ:case NE:case GE:case GT:case LT:case LE:
                return new IcmpInst(name,opcode,lhs,rhs);
            default:
                return new BinaryOpInst(name,opcode,lhs,rhs);
        }
    }
}
