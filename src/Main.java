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
                var cFGSimplifier=new CFGSimplifier(func);
                var dominatorAnalysis=new DominatorAnalysis(func);
                var mem2reg=new Mem2reg(func,dominatorAnalysis);
                var dce=new DeadCodeElimination(func);
                var sccp=new SCCP(func);
                var cse=new CSE(func,dominatorAnalysis);
                cFGSimplifier.run();
                dominatorAnalysis.run();
                mem2reg.run();
                boolean changed=true;
                while(changed) {
                    changed=false;
                    dominatorAnalysis.run();
                    changed|=sccp.run();
                    changed|=dce.run();
                    changed|=cse.run();
                    changed|=cFGSimplifier.run();
                }
            }
        }
        IRPrinter ssaPrinter=new IRPrinter(topModule,"ssa.ll");

    }
}
