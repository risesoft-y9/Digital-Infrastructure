package net.risesoft.liquibase;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

public class VersionComparator implements Comparator<String> {

    private String extractVersion(String filename) {
        String result = StringUtils.substringAfterLast(filename, "/");
        return StringUtils.substringBefore(result, ".xml");
    }

    @Override
    public int compare(String f1, String f2) {
        System.out.println(f1);
        System.out.println(f2);
        String v1 = extractVersion(f1);
        String v2 = extractVersion(f2);

        String[] p1 = v1.split("\\.");
        String[] p2 = v2.split("\\.");

        int len = Math.max(p1.length, p2.length);

        for (int i = 0; i < len; i++) {
            int n1 = i < p1.length ? Integer.parseInt(p1[i]) : 0;
            int n2 = i < p2.length ? Integer.parseInt(p2[i]) : 0;

            if (n1 != n2) {
                return Integer.compare(n1, n2);
            }
        }
        return 0;
    }
}
