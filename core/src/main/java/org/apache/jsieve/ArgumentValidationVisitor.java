/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.jsieve;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.parser.generated.ASTcommand;
import org.apache.jsieve.parser.generated.ASTcommands;
import org.apache.jsieve.parser.generated.ASTstart;
import org.apache.jsieve.parser.generated.ASTtest;

/**
 * Checks, when a script is parsed, the arguments and blocks that each command
 * and test checks when it runs, and that each elsif or else follows an if or
 * elsif, on code that is reached or not: RFC 5228 section 2.10.6 allows these
 * checks on code that is not reached, and a script that would fail on them is
 * better rejected at upload than when a mail reaches that code. It builds
 * commands and tests as {@link SieveParserVisitorImpl} does, and runs none of
 * them.
 */
class ArgumentValidationVisitor extends SieveParserVisitorImpl {

    private final SieveContext context;

    ArgumentValidationVisitor(SieveContext context) {
        super(context);
        this.context = context;
    }

    public Object visit(ASTstart node, Object data) throws SieveException {
        node.childrenAccept(this, new ArrayList<Object>());
        return data;
    }

    public Object visit(ASTcommands node, Object data) throws SieveException {
        // An elsif or else directly follows an if or elsif (RFC 5228 section 3.1)
        String previous = null;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            final ASTcommand command = (ASTcommand) node.jjtGetChild(i);
            final String name = command.getName().toLowerCase(Locale.ROOT);
            if ((name.equals("elsif") || name.equals("else"))
                    && !("if".equals(previous) || "elsif".equals(previous))) {
                context.setCoordinate(command.getCoordinate());
                throw context.getCoordinate().commandException(
                        "Unexpected Command: \"" + name + "\".");
            }
            previous = name;
        }
        return super.visit(node, data);
    }

    @SuppressWarnings("unchecked")
    public Object visit(ASTcommand node, Object data) throws SieveException {
        super.visit(node, data);
        final List<Object> visited = (List<Object>) data;
        final Command command = (Command) visited.get(visited.size() - 1);
        context.setCoordinate(node.getCoordinate());
        context.getCommandManager().getCommand(command.getName())
                .validate(command.getArguments(), command.getBlock(), context);
        return data;
    }

    @SuppressWarnings("unchecked")
    public Object visit(ASTtest node, Object data) throws SieveException {
        super.visit(node, data);
        final List<Object> visited = (List<Object>) data;
        final Test test = (Test) visited.get(visited.size() - 1);
        context.setCoordinate(node.getCoordinate());
        context.getTestManager().getTest(test.getName()).validate(test.getArguments(), context);
        return data;
    }

}
