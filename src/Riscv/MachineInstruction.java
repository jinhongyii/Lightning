package Riscv;

import java.util.HashSet;
import java.util.Set;

public class MachineInstruction {
    MachineInstruction prev;
    MachineInstruction next;
    MachineBasicBlock parent;
    public void setNextInstruction(MachineInstruction instruction){
        this.next=instruction;
        instruction.prev=this;
    }
    public void setPrev(MachineInstruction prev) {
        this.prev = prev;
    }

    public void setNext(MachineInstruction next) {
        this.next = next;
    }

    public MachineInstruction getPrev() {
        return prev;
    }

    public MachineInstruction getNext() {
        return next;
    }

    public void accept(Visitor visitor){
    }
    public Set<VirtualRegister> getUse(){
        return new HashSet<>();
    }
    public Set<VirtualRegister> getDef(){
        return new HashSet<>();
    }
    public void addInstBefore(MachineInstruction inst){
        if (parent.head == this) {
            parent.head = inst;
            inst.setNextInstruction(this);
            inst.setPrev(null);
        } else {
            this.prev.setNextInstruction(inst);
            inst.setNextInstruction(this);
        }
        inst.parent=this.parent;
    }
    public void addInstAfter(MachineInstruction inst){
        if (parent.tail == this) {
            parent.tail = inst;
            this.setNextInstruction(inst);
            inst.setNext(null);
        } else {
            inst.setNextInstruction(this.next);
            this.setNextInstruction(inst);
        }
        inst.parent=this.parent;
    }
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
    }
    public void color(){

    }
}
