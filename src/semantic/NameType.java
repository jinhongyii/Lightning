package semantic;



public class NameType extends SemanticType{
    private String name;
    private SemanticType binding;

    @Override
    protected SemanticType actual() {
        return binding;
    }
    @Override
    public boolean canAssignTo(SemanticType other) {
        return this.actual().canAssignTo(other);
    }
    public void bind(SemanticType other){
        binding=other;
    }
    public NameType(String name){
        super(kind.name);
        this.name=name;
        this.binding=null;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && binding.equals(((NameType)obj).binding);
    }
    public boolean isArrayType(){
        return binding!=null && binding.typekind==kind.array;
    }
    public boolean isBoolType(){
        return binding!=null && binding.typekind==kind.bool;
    }
    public boolean isIntType(){
        return binding!=null && binding.typekind==kind.integer;
    }
    public boolean isNameType(){
        return binding==null;
    }
    public boolean isNullType(){
        return binding!=null && binding.typekind==kind.nil;
    }
    public boolean isRecordType() {
        return binding!=null && binding.typekind == kind.record;
    }
    public boolean isStringType(){
        return binding!=null && binding.typekind==kind.voidType;
    }
    public boolean isVoidType(){
        return binding!=null && binding.typekind==kind.voidType;
    }

    @Override
    public boolean strictComparable(SemanticType other) {
        return binding!=null && binding.strictComparable(other);
    }

    @Override
    public boolean mediumComparable(SemanticType other) {
        return binding!=null && binding.mediumComparable(other);
    }

    @Override
    public boolean looseComparable(SemanticType other) {
        return binding!=null && binding.looseComparable(other);
    }
}
