/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache", "Jakarta", "JAMES", "JSieve" and 
 *    "Apache Software Foundation" must not be used to endorse or promote
 *    products derived from this software without prior written permission.
 *    For written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */
package org.apache.jsieve.junit.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.jsieve.mail.SieveMailException;
import org.apache.jsieve.mail.optional.EnvelopeAccessors;

/**
 * <p>Class SieveEnvelopeMailAdapter extends class SieveMailAdapter, a mock 
 * implementation of a MailAdapter, to add support for EnvelopeAccessors.<p>
 * 
 * <p>As the Envelope Test is an optional Sieve test, MailAdapter support for the
 * interface is optional too.</p>
 */
public class SieveEnvelopeMailAdapter
    extends SieveMailAdapter
    implements EnvelopeAccessors
{
    /**
     * The FROM address used in the SMTP MAIL command.
     */
    private String fieldEnvelopeFrom;
    
    /**
     * The TO address used in the SMTP RCPT command that resulted in this message
     *  getting delivered to this user.
     */ 
    private String fieldEnvelopeTo;

    /**
     * Constructor for SieveEnvelopeMailAdapter.
     * @param message
     */
    public SieveEnvelopeMailAdapter(MimeMessage message)
    {
        super(message);
    }
    
    /**
     * Method getEnvelopes.
     * @return Map
     */
    protected Map getEnvelopes()
    {
        Map envelopes = new HashMap(2);
        if (null != getEnvelopeFrom())
            envelopes.put("From", getEnvelopeFrom());
        if (null != getEnvelopeTo())
            envelopes.put("To", getEnvelopeTo());
        return envelopes;
    }
        
    /**
     * @see org.apache.jsieve.mail.optional.EnvelopeAccessors#getEnvelope(String)
     */
    public List getEnvelope(String name) throws SieveMailException
    {
        List values = new ArrayList(1);
        Object value = getEnvelopes().get(name);
        if (null != value)
            values.add(value);
        return values;
    }

    /**
     * @see org.apache.jsieve.mail.optional.EnvelopeAccessors#getEnvelopeNames()
     */
    public List getEnvelopeNames() throws SieveMailException
    {
        return new ArrayList(getEnvelopes().keySet());
    }

    /**
     * @see org.apache.jsieve.mail.optional.EnvelopeAccessors#getMatchingEnvelope(String)
     */
    public List getMatchingEnvelope(String name) throws SieveMailException
    {
        Iterator envelopeNamesIter = getEnvelopeNames().iterator();
        List matchedEnvelopeValues = new ArrayList(32);
        while (envelopeNamesIter.hasNext())
        {
            String envelopeName = (String) envelopeNamesIter.next();
            if (envelopeName.trim().equalsIgnoreCase(name))
                matchedEnvelopeValues.addAll(getEnvelope(envelopeName));
        }

        return matchedEnvelopeValues;
    }

    /**
     * Returns the from.
     * @return String
     */
    public String getEnvelopeFrom()
    {
        return fieldEnvelopeFrom;
    }

    /**
     * Returns the recipient.
     * @return String
     */
    public String getEnvelopeTo()
    {
        return fieldEnvelopeTo;
    }

    /**
     * Sets the from.
     * @param from The from to set
     */
    public void setEnvelopeFrom(String from)
    {
        fieldEnvelopeFrom = from;
    }

    /**
     * Sets the recipient.
     * @param recipient The recipient to set
     */
    public void setEnvelopeTo(String recipient)
    {
        fieldEnvelopeTo = recipient;
    }

}
