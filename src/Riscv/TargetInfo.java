package Riscv;

import java.util.HashMap;
import java.util.Set;

public class TargetInfo {
    public static String[] regNames={"zero","ra","sp","gp","tp","t0","t1","t2","s0","s1","a0","a1","a2","a3","a4","a5","a6","a7","s2","s3","s4","s5","s6","s7","s8","s9","s10","s11","t3","t4","t5","t6" };
    public static String[] calleeSavedRegister={"s0","s1","s2","s3","s4","s5","s6","s7","s8","s9","s10","s11"};
    public static String[] argumentRegister={"a0","a1","a2","a3","a4","a5","a6","a7"};
    public static Set<String> callerSavedRegister =Set.of("ra","t0","t1","t2","a0","a1","a2","a3","a4","a5","a6","a7","t3","t4","t5","t6" );

    public static String[] AllocableRegister={"ra","t0","t1","t2","s0","s1","a0","a1","a2","a3","a4","a5","a6","a7","s2","s3","s4","s5","s6","s7","s8","s9","s10","s11","t3","t4","t5","t6" };
    public static HashMap<String,VirtualRegister> vPhysicalReg=new HashMap<>();

    static {
        for (var name : regNames) {
            var vReg=new VirtualRegister(name);
            vPhysicalReg.put(name, vReg);
            vReg.color=new PhysicalRegister(name);
        }
    }
}
