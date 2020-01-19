package semantic;

import java.util.ArrayList;

public class RecordType extends SemanticType {
    private ArrayList<String> fieldName=new ArrayList<>();
    private ArrayList<SemanticType> fieldType=new ArrayList<>();
    String recordName;
    public ArrayList<String> getFieldName() {
        return fieldName;
    }

    public ArrayList<SemanticType> getFieldType() {
        return fieldType;
    }

    public String getRecordName() {
        return recordName;
    }

    public RecordType(String recordName){
        super(kind.record);
        this.recordName=recordName;
    }
    public void addRecord(String name,SemanticType type){
        fieldName.add(name);
        fieldType.add(type);
    }

    @Override
    public boolean canAssignTo(SemanticType other) {
        return other.actual().equals(this);
    }

    @Override
    public boolean equals(Object obj) {

        return obj==this;
    }

    @Override
    public boolean looseComparable(SemanticType other) {
        return other.isNullType();
    }
}
