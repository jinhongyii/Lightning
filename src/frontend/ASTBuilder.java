package frontend;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ASTBuilder extends mxBaseListener {
    public ParseTreeProperty values = new ParseTreeProperty<>();

    @Override
    public void exitCompilationUnit(mxParser.CompilationUnitContext ctx) {

    }

}
