package optim;

import IR.Function;

abstract public class FunctionPass implements Pass{
        Function function;
        FunctionPass(Function function){
            this.function=function;
        }
        //todo: return changed
        // remember to clear all internal data structure before exit run()
        public abstract boolean run();



    }
