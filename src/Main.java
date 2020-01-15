import frontend.ASTBuilder;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Main {
    public static void main(String[] args){
        ParseTreeWalker walker=new ParseTreeWalker();
        ParseTreeListener listener=new ASTBuilder();
//        ParseTree=
    }
}
