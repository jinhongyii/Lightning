package IR;

import java.util.HashMap;

public class SymbolTable extends HashMap<String,Value> {
    public SymbolTable(){
        super();
    }

    @Override
    public Value put(String key, Value value) {
        var uniqueName=getUniqueName(key);
        value.setName(uniqueName);
        return super.put(uniqueName, value);
    }
    private String getUniqueName(String originalName){
        if (!originalName.equals("") && get(originalName) == null) {
            return originalName;
        }
        int counter=0;
        String tryName=originalName;
        for (counter = 0; get(tryName) != null; counter++) {
            tryName=originalName+"."+(counter+1);
        }

        return originalName+"."+counter;
    }

    public void remove(Value value) {
        super.remove(value.getName());
    }
}
