package semantic;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

import static java.lang.Math.abs;

public class SymbolTable<Type> implements Iterable<SymbolTable.Entry<Type>>{
    @NotNull
    @Override
    public Iterator<Entry<Type>> iterator() {
        int i=0;


        return new tableIterator(0,null);
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
            if(now==null){
                int i;
                for (i = 0; i < 1024; i++) {
                    if (hashtable[i] != null) {
                        break;
                    }
                }
                return now=hashtable[i];
            }
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

    private ArrayList<HashSet<String>> stack=new ArrayList<>();

    public SymbolTable() {
        stack.add(new HashSet<>());
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
        if (stack.get(stack.size()-1).contains(sym)) {
            throw new TypeChecker.semanticException("redefinition");
        }
        stack.get(stack.size()-1).add(sym);

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
        stack.add(new HashSet<>());
    }
    public void endScope(){
        for (String sym : stack.get(stack.size()-1)) {
            int idx=abs(sym.hashCode()%1023);
            hashtable[idx]=hashtable[idx].next;
        }
        stack.remove(stack.size()-1);
    }
    public boolean isGlobalVariable(String string){
        if (lookup(string) instanceof FuncEntry) {
            return true;
        }
        if(!stack.get(0).contains(string)){
            return false;
        }
        for (int i = 1; i < stack.size(); i++) {
            if (stack.get(i).contains(string)) {
                return false;
            }
        }
        return true;
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