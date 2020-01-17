package semantic;

import java.util.ArrayList;

public class Type {
    enum kind{record,nil,integer,string,array,bool,voidType,name};
    ArrayList<Type> record;
    Type array;
    String name;
    Type binding;
}
