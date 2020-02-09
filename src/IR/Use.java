package IR;

public class Use {
    Value val;
    User user;
    Use prev,next;

    public Use getPrev() {
        return prev;
    }

    public Use getNext() {
        return next;
    }

    public Use(Value val, User user){
        if(val!=null) {
            if (val.use_tail == null) {
                val.use_tail = val.use_head = this;
            } else {
                val.use_tail.setNextUse(this);
                val.use_tail = this;
            }
        }
//        val.uses.add(this);
        this.val=val;
        this.user=user;
    }

    public void setNextUse(Use next) {
        next.prev=this;
        this.next=next;
    }
    public Value getVal() {
        return val;
    }

    public User getUser() {
        return user;
    }


    public void setValue(Value val){
        if(this.val!=null){
            delete();
        }
        this.val=val;
        if(val!=null) {
            if (val.use_tail != null) {
                val.use_tail.setNextUse(this);
                val.use_tail = this;
            } else {
                val.use_tail = val.use_head = this;
            }
        }
    }
    public void delete(){
        if (this.prev != null) {
            this.prev.next = this.next;
        } else {
            val.use_head=this.next;
        }
        if (this.next != null) {
            this.next.prev = this.prev;
        } else {
            val.use_tail=this.prev;
        }
        this.next=null;
        this.prev=null;
    }
}
