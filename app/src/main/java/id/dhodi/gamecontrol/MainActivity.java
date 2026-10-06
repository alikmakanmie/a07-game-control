package id.dhodi.gamecontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    LinearLayout listBox;
    TextView engineStatus;
    Switch engineSwitch;
    Map<String, int[]> profiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(Ui.BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(Ui.dp(this, 20), Ui.dp(this, 24), Ui.dp(this, 20), Ui.dp(this, 20));

        root.addView(Ui.text(this, "Game Control +", 27, Ui.WHITE, true));
        TextView sub = Ui.text(this, "Kontrol CPU, GPU, dan FPS per aplikasi", 14, Ui.SUB, false);
        LinearLayout.LayoutParams subP = new LinearLayout.LayoutParams(-1, -2);
        subP.topMargin = Ui.dp(this, 4);
        root.addView(sub, subP);

        // Kartu mesin
        LinearLayout eng = new LinearLayout(this);
        eng.setOrientation(LinearLayout.HORIZONTAL);
        eng.setGravity(Gravity.CENTER_VERTICAL);
        eng.setBackground(Ui.card(this, Ui.CARD));
        eng.setPadding(Ui.dp(this, 18), Ui.dp(this, 16), Ui.dp(this, 18), Ui.dp(this, 16));
        LinearLayout.LayoutParams engP = new LinearLayout.LayoutParams(-1, -2);
        engP.topMargin = Ui.dp(this, 20);
        LinearLayout engTxt = new LinearLayout(this);
        engTxt.setOrientation(LinearLayout.VERTICAL);
        engTxt.addView(Ui.text(this, "Mesin kontrol", 16, Ui.WHITE, true));
        engineStatus = Ui.text(this, "Memeriksa…", 13, Ui.SUB, false);
        engTxt.addView(engineStatus);
        eng.addView(engTxt, new LinearLayout.LayoutParams(0, -2, 1f));
        engineSwitch = new Switch(this);
        engineSwitch.setOnCheckedChangeListener((v, checked) -> new Thread(() -> {
            Su.run(checked ? "rm -f " + Su.OFF : "touch " + Su.OFF);
            runOnUiThread(() -> engineStatus.setText(checked ? "Aktif — profil diterapkan otomatis" : "Nonaktif"));
        }).start());
        eng.addView(engineSwitch);
        root.addView(eng, engP);

        TextView logBtn = Ui.text(this, "Lihat catatan mesin", 14, Ui.BLUE, false);
        logBtn.setPadding(0, Ui.dp(this, 14), 0, 0);
        logBtn.setOnClickListener(v -> new Thread(() -> {
            String log = Su.run("tail -n 40 " + Su.LOGF + " 2>/dev/null");
            if (log.trim().isEmpty()) log = "(belum ada catatan — mesin mulai sesudah restart dengan module terpasang)";
            final String f = log;
            runOnUiThread(() -> {
                TextView tv = Ui.text(this, f, 11, Ui.WHITE, false);
                tv.setPadding(Ui.dp(this, 16), Ui.dp(this, 16), Ui.dp(this, 16), Ui.dp(this, 16));
                ScrollView sv = new ScrollView(this);
                sv.addView(tv);
                new AlertDialog.Builder(this).setTitle("Catatan mesin").setView(sv).setPositiveButton("Tutup", null).show();
            });
        }).start());
        root.addView(logBtn);

        TextView sec = Ui.text(this, "Aplikasi", 18, Ui.WHITE, true);
        LinearLayout.LayoutParams secP = new LinearLayout.LayoutParams(-1, -2);
        secP.topMargin = Ui.dp(this, 22);
        secP.bottomMargin = Ui.dp(this, 8);
        root.addView(sec, secP);

        listBox = new LinearLayout(this);
        listBox.setOrientation(LinearLayout.VERTICAL);
        root.addView(listBox);

        ScrollView sv = new ScrollView(this);
        sv.addView(root);
        setContentView(sv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            profiles = Profiles.load();
            boolean installed = Su.moduleInstalled();
            boolean off = Su.run("test -f " + Su.OFF + " && echo OFF").contains("OFF");
            runOnUiThread(() -> {
                engineSwitch.setChecked(!off);
                engineStatus.setText(!installed ? "Module belum terpasang" : (off ? "Nonaktif" : "Aktif — profil diterapkan otomatis"));
                buildList(installed);
            });
        }).start();
    }

    void buildList(boolean installed) {
        listBox.removeAllViews();
        if (!installed) {
            TextView warn = Ui.text(this, "Module A07 Game Control+ belum terpasang. Pasang module-nya lewat KernelSU Manager lalu restart — aplikasi ini adalah pengaturnya.", 14, 0xFFFFB74D, false);
            warn.setPadding(0, Ui.dp(this, 10), 0, Ui.dp(this, 10));
            listBox.addView(warn);
        }
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> res = pm.queryIntentActivities(intent, 0);
        List<Object[]> apps = new ArrayList<>();
        for (ResolveInfo ri : res) {
            String pkg = ri.activityInfo.packageName;
            String label = ri.loadLabel(pm).toString();
            apps.add(new Object[]{label, pkg});
        }
        Collections.sort(apps, new Comparator<Object[]>() {
            public int compare(Object[] a, Object[] b) {
                return ((String) a[0]).toLowerCase().compareTo(((String) b[0]).toLowerCase());
            }
        });
        for (Object[] app : apps) {
            final String label = (String) app[0];
            final String pkg = (String) app[1];
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackground(Ui.card(this, Ui.CARD));
            row.setPadding(Ui.dp(this, 14), Ui.dp(this, 12), Ui.dp(this, 14), Ui.dp(this, 12));
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1, -2);
            rp.bottomMargin = Ui.dp(this, 10);
            ImageView iv = new ImageView(this);
            try {
                Drawable d = pm.getApplicationIcon(pkg);
                iv.setImageDrawable(d);
            } catch (Exception ignored) {}
            row.addView(iv, new LinearLayout.LayoutParams(Ui.dp(this, 42), Ui.dp(this, 42)));
            LinearLayout tx = new LinearLayout(this);
            tx.setOrientation(LinearLayout.VERTICAL);
            tx.setPadding(Ui.dp(this, 12), 0, 0, 0);
            tx.addView(Ui.text(this, label, 15, Ui.WHITE, false));
            tx.addView(Ui.text(this, pkg, 11, Ui.SUB, false));
            row.addView(tx, new LinearLayout.LayoutParams(0, -2, 1f));
            int[] prof = profiles == null ? null : profiles.get(pkg);
            if (prof != null) {
                String fps = prof[2] == 0 ? "FPS maks" : "FPS " + prof[2];
                TextView chip = Ui.text(this, "CPU " + prof[0] + "% · GPU " + prof[1] + "%\n" + fps, 11, Ui.BLUE, true);
                chip.setGravity(Gravity.RIGHT);
                row.addView(chip);
            }
            row.setOnClickListener(v -> {
                Intent i = new Intent(this, ProfileActivity.class);
                i.putExtra("pkg", pkg);
                i.putExtra("label", label);
                startActivity(i);
            });
            listBox.addView(row, rp);
        }
    }
}
