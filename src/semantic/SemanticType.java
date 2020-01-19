package semantic;

import ast.Type;

import java.util.ArrayList;
abstract public class SemanticType {
    public enum kind{record,nil,integer,string,array,bool,voidType,name} ;
    protected kind typekind;
    protected SemanticType actual(){
        return this;
    }
    public boolean canAssignTo(SemanticType other){
        return false;
    }
    public boolean strictComparable(SemanticType other){return false;}
    public boolean mediumComparable(SemanticType other){return false;}
    public boolean looseComparable(SemanticType other){return false;}
    public boolean isArrayType(){
        return typekind==kind.array;
    }
    public boolean isBoolType(){
        return typekind==kind.bool;
    }
    public boolean isIntType(){
        return typekind==kind.integer;
    }
    public boolean isNameType(){
        return typekind==kind.name;
    }
    public boolean isNullType(){
        return typekind==kind.nil;
    }
    public boolean isRecordType() {
        return typekind == kind.record;
    }
    public boolean isStringType(){
        return typekind==kind.string;
    }
    public boolean isVoidType(){
        return typekind==kind.voidType;
    }
    public SemanticType(kind typekind){
        this.typekind=typekind;
    }
    @Override
    public boolean equals(Object obj) {
        return this.typekind==((SemanticType)obj).typekind;
    }
    //    ArrayList<SemanticType> record=new ArrayList<>();
//    SemanticType array;
//    int arrayDim;
//    String name;
//    SemanticType binding;
//    public void addRecord(SemanticType semanticType){
//        record.add(semanticType);
//    }
//    public SemanticType(kind kind){
//        this.typekind=kind;
//    }
//    public SemanticType(kind kind, SemanticType array, int arrayDim){
//        this.typekind=kind;
//        this.array=array;
//        this.arrayDim=arrayDim;
//    }
//    public SemanticType(kind kind, String name){
//        this.typekind=kind;
//        this.name=name;
//        this.binding=null;
//    }


}
