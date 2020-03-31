package Riscv;

import IR.*;
import IR.Types.ArrayType;
import IR.Types.PointerType;

import java.util.ArrayList;
import java.util.LinkedList;

public class MachineModule {
    ArrayList<GlobalVar> globalVars=new ArrayList<>();
    LinkedList<MachineFunction> functions=new LinkedList<>();

    public ArrayList<GlobalVar> getGlobalVars() {
        return globalVars;
    }

    public LinkedList<MachineFunction> getFunctions() {
        return functions;
    }
    public void addFunc(MachineFunction function){
        functions.addLast(function);
    }
    public GlobalVar addGlobal(GlobalVariable globalVariable){
        GlobalVar gVar;
        var initializer=globalVariable.getInitializer();
        var type=((PointerType)globalVariable.getType()).getPtrType();
        if (type instanceof ArrayType) {
            gVar=new GlobalVar(((ConstantString) initializer).getVal(),globalVariable.getName());
        } else {
            int val;
            int size;
            if (type.equals(Type.TheInt1)) {
                val=initializer==null?0:((ConstantBool) initializer).isTrue() ? 1 : 0;
                size=1;
            } else {
                if (type instanceof PointerType) {
                    val = 0;
                    size = 4;
                } else {
                    val=initializer==null?0:((ConstantInt)initializer).getVal();
                    size=4;
                }
            }
            gVar=new GlobalVar(val,size,globalVariable.getName() );
        }
        globalVars.add(gVar);
        return gVar;
    }
}
