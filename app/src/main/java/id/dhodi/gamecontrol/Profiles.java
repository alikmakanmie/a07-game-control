package id.dhodi.gamecontrol;

import java.util.LinkedHashMap;
import java.util.Map;

public class Profiles {
    // pkg -> [cpu, gpu, fps]
    public static Map<String, int[]> load() {
        Map<String, int[]> map = new LinkedHashMap<>();
        String conf = Su.readConf();
        for (String line : conf.split("\n")) {
            line = line.trim();
            if (line.isEmpty() || !line.contains("|")) continue;
            String[] f = line.split("\\|");
            try {
                map.put(f[0], new int[]{Integer.parseInt(f[1]), Integer.parseInt(f[2]), Integer.parseInt(f[3])});
            } catch (Exception ignored) {}
        }
        return map;
    }

    public static void save(Map<String, int[]> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, int[]> e : map.entrySet()) {
            int[] v = e.getValue();
            sb.append(e.getKey()).append("|").append(v[0]).append("|").append(v[1]).append("|").append(v[2]).append("\n");
        }
        Su.writeConf(sb.toString());
    }
}
