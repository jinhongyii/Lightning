package optim;

import IR.Function;

public interface FunctionPass extends Pass{
    void runOnFunction(Function function);
}
