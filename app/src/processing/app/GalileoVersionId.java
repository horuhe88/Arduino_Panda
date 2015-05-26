/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

package processing.app;

import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.charset.Charset;
import java.io.UnsupportedEncodingException;

public class GalileoVersionId implements Comparable<GalileoVersionId> {

    static Charset utf8 = Charset.forName("UTF-8");
    static Integer zero = new Integer(0);
    static Integer[] empty = { };
    static String quarkCodeline = "!Quark";

    private String codeline;
    private List<Integer> release;
    private Integer build;

    public GalileoVersionId(String codeline, Integer[] release, Integer build) {
        this.codeline = new String(codeline.getBytes(utf8));
        this.release = Arrays.asList(release);
        this.build = build;
    }

    private String toStringAux(Boolean useCanonical) {
        StringBuilder s = new StringBuilder();

        Boolean codelined = (this.codeline.length() > 0);
        Boolean identified = (this.release.size() > 0);
        Boolean serialized = !this.build.equals(zero);

        if (!useCanonical && this.codeline.equals(quarkCodeline)) {
            codelined = false;
            serialized = !identified;
        }

        if (codelined) {
            s.append(this.codeline);

            if (identified) {
                s.append('-');
            }
        }

        Boolean point = false;
        for (Integer n : this.release) {
            if (point) s.append('.');
            s.append(n.toString());
            point = true;
        }

        if (serialized) {
            if (identified || codelined) {
                s.append('+');
            }

            s.append(this.build.toString());
        }

        return s.toString();
    }

    public String toPresentationString() {
        return this.toStringAux(false);
    }

    public String toCanonicalString() {
        return this.toStringAux(true);
    }

    static public Boolean isReleasedQuarkBuild(String version) {
        Boolean matched = version.matches("^0[Xx][0-9A-Fa-f]+$");

        if (matched) {
            int n = Integer.parseInt(version.substring(2), 16);
            if (n < 0x80000) {
                matched = false;
            }
        }

        return matched;
    }

    static public Boolean isUnreleasedQuarkBuild(String version) {
        Boolean matched = version.matches("^[1-9][0-9]*$");

        if (!matched) {
            matched = version.matches("^0[Xx][0-9A-Fa-f]+$");

            if (matched) {
                int n = Integer.parseInt(version.substring(2), 16);
                if (n >= 0x80000) {
                    matched = false;
                }
            }
        }

        return matched;
    }

    static public Boolean isCanonicalFormat(String version) {
        Pattern p;
        Matcher m;
        Boolean hasReleaseArray;
        Boolean hasBuildNumber;

        if (version == null || version.length() < 1 || version.length() > 63) {
            return false;
        }

        p = Pattern.compile("(!?[A-Z][A-Za-z0-9_]*)([-+]?)");
        m = p.matcher(version);
        if (!m.lookingAt()) {
            return false;
        }

        version = version.substring(m.end());
        String conjunct = m.group(1);
        hasReleaseArray = conjunct.equals("-");
        hasBuildNumber = conjunct.equals("+");

        if (hasReleaseArray) {
            p = Pattern.compile("((0|[1-9][0-9]*)(\\.(0|[1-9][0-9]*)))(\\+?)");
            m = p.matcher(version);
            if (!m.lookingAt()) {
                return false;
            }

            version = version.substring(m.end());
            conjunct = m.group(4);
            hasBuildNumber = conjunct.equals("+");
        }

        return !hasBuildNumber || version.matches("0|[1-9][0-9]*");
    }

    static public String parseCodeline(String version) {
        int i = version.indexOf("-");
        if (i < 0) {
            i = version.indexOf("+");
            if (i < 0) {
                return "";
            }
        }

        return version.substring(0, i);
    }

    static public Integer parseBuildNumber(String version) {
        int i = version.lastIndexOf("+");
        if (i < 0) {
            return zero;
        }

        try {
            String s = version.substring(i + 1);
            return Integer.parseInt(s, 10);
        }
        catch (NumberFormatException _) {
            throw new IllegalArgumentException(version);
        }
    }

    static public Integer[] parseReleaseArray(String version) {
        int i = version.indexOf("-");
        i = (i >= 0) ? i + 1 : 0;

        int j = version.lastIndexOf("+");
        j = (j >= 0) ? j : version.length();

        Vector<Integer> v = new Vector<Integer>();
        if (i < j) {
            String code = version.substring(i, j);
            for (String part : code.split("\\.")) {
                Integer n;

                try {
                    n = Integer.parseInt(part, 10);
                }
                catch (NumberFormatException _) {
                    throw new IllegalArgumentException("part '" + part + "' not valid.");
                }

                v.add(n);
            }
        }

        return v.toArray(empty);
    }

    static public GalileoVersionId ofTargetString(String version) {
        if (GalileoVersionId.isUnreleasedQuarkBuild(version)) {
            String codeline = quarkCodeline;
            Integer build;
            Integer[] release = empty;

            if (version.startsWith("0x") || version.startsWith("0X")) {
                build = Integer.parseInt(version.substring(2), 16);
            }
            else {
                build = Integer.parseInt(version, 10);
            }

            return new GalileoVersionId(codeline, release, build);
        }
        else if (GalileoVersionId.isReleasedQuarkBuild(version)) {
            String codeline = quarkCodeline;

            int n = Integer.parseInt(version.substring(2), 16);

            Integer build = Integer.valueOf(n & 0xff);
            n >>= 8;

            Vector<Integer> v = new Vector<Integer>();
            for (int i = 0; i < 3; ++i) {
                v.add(0, Integer.valueOf(n & 0xff));
                n >>= 8;
            }

            Integer[] release = v.toArray(empty);

            return new GalileoVersionId(codeline, release, build);
        }

        return ofString(version);
    }

    static public GalileoVersionId ofString(String version) {
        if (!GalileoVersionId.isCanonicalFormat(version)) {
            throw new IllegalArgumentException(version);
        }

        String codeline = GalileoVersionId.parseCodeline(version);
        Integer[] release = GalileoVersionId.parseReleaseArray(version);
        Integer build = GalileoVersionId.parseBuildNumber(version);

        return new GalileoVersionId(codeline, release, build);
    }

    public int compareTo(GalileoVersionId that) {
        Iterator<Integer> thisRelease = this.release.listIterator();
        Iterator<Integer> thatRelease = that.release.listIterator();

        if (this.codeline.equals(quarkCodeline) && that.codeline.equals(quarkCodeline)) {
            Boolean thisHasNext = thisRelease.hasNext();
            Boolean thatHasNext = thatRelease.hasNext();

            if (thisHasNext != thatHasNext)
                throw new IllegalArgumentException();
        }

        while (thisRelease.hasNext() && thatRelease.hasNext()) {
            Integer thisPart = thisRelease.next();
            Integer thatPart = thatRelease.next();
            int d = thisPart.compareTo(thatPart);
            if (d != 0) {
                return d;
            }
        }

        if (thisRelease.hasNext()) {
            return -1;
        }

        if (thatRelease.hasNext()) {
            return 1;
        }

        int d = this.codeline.compareTo(that.codeline);
        if (d != 0) {
            return d;
        }

        return this.build.compareTo(that.build);
    }
};

//--- End of file
