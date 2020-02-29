package optim.dsa;

import IR.Function;
import IR.Module;
import java.util.HashMap;

public class Local  {
    HashMap<Function,DSGraph> graphs=new HashMap<>();

    public void run(Module module){
        graphs.clear();
        for (var func : module.getFunctionList()) {
            graphs.put(func,new DSGraph(func));
        }
    }
}
