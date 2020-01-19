package semantic;

public class NameEntry {
    enum kind{varEntry, funcEntry};
    protected kind nameKind;
    NameEntry(kind nameKind){
        this.nameKind=nameKind;
    }
}
