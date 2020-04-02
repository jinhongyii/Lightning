package Riscv;

import java.util.HashMap;

public class TargetInfo {
    public static String[] regNames={"zero","ra","sp","gp","tp","t0","t1","t2","s0","s1","a0","a1","a2","a3","a4","a5","a6","a7","s2","s3","s4","s5","s6","s7","s8","s9","s10","s11","t3","t4","t5","t6" };
    public static String[] calleeSavedRegister={"s0","s1","s2","s3","s4","s5","s6","s7","s8","s9","s10","s11"};
    public static String[] argumentRegister={"a0","a1","a2","a3","a4","a5","a6","a7"};
    public static String[] callerSavedRegister ={"ra","t0","t1","t2","a0","a1","a2","a3","a4","a5","a6","a7","t3","t4","t5","t6" };
    public static HashMap<String,VirtualRegister> vPhysicalReg=new HashMap<>();

    static {
        for (var name : regNames) {
            vPhysicalReg.put(name, new VirtualRegister(name));
        }
    }
}
