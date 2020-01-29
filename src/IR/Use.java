package IR;

public class Use {
    Value val;
    User user;
    Use prev,next;

    public Use(Value val,User user){
        val.uses.add(this);
        this.val=val;
        this.user=user;
    }
}
