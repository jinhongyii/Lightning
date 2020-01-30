package semantic;


import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.lang.Math.abs;

public class SymbolTable<Type> implements Iterable<SymbolTable.Entry<Type>>{
    @NotNull
    @Override
    public Iterator<Entry<Type>> iterator() {
        int i=0;

        for (i = 0; i < 1024; i++) {
            if (hashtable[i] != null) {
                break;
            }
        }
        return new tableIterator(0,i==1023?null:hashtable[i]);
    }

    public class tableIterator implements Iterator<Entry<Type>>{
        int idx;
        int iterated=0;
        Entry now;
        public tableIterator(int idx,Entry now){
            this.idx=idx;
            this.now=now;
        }
        @Override
        public boolean hasNext() {
            return iterated<size();
        }
        @Override
        public Entry<Type> next() {
            iterated++;
            if (now.next != null) {
                now = now.next;
            } else {
                idx++;
                for(;idx<1022 && hashtable[idx]==null;idx++);
                now=hashtable[idx];
            }
            return now;
        }
    }


    public static class Entry<Type>{
        String sym;
        Type type;
        Entry next;

        public String getSym() {
            return sym;
        }

        public Type getType() {
            return type;
        }

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
     private Entry[] hashtable=new Entry[1023];

    private Stack<HashSet<String>> stack=new Stack<>();

    public SymbolTable() {
        stack.push(new HashSet<>());
    }
    public int size(){
        int cnt=0;
        for (var level : stack) {
            cnt+=level.size();
        }
        return cnt;

    }
    public void enter(String sym,Type type) throws TypeChecker.semanticException {
        int idx=abs(sym.hashCode()%1023);
        hashtable[idx]=new Entry<>(sym,type,hashtable[idx]);
        if (stack.peek().contains(sym)) {
            throw new TypeChecker.semanticException("redefinition");
        }
        stack.peek().add(sym);

    }

    public Type lookup(String sym) {
        int idx=abs(sym.hashCode()%1023);
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
        stack.push(new HashSet<>());
    }
    public void endScope(){
        for (String sym : stack.peek()) {
            int idx=abs(sym.hashCode()%1023);
            hashtable[idx]=hashtable[idx].next;
        }
        stack.pop();


    }

}
//public class SymbolTable<Type> extends ArrayList<HashMap<String,Type>>{
//    public SymbolTable(){
//        add(new HashMap<>());
//    }
//
//    @Override
//    public synchronized int size() {
//        int cnt=0;
//        for (var level : this) {
//            cnt+=level.size();
//        }
//        return cnt;
//    }
//
//    public void enter(String sym,Type type) throws TypeChecker.semanticException {
//            if (get(super.size()-1).containsKey(sym)) {
//                throw new TypeChecker.semanticException("redefinition");
//            }
//            get(super.size()-1).put(sym,type);
//
//    }
//    public Type lookup(String sym){
//        for (int i = super.size() - 1; i >= 0; i--) {
//            var tmp=get(i).get(sym);
//            if ( tmp != null) {
//                return tmp;
//            }
//        }
//        return null;
//    }
//    public void beginScope(String)
//}