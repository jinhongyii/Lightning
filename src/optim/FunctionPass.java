package optim;

import IR.Function;

abstract public class FunctionPass implements Pass{
        Function function;
        FunctionPass(Function function){
            this.function=function;
        }

        public abstract void run();



    }
