package common;

import java.util.*;

public class Utils {
    // Parse "COMMAND|k=v;k2=v2" -> Map of params
    public static Map<String,String> parseParams(String paramStr) {
        Map<String,String> map = new HashMap<>();
        if (paramStr == null || paramStr.isEmpty()) return map;
        String[] pairs = paramStr.split(";");
        for (String p : pairs) {
            int idx = p.indexOf('=');
            if (idx > 0) {
                String k = p.substring(0, idx);
                String v = p.substring(idx+1);
                map.put(k, v);
            }
        }
        return map;
    }

    // Build param string from map
    public static String buildParams(Map<String,String> map) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (var e : map.entrySet()) {
            if (!first) sb.append(";");
            sb.append(e.getKey()).append("=").append(e.getValue());
            first = false;
        }
        return sb.toString();
    }
}
