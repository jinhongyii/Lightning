package backend;

import Riscv.*;

public class Peephole {
    MachineFunction function;
    public Peephole(MachineFunction function){
        this.function=function;
    }
    public void run(){
        removeRedundantLoadStore();
    }

    private void removeRedundantLoadStore() {
        for (var bb = function.getHead(); bb != null; bb = bb.getNext()) {
            for (var inst = bb.getHead(); inst != null;) {
                var tmp=inst.getNext();
                if (inst instanceof Load) {
                    MachineInstruction prev = inst.getPrev();
                    if (prev instanceof Store && ((Store) prev).getPtr() == ((Load) inst).getSrc() && ((Store) prev).offset==((Load) inst).offset) {
                        if (((Store) prev).getSrc() != ((Load) inst).getRd()) {
                            inst.getPrev().addInstAfter(new Move(((Store) prev).getSrc(), ((Load) inst).getRd()));
                        }
                        inst.delete();
                    }
                }
                inst=tmp;
            }
        }
    }
}
