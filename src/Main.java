import IR.IRPrinter;
import IR.Module;
import backend.IRBuilder;
import frontend.ASTBuilder;
import optim.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
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
        InputStream is = new FileInputStream(args[0]);
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
        IRPrinter irPrinter=new IRPrinter(topModule,"main.ll");
        for (var func : topModule.getFunctionList()) {
            if (!func.isExternalLinkage()) {
               Optimizer optimizer=new Optimizer(func);
               optimizer.run();
            }

        }
        boolean changed=true;
        while(changed) {
            changed=false;
            Inliner inliner = new Inliner(topModule);
            inliner.run();
            IRPrinter inlinePrinter = new IRPrinter(topModule, "inline.ll");
            for (var func : topModule.getFunctionList()) {
                if (!func.isExternalLinkage()) {
                    Optimizer optimizer = new Optimizer(func);
                    changed|=optimizer.run();
                }
            }
        }
        DeadFunctionElimination dfe=new DeadFunctionElimination(topModule);
//        dfe.run();
        IRPrinter finalPrinter=new IRPrinter(topModule,"final.ll");

    }
}
