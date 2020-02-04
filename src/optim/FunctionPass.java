package optim;

import IR.Function;
import IR.Module;

abstract public class FunctionPass implements Pass{
        Function function;
        FunctionPass(Function function){
            this.function=function;
        }

        abstract void run();



    }
