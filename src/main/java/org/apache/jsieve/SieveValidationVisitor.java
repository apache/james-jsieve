package org.apache.jsieve;

import java.util.ArrayList;
import java.util.List;

import org.apache.jsieve.exception.LookupException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.parser.generated.ASTargument;
import org.apache.jsieve.parser.generated.ASTarguments;
import org.apache.jsieve.parser.generated.ASTblock;
import org.apache.jsieve.parser.generated.ASTcommand;
import org.apache.jsieve.parser.generated.ASTcommands;
import org.apache.jsieve.parser.generated.ASTstart;
import org.apache.jsieve.parser.generated.ASTstring;
import org.apache.jsieve.parser.generated.ASTstring_list;
import org.apache.jsieve.parser.generated.ASTtest;
import org.apache.jsieve.parser.generated.ASTtest_list;
import org.apache.jsieve.parser.generated.SieveParserVisitor;
import org.apache.jsieve.parser.generated.SimpleNode;

/**
 * Validates nodes visited.
 * Some checks are more conveniently carried out what then 
 * tree has already been constructed.
 */
public class SieveValidationVisitor implements SieveParserVisitor {
    
    private boolean requireAllowed = true;
    private boolean isInRequire = false;

    public Object visit(SimpleNode node, Object data) throws SieveException {
        return visitNode(node, data);
    }

    private Object visitNode(SimpleNode node, Object data) throws SieveException {
        List children = new ArrayList(node.jjtGetNumChildren());
        node.childrenAccept(this, children);
        return data;
    }

    public Object visit(ASTstart node, Object data) throws SieveException {
        return visitNode(node, data);
    }

    public Object visit(ASTcommands node, Object data) throws SieveException {
        return visitNode(node, data);
    }

    public Object visit(ASTcommand node, Object data) throws SieveException {
        final String name = node.getName();
        Command.lookup(name);
        if ("require".equalsIgnoreCase(name)) {
            if (requireAllowed) {
                isInRequire = true;
            } else {
                throw new SieveException("'require' is only allowed before other commands");
            }
        } else {
            requireAllowed = false;
            isInRequire = false;
        }
        return visitNode(node, data);
    }

    public Object visit(ASTblock node, Object data) throws SieveException {
        return visitNode(node, data);
    }

    public Object visit(ASTarguments node, Object data) throws SieveException {

        return visitNode(node, data);
    }

    public Object visit(ASTargument node, Object data) throws SieveException {
        return visitNode(node, data);
    }

    public Object visit(ASTtest node, Object data) throws SieveException {
        return visitNode(node, data);
    }

    public Object visit(ASTtest_list node, Object data) throws SieveException {
        return visitNode(node, data);
    }

    public Object visit(ASTstring node, Object data) throws SieveException {
        if (isInRequire) {
            final Object value = node.getValue();
            if (value != null && value instanceof String) {
                final String quotedName = (String) value;
                final String name = quotedName.substring(1, quotedName.length()-1);
                try {
                    Command.lookup(name);
                } catch (LookupException e) {
                    //TODO: catching is inefficient, should just check
                    Test.lookup(name);
                }
            }
        }
        return visitNode(node, data);
    }

    public Object visit(ASTstring_list node, Object data) throws SieveException {
        return visitNode(node, data);
    }

}
