package optim.dsa;

import IR.Function;

import java.util.ArrayList;
import java.util.HashMap;

public class DSCallNode {
    Function callee;
    DSHandle returnValue;
    ArrayList<DSHandle> arguments;

    public DSCallNode(Function callee, DSHandle returnValue, ArrayList<DSHandle> arguments) {
        this.callee = callee;
        this.returnValue = returnValue;
        this.arguments = arguments;
    }
    public DSCallNode(DSCallNode other,HashMap<DSNode,DSNode> nodeMap){
        this.callee=other.callee;
        this.returnValue=new DSHandle(nodeMap.get(other.returnValue.getNode()),other.returnValue.field);
        this.arguments=new ArrayList<>();
        for (var arg : other.arguments) {
            arguments.add(new DSHandle(nodeMap.get(arg.getNode()),arg.field));
        }
    }
}
