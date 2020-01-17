import frontend.ASTBuilder;
import frontend.ASTPrinter;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import parser.mxLexer;
import parser.mxParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws IOException {
        InputStream is = args.length > 0 ? new FileInputStream(args[0]) : System.in;
        ANTLRInputStream input = new ANTLRInputStream(is);
        mxLexer lexer=new mxLexer(input);
        CommonTokenStream tokens =new CommonTokenStream(lexer);
        mxParser parser=new mxParser(tokens);
        ParseTree tree=parser.compilationUnit();
        ParseTreeWalker walker=new ParseTreeWalker();
        ASTBuilder builder=new ASTBuilder();
        walker.walk(builder, tree);
        ASTPrinter printer=new ASTPrinter(builder.getASTStartNode());
    }
}
