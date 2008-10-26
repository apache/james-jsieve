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

package org.apache.jsieve.parser.address;

import org.apache.jsieve.parser.generated.address.ASTaddr_spec;
import org.apache.jsieve.parser.generated.address.ASTaddress;
import org.apache.jsieve.parser.generated.address.ASTaddress_list;
import org.apache.jsieve.parser.generated.address.ASTangle_addr;
import org.apache.jsieve.parser.generated.address.ASTdomain;
import org.apache.jsieve.parser.generated.address.ASTgroup_body;
import org.apache.jsieve.parser.generated.address.ASTlocal_part;
import org.apache.jsieve.parser.generated.address.ASTmailbox;
import org.apache.jsieve.parser.generated.address.ASTname_addr;
import org.apache.jsieve.parser.generated.address.ASTphrase;
import org.apache.jsieve.parser.generated.address.ASTroute;
import org.apache.jsieve.parser.generated.address.AddressListParserVisitor;
import org.apache.jsieve.parser.generated.address.SimpleNode;

/**
 * Do nothing implementation suitablae for subclassing.
 */
public abstract class BaseAddressListVisitor implements
        AddressListParserVisitor {

    public Object visit(SimpleNode node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTaddress_list node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTaddress node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTmailbox node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTname_addr node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTgroup_body node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTangle_addr node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTroute node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTphrase node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTaddr_spec node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTlocal_part node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTdomain node, Object data) {
        return node.childrenAccept(this, data);
    }
}
