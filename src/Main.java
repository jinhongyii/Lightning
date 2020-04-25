import IR.IRPrinter;
import IR.Module;
import Riscv.MachineModule;
import backend.AsmPrinter;
import backend.IRBuilder;
import backend.InstructionSelector;
import backend.RegAlloc;
import frontend.ASTBuilder;
import optim.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import parser.ThrowingErrorListener;
import parser.mxLexer;
import parser.mxParser;
import semantic.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws IOException, TypeChecker.semanticException {
        InputStream is = new FileInputStream("code.txt");
        ANTLRInputStream input = new ANTLRInputStream(is);
        mxLexer lexer=new mxLexer(input);
        lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
        CommonTokenStream tokens =new CommonTokenStream(lexer);
        mxParser parser=new mxParser(tokens);
        parser.addErrorListener(ThrowingErrorListener.INSTANCE);
        ParseTree tree=parser.compilationUnit();
        ParseTreeWalker walker=new ParseTreeWalker();
        SymbolTable<SemanticType> typeTable=new SymbolTable<>();
        SymbolTable<NameEntry> valTable=new SymbolTable<>();
        ASTBuilder builder=new ASTBuilder(typeTable);
        walker.walk(builder, tree);
//        ASTPrinter printer=new ASTPrinter(builder.getASTStartNode());
        FunctionScanner scanner=new FunctionScanner(typeTable,valTable,builder.getASTStartNode());
        TypeChecker typeChecker=new TypeChecker(typeTable,valTable,builder.getASTStartNode());
        IRBuilder irBuilder=new IRBuilder(typeTable,valTable,builder.getASTStartNode());
        Module topModule = irBuilder.getTopModule();
        if(args[0].equals("0")) {
            IRPrinter irPrinter = new IRPrinter(topModule, "main.ll", false);
        }else {
//            IRPrinter irPrinter = new IRPrinter(topModule, "main.ll", true);
            GlobalOptimizer optimizer = new GlobalOptimizer(topModule);
            optimizer.run();
            MachineModule mModule=new MachineModule();
            InstructionSelector selector=new InstructionSelector(mModule,topModule);
//            AsmPrinter printer=new AsmPrinter(mModule,"pseudo.s");

            for (var func : mModule.getFunctions()) {
                if(!func.isExternalLinkage()) {
                    RegAlloc alloc = new RegAlloc(func, optimizer.localOptimizers.get(func.getIRfunction()).loopAnalysis);
                    alloc.alloc();
                }
            }
            AsmPrinter printer2=new AsmPrinter(mModule,"output.s");

        }

    }
}
