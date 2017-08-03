/*
 * #%L
 * Cantharella :: Utils
 * $Id: IntuitiveStringComparator.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/main/java/nc/ird/cantharella/utils/IntuitiveStringComparator.java $
 * %%
 * Copyright (C) 2009 - 2012 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package nc.ird.cantharella.utils;

import java.util.Comparator;

/**
 * <p>
 * A comparator that emulates the "intuitive" sorting used by Windows Explorer. The rules are as follows:
 * </p>
 * <ul>
 * <li>Any sequence of one or more digits is treated as an atomic unit, a number. When these number units are matched
 * up, they're compared according to their respective numeric values. If they're numerically equal, but one has more
 * leading zeroes than the other, the longer sequence is considered larger.</li>
 * <li>Numbers always sort before any other kind of character.</li>
 * <li>Spaces and all punctuation characters always sort before letters.</li>
 * <li>Letters are sorted case-insensitively.</li>
 * </ul>
 * <p>
 * Explorer's sort order for punctuation characters is not quite the same as their ASCII order. Also, some characters
 * aren't allowed in file names, so I don't know how they would be sorted. This class just sorts them all according to
 * their ASCII values.
 * </p>
 * <p>
 * This comparator is only guaranteed to work with 7-bit ASCII strings.
 * </p>
 * 
 * @author Alan Moore
 * @param <T> generic type
 */
public class IntuitiveStringComparator<T extends CharSequence> implements Comparator<T> {

    /** string 1 */
    private T str1;

    /** string 2 */
    private T str2;

    /** posistion 1 */
    private int pos1;

    /** position 2 */
    private int pos2;

    /** lenght 1 */
    private int len1;

    /** length 2 */
    private int len2;

    /** {@inheritDoc} */
    @Override
    public int compare(T s1, T s2) {
        str1 = s1;
        str2 = s2;
        len1 = str1.length();
        len2 = str2.length();
        pos1 = pos2 = 0;

        if (len1 == 0) {
            return len2 == 0 ? 0 : -1;
        } else if (len2 == 0) {
            return 1;
        }

        while (pos1 < len1 && pos2 < len2) {
            char ch1 = str1.charAt(pos1);
            char ch2 = str2.charAt(pos2);
            int result = 0;

            if (Character.isDigit(ch1)) {
                result = Character.isDigit(ch2) ? compareNumbers() : -1;
            } else if (Character.isLetter(ch1)) {
                result = Character.isLetter(ch2) ? compareOther(true) : 1;
            } else {
                result = Character.isDigit(ch2) ? 1 : Character.isLetter(ch2) ? -1 : compareOther(false);
            }

            if (result != 0) {
                return result;
            }
        }

        return len1 - len2;
    }

    /**
     * Compare numbers
     * 
     * @return the integer response
     */
    private int compareNumbers() {
        int delta = 0;
        int zeroes1 = 0, zeroes2 = 0;
        char ch1 = (char) 0, ch2 = (char) 0;

        // Skip leading zeroes, but keep a count of them.
        while (pos1 < len1 && (ch1 = str1.charAt(pos1++)) == '0') {
            zeroes1++;
        }
        while (pos2 < len2 && (ch2 = str2.charAt(pos2++)) == '0') {
            zeroes2++;
        }

        // If one sequence contains more significant digits than the
        // other, it's a larger number. In case they turn out to have
        // equal lengths, we compare digits at each position; the first
        // unequal pair determines which is the bigger number.
        while (true) {
            boolean noMoreDigits1 = (ch1 == 0) || !Character.isDigit(ch1);
            boolean noMoreDigits2 = (ch2 == 0) || !Character.isDigit(ch2);

            if (noMoreDigits1 && noMoreDigits2) {
                return delta != 0 ? delta : zeroes1 - zeroes2;
            } else if (noMoreDigits1) {
                return -1;
            } else if (noMoreDigits2) {
                return 1;
            } else if (delta == 0 && ch1 != ch2) {
                delta = ch1 - ch2;
            }

            ch1 = pos1 < len1 ? str1.charAt(pos1++) : (char) 0;
            ch2 = pos2 < len2 ? str2.charAt(pos2++) : (char) 0;
        }
    }

    /**
     * Compare other characters
     * 
     * @param isLetters true if characters are letters
     * @return the integer response
     */
    private int compareOther(boolean isLetters) {
        char ch1 = str1.charAt(pos1++);
        char ch2 = str2.charAt(pos2++);

        if (ch1 == ch2) {
            return 0;
        }

        if (isLetters) {
            ch1 = Character.toUpperCase(ch1);
            ch2 = Character.toUpperCase(ch2);
            if (ch1 != ch2) {
                ch1 = Character.toLowerCase(ch1);
                ch2 = Character.toLowerCase(ch2);
            }
        }

        return ch1 - ch2;
    }
}