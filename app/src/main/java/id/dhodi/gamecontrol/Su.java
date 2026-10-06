package id.dhodi.gamecontrol;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class Su {
    public static final String MODDIR = "/data/adb/modules/a07gamecontrol";
    public static final String CONF = MODDIR + "/profiles.conf";
    public static final String OFF = MODDIR + "/off";
    public static final String LOGF = MODDIR + "/gamecontrol.log";

    public static String run(String... cmds) {
        StringBuilder out = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream in = new DataOutputStream(p.getOutputStream());
            for (String c : cmds) { in.writeBytes(c + "\n"); }
            in.writeBytes("exit\n");
            in.flush();
            in.close();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = r.readLine()) != null) out.append(line).append("\n");
            p.waitFor();
        } catch (Exception e) {
            return "";
        }
        return out.toString();
    }

    public static boolean moduleInstalled() {
        return run("test -d " + MODDIR + " && echo ADA").contains("ADA");
    }

    public static String readConf() {
        return run("cat " + CONF + " 2>/dev/null");
    }

    public static void writeConf(String content) {
        String cmd = "cat > " + CONF + " <<'DHODI_EOF'\n" + content + "DHODI_EOF";
        run(cmd);
    }
}
