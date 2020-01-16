package semantics;

public class Symbol {
    String name;
    int hashcode;
    public Symbol(String name){
        this.name=name;
        hashcode=name.hashCode();
    }

}
