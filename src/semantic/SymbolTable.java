package semantic;


import java.util.Stack;

public class SymbolTable<Type> {
    private static class Entry<Type>{
        String sym;
        Type type;
        Entry next;
        Entry(){
            this.sym="";
            this.type=null;
            this.next=null;
        }
        Entry(String sym,Type type,Entry next){
            this.sym=sym;
            this.type=type;
            this.next=next;
        }
    }
    Entry[] hashtable=new Entry[1023];

    private Stack<String> stack=new Stack<>();

    public SymbolTable() {

    }

    public void enter(String sym,Type type) {
        int idx=sym.hashCode()%1023;
        hashtable[idx]=new Entry<>(sym,type,hashtable[idx]);
        stack.push(sym);
    }

    public Type lookup(String sym) {
        int idx=sym.hashCode()%1023;
        Entry entry=hashtable[idx];
        while (entry != null) {
            if (entry.sym.equals(sym)) {
                return (Type) entry.type;
            } else {
                entry=entry.next;
            }
        }
        return null;
    }
    private final String marksym="<mark>";
    public void beginScope(){
        enter(marksym, null);
    }
    public void endScope(){
        String s;
        do {
            s=stack.pop();
            int idx=s.hashCode()%1023;
            hashtable[idx]=hashtable[idx].next;
        }while (!s.equals(marksym));
    }

}
