package com.abctech.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final Log logger = LogFactory.getLog(Utils.class);

    public static final  int     TIME_SEC_THREE        = 3000;
    public static final  long    TIME_ONE_HOUR         = 60 * 60 * 1000L;
    public static final  int     HOUR_PER_DAY          = 24;
    private static final Pattern PLAIN_TEXT_TO_ESCAPE  = Pattern.compile("[<>&]| {2,}|\r?\n");
    private static final Pattern NON_CHARACTER_PATTERN = Pattern.compile("([^\\d])");

    /**
     * @param text
     * @return string
     */
    public static String escapeCharacterToDisplay(String text) {
        Pattern pattern = PLAIN_TEXT_TO_ESCAPE;
        Matcher match = pattern.matcher(text);

        if (match.find()) {
            StringBuilder out = new StringBuilder();
            int end = 0;
            do {
                int start = match.start();
                out.append(text.substring(end, start));
                end = match.end();
                int c = text.codePointAt(start);
                if (c == ' ') {
                    // Escape successive spaces into series of "&nbsp;".
                    for (int i = 1, n = end - start; i < n; ++i) {
                        out.append("&nbsp;");
                    }
                    out.append(' ');
                } else if (c == '\r' || c == '\n') {
                    out.append("<br>");
                } else if (c == '<') {
                    out.append("&lt;");
                } else if (c == '>') {
                    out.append("&gt;");
                } else if (c == '&') {
                    out.append("&amp;");
                }
            } while (match.find());
            out.append(text.substring(end));
            text = out.toString();
        }
        return text;
    }

    /**
     * @param orgString
     * @return ������ ���ŵ� ����
     */
    public static String getPureString(final String orgString) {
        if (orgString == null)
            return "";

        StringBuffer destStringBuffer = new StringBuffer();
        Matcher m = NON_CHARACTER_PATTERN.matcher(orgString);

        while (m.find()) {
            m.appendReplacement(destStringBuffer, "");
        }
        m.appendTail(destStringBuffer);
        return destStringBuffer.toString().toLowerCase();
    }

    public static long getFileSize(final String path) {
        File oFile = new File(path);
        long len = -1;
        if (oFile.exists()) {
            len = oFile.length();
        }
        return len;
    }

    public static boolean isValidEmail(String inputStr) {
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(inputStr);
        if (!m.matches()) {
            return false;
        }
        return true;
    }

    public static byte[] getHash(String password) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            return digest.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
        if (c != null && string != null) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static boolean isExists(String target, Collection<? extends Object> list) {
        if ((list == null) || (list.size() == 0) || target == null) {
            return false;
        }
        for (Object obj : list) {
            if (obj.equals(target))
                return true;
        }
        return false;
    }

    public static String makeRandomPassword() {
        String arr = "0123456789";
        Random random = new Random(System.currentTimeMillis());
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int pos = random.nextInt(arr.length());
            builder.append(arr.charAt(pos));
        }
        return builder.toString();
    }

    public static String getContentType(String fileName) {
        return URLConnection.guessContentTypeFromName(fileName);
    }

    public static long getStartDateOfWeek(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long getEndDateOfWeek(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_WEEK, 6);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }

    public static long getStartTimeOfToday(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long getEndTimeOfToday(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }
}
