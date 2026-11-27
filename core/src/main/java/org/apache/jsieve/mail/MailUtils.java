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

package org.apache.jsieve.mail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.jsieve.comparators.AsciiCasemap;
import org.apache.jsieve.exception.SieveException;

/**
 * Class MailUtils implements utility methods that are useful when processing
 * Sieve mail.
 */
public class MailUtils {

    /**
     * Constructor for MailUtils.
     */
    protected MailUtils() {
        super();
    }

    /**
     * Charges the evaluation budget.
     */
    @FunctionalInterface
    public interface Charge {
        /**
         * @param cost not negative
         * @throws SieveException when the budget is exhausted
         */
        void charge(long cost) throws SieveException;
    }

    /**
     * A lookup run by {@link #lookUp(HeaderLookups, Lookup)}.
     */
    @FunctionalInterface
    public interface Lookup<T> {
        T get() throws SieveException;
    }

    /**
     * The header lookups of one evaluation: what they fetch beyond the values
     * they answer is charged to its budget, and whether the getHeader of an
     * adapter that does not declare it ignores case is probed at most once.
     * Like the context of the evaluation, it must not be shared between
     * threads.
     */
    public static final class HeaderLookups {
        private final Charge charge;
        private Boolean getHeaderIgnoresCase;
        // No probe could tell: the mail holds every case spelling of every name
        private boolean undecided;
        private SieveException exhausted;

        /**
         * @param charge charges the budget of the evaluation, not null
         */
        public HeaderLookups(Charge charge) {
            this.charge = charge;
        }
    }

    private static final ThreadLocal<HeaderLookups> CURRENT_LOOKUPS = new ThreadLocal<HeaderLookups>();

    /**
     * Runs a lookup of the adapter, such as {@link MailAdapter#getMatchingHeader(String)},
     * on behalf of the given evaluation: the calls it makes to
     * {@link #getMatchingHeader(MailAdapter, String)} charge its budget and
     * share what they learnt of the adapter. When the budget is exhausted
     * within the lookup, its exception is thrown, whatever the adapter did
     * with it.
     *
     * @throws SieveException when the budget is exhausted or the mail cannot be read
     */
    public static <T> T lookUp(HeaderLookups lookups, Lookup<T> lookup) throws SieveException {
        final HeaderLookups previous = CURRENT_LOOKUPS.get();
        CURRENT_LOOKUPS.set(lookups);
        try {
            final T result = lookup.get();
            if (lookups.exhausted != null) {
                throw lookups.exhausted;
            }
            return result;
        } catch (SieveException | RuntimeException e) {
            if (lookups.exhausted != null) {
                throw lookups.exhausted;
            }
            throw e;
        } finally {
            if (previous == null) {
                CURRENT_LOOKUPS.remove();
            } else {
                CURRENT_LOOKUPS.set(previous);
            }
        }
    }

    /**
     * Carries the exhaustion of the budget through the adapter, whose
     * getMatchingHeader only throws SieveMailException.
     */
    private static final class BudgetExhausted extends SieveMailException {
        private static final long serialVersionUID = 1L;

        BudgetExhausted(SieveException budgetException) {
            super(budgetException.getMessage());
        }
    }

    private static void charge(HeaderLookups lookups, List<String> values) throws SieveMailException {
        if (lookups == null) {
            return;
        }
        long cost = 0;
        for (String value : values) {
            cost += value.length() + 1L;
        }
        if (lookups.exhausted == null) {
            try {
                lookups.charge.charge(cost);
                return;
            } catch (SieveException e) {
                lookups.exhausted = e;
            }
        }
        throw new BudgetExhausted(lookups.exhausted);
    }

    /**
     * <p>
     * Method getMatchingHeader answers a List of all of the headers in the mail
     * with the passed name. If no headers are found an empty List is returned.
     * </p>
     * 
     * <p>
     * This method differs from MailAdapter.getHeader(String) in that it ignores
     * case and whitespace prefixes and suffixes to a header name when
     * performing the match, as required by RFC 3028. Thus "From", "from ", "
     * From" and " from " are considered equal.
     * </p>
     * 
     * <p>
     * Run within {@link #lookUp(HeaderLookups, Lookup)}, the values it fetches
     * beyond those it answers are charged to the budget of the evaluation.
     * </p>
     * 
     * @param name
     * @return List
     * @throws SieveMailException
     */
    static public List<String> getMatchingHeader(MailAdapter mail, String name)
            throws SieveMailException {
        // Header names are compared with i;ascii-casemap (RFC 5228 section 2.7)
        final String asciiUpperCaseName = AsciiCasemap.toAsciiUpperCase(name);
        final List<String> headerNames = mail.getHeaderNames();
        final List<String> matchingNames = new ArrayList<String>();
        for (String headerName: headerNames) {
            if (AsciiCasemap.toAsciiUpperCase(headerName.trim()).equals(asciiUpperCaseName))
                matchingNames.add(headerName);
        }
        final List<String> matchedHeaderValues = new ArrayList<String>(32);
        if (matchingNames.isEmpty()) {
            return matchedHeaderValues;
        }
        if (matchingNames.size() == 1) {
            matchedHeaderValues.addAll(mail.getHeader(matchingNames.get(0)));
            return matchedHeaderValues;
        }
        final HeaderLookups lookups = CURRENT_LOOKUPS.get();
        final String probedFamily = AsciiCasemap.toAsciiUpperCase(matchingNames.get(0));
        Boolean ignoresCase = mail.getHeaderIgnoresCase()
            .orElse(lookups == null ? null : lookups.getHeaderIgnoresCase);
        if (ignoresCase == null && lookups != null && lookups.undecided) {
            return lookUpEachSpelling(mail, matchingNames, lookups);
        }
        List<String> probedValues = null;
        if (ignoresCase == null) {
            final Set<String> names = new HashSet<String>(headerNames);
            probedValues = probeFamily(mail, headerNames, names, probedFamily);
            if (probedValues == null) {
                ignoresCase = probeOtherFamilies(mail, headerNames, names, probedFamily, lookups);
            } else {
                ignoresCase = !probedValues.isEmpty();
            }
            if (ignoresCase == null && lookups != null) {
                return lookUpEachSpelling(mail, matchingNames, lookups);
            }
            if (ignoresCase == null) {
                // Out of an evaluation, nothing bounds asking every spelling
                ignoresCase = true;
            } else if (lookups != null) {
                lookups.getHeaderIgnoresCase = ignoresCase;
            }
        }
        // Spellings differing in their whitespace are asked each, whatever the adapter
        final Set<String> askedSpellings = new HashSet<String>();
        for (String headerName : matchingNames) {
            final String family = AsciiCasemap.toAsciiUpperCase(headerName);
            if (!ignoresCase) {
                matchedHeaderValues.addAll(mail.getHeader(headerName));
            } else if (askedSpellings.add(family)) {
                if (probedValues != null && family.equals(probedFamily)) {
                    matchedHeaderValues.addAll(probedValues);
                } else {
                    matchedHeaderValues.addAll(mail.getHeader(headerName));
                }
            }
        }
        return matchedHeaderValues;
    }

    /**
     * Tells, for an adapter that does not declare it, whether its getHeader
     * ignores the case of the name, as the one of JavaMail does: then each
     * spelling of a name answers the values of all of them, and asking each
     * of k spellings would answer k times k values. The adapter is asked a
     * case spelling of the given family that the mail does not hold: an
     * adapter matching the exact name answers no value for it, one ignoring
     * case answers the values of the family, which the lookup answers and
     * then does not ask again. Finding the absent spelling tries at most as
     * many spellings as the family has members, so this is linear in the
     * header names.
     *
     * @return the values answered for the absent spelling, or null when the
     *         mail holds every case spelling of the family
     */
    private static List<String> probeFamily(MailAdapter mail, List<String> headerNames, Set<String> names,
            String family) throws SieveMailException {
        int members = 0;
        for (String headerName : headerNames) {
            if (AsciiCasemap.toAsciiUpperCase(headerName).equals(family)) {
                members++;
            }
        }
        final String absent = absentSpelling(family, members, names);
        return absent == null ? null : mail.getHeader(absent);
    }

    /**
     * Tells whether the getHeader of an adapter that does not declare it
     * ignores case, when the mail holds every case spelling of the name looked
     * up, which no mail but a crafted one does: an absent spelling of the
     * first other family that has one is asked. Its values are not those of
     * the name looked up: they are charged, and the answer is kept for the
     * other lookups of the evaluation.
     *
     * @return whether getHeader ignores case, or null when the mail holds
     *         every case spelling of every name
     */
    private static Boolean probeOtherFamilies(MailAdapter mail, List<String> headerNames, Set<String> names,
            String probedFamily, HeaderLookups lookups) throws SieveMailException {
        final Map<String, Integer> families = new LinkedHashMap<String, Integer>();
        for (String headerName : headerNames) {
            families.merge(AsciiCasemap.toAsciiUpperCase(headerName), 1, Integer::sum);
        }
        families.remove(probedFamily);
        for (Map.Entry<String, Integer> family : families.entrySet()) {
            final String absent = absentSpelling(family.getKey(), family.getValue(), names);
            if (absent != null) {
                final List<String> values = mail.getHeader(absent);
                charge(lookups, values);
                return !values.isEmpty();
            }
        }
        return null;
    }

    /**
     * Looks up the values of every spelling, when the mail holds every case
     * spelling of every name and no probe can tell whether the getHeader of
     * the adapter ignores case. Every spelling answering the values another
     * spelling of its family answered is charged as it is: an adapter ignoring
     * case answers the values of the whole family to each of its k spellings.
     * The other values, answered, are charged by the evaluation. Two spellings of a family answering different
     * values prove the adapter matches the exact name: the values of every
     * spelling are answered. When every spelling of each family answers the
     * same values, both adapters answer each value: those of one spelling of
     * each family are answered, and an adapter matching the exact name loses
     * only their repetitions, and the other lookups of the evaluation ask
     * every spelling without probing again.
     */
    private static List<String> lookUpEachSpelling(MailAdapter mail, List<String> matchingNames,
            HeaderLookups lookups) throws SieveMailException {
        final Map<String, List<String>> familyValues = new LinkedHashMap<String, List<String>>();
        final List<List<String>> spellingValues = new ArrayList<List<String>>();
        boolean exactName = false;
        for (String headerName : matchingNames) {
            final List<String> values = mail.getHeader(headerName);
            final List<String> first = familyValues.putIfAbsent(AsciiCasemap.toAsciiUpperCase(headerName), values);
            if (first != null && first.equals(values)) {
                charge(lookups, values);
                // Kept once: the repetitions of an adapter ignoring case hold no memory
                spellingValues.add(first);
            } else {
                exactName |= first != null;
                spellingValues.add(values);
            }
        }
        if (exactName) {
            lookups.getHeaderIgnoresCase = false;
        } else {
            // The other lookups of the evaluation do not probe again
            lookups.undecided = true;
        }
        final List<String> matchedHeaderValues = new ArrayList<String>(32);
        for (List<String> values : exactName ? spellingValues : familyValues.values()) {
            matchedHeaderValues.addAll(values);
        }
        return matchedHeaderValues;
    }

    /**
     * A spelling of the given upper case name, differing in the case of its
     * ASCII letters, that is not among the names, or null when the given
     * number of members of the family already holds all of them.
     */
    private static String absentSpelling(String upperCaseName, int members, Set<String> names) {
        final List<Integer> letters = new ArrayList<Integer>();
        for (int i = 0; i < upperCaseName.length(); i++) {
            final char c = upperCaseName.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                letters.add(i);
            }
        }
        if (letters.size() < 31 && (1 << letters.size()) <= members) {
            return null;
        }
        // members + 1 distinct spellings: at least one of them is absent
        final char[] spelling = upperCaseName.toCharArray();
        for (int mask = 0; mask <= members; mask++) {
            for (int bit = 0; bit < letters.size(); bit++) {
                final int index = letters.get(bit);
                final boolean lower = bit < 31 && (mask & (1 << bit)) != 0;
                spelling[index] = lower ? Character.toLowerCase(upperCaseName.charAt(index)) : upperCaseName.charAt(index);
            }
            final String candidate = new String(spelling);
            if (!names.contains(candidate)) {
                return candidate;
            }
        }
        return null;
    }
}
