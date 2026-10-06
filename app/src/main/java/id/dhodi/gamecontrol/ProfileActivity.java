package id.dhodi.gamecontrol;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class ProfileActivity extends Activity {
    String pkg;
    Map<String, int[]> profiles;
    Switch swActive;
    SeekBar sbCpu, sbGpu;
    TextView tvCpu, tvGpu;
    RadioGroup rgFps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(Ui.BG);
        pkg = getIntent().getStringExtra("pkg");
        String label = getIntent().getStringExtra("label");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(Ui.dp(this, 20), Ui.dp(this, 24), Ui.dp(this, 20), Ui.dp(this, 20));

        LinearLayout head = new LinearLayout(this);
        head.setOrientation(LinearLayout.HORIZONTAL);
        head.setGravity(Gravity.CENTER_VERTICAL);
        ImageView iv = new ImageView(this);
        try {
            Drawable d = getPackageManager().getApplicationIcon(pkg);
            iv.setImageDrawable(d);
        } catch (PackageManager.NameNotFoundException ignored) {}
        head.addView(iv, new LinearLayout.LayoutParams(Ui.dp(this, 48), Ui.dp(this, 48)));
        LinearLayout ht = new LinearLayout(this);
        ht.setOrientation(LinearLayout.VERTICAL);
        ht.setPadding(Ui.dp(this, 12), 0, 0, 0);
        ht.addView(Ui.text(this, label, 20, Ui.WHITE, true));
        ht.addView(Ui.text(this, pkg, 12, Ui.SUB, false));
        head.addView(ht);
        root.addView(head);

        // Kartu aktif
        LinearLayout card1 = new LinearLayout(this);
        card1.setOrientation(LinearLayout.HORIZONTAL);
        card1.setGravity(Gravity.CENTER_VERTICAL);
        card1.setBackground(Ui.card(this, Ui.CARD));
        card1.setPadding(Ui.dp(this, 18), Ui.dp(this, 16), Ui.dp(this, 18), Ui.dp(this, 16));
        LinearLayout.LayoutParams c1p = new LinearLayout.LayoutParams(-1, -2);
        c1p.topMargin = Ui.dp(this, 20);
        LinearLayout c1t = new LinearLayout(this);
        c1t.setOrientation(LinearLayout.VERTICAL);
        c1t.addView(Ui.text(this, "Terapkan profil", 16, Ui.WHITE, true));
        c1t.addView(Ui.text(this, "Berlaku otomatis saat aplikasi ini di latar depan", 12, Ui.SUB, false));
        card1.addView(c1t, new LinearLayout.LayoutParams(0, -2, 1f));
        swActive = new Switch(this);
        card1.addView(swActive);
        root.addView(card1, c1p);

        // Kartu pengaturan
        LinearLayout card2 = new LinearLayout(this);
        card2.setOrientation(LinearLayout.VERTICAL);
        card2.setBackground(Ui.card(this, Ui.CARD));
        card2.setPadding(Ui.dp(this, 18), Ui.dp(this, 18), Ui.dp(this, 18), Ui.dp(this, 18));
        LinearLayout.LayoutParams c2p = new LinearLayout.LayoutParams(-1, -2);
        c2p.topMargin = Ui.dp(this, 14);

        tvCpu = Ui.text(this, "Pengaturan CPU — 100%", 15, Ui.WHITE, true);
        card2.addView(tvCpu);
        sbCpu = slider();
        sbCpu.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean u) { tvCpu.setText("Pengaturan CPU — " + (p + 50) + "%"); }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });
        card2.addView(sbCpu);

        tvGpu = Ui.text(this, "Pengaturan GPU — 100%", 15, Ui.WHITE, true);
        LinearLayout.LayoutParams gP = new LinearLayout.LayoutParams(-1, -2);
        gP.topMargin = Ui.dp(this, 16);
        card2.addView(tvGpu, gP);
        sbGpu = slider();
        sbGpu.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean u) { tvGpu.setText("Pengaturan GPU — " + (p + 50) + "%"); }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });
        card2.addView(sbGpu);

        TextView tvFps = Ui.text(this, "Batas FPS", 15, Ui.WHITE, true);
        LinearLayout.LayoutParams fP = new LinearLayout.LayoutParams(-1, -2);
        fP.topMargin = Ui.dp(this, 16);
        card2.addView(tvFps, fP);
        rgFps = new RadioGroup(this);
        rgFps.setOrientation(RadioGroup.HORIZONTAL);
        rgFps.addView(radio("Maks", 0));
        rgFps.addView(radio("90", 90));
        rgFps.addView(radio("60", 60));
        card2.addView(rgFps);
        TextView note = Ui.text(this, "Batas FPS bekerja lewat refresh rate layar (panel A07: 60/90 Hz) selama aplikasi ini di latar depan. Batas CPU/GPU menurunkan frekuensi maksimum dari batas bawaan pabrik — tidak pernah menaikkannya.", 12, Ui.SUB, false);
        LinearLayout.LayoutParams nP = new LinearLayout.LayoutParams(-1, -2);
        nP.topMargin = Ui.dp(this, 10);
        card2.addView(note, nP);
        root.addView(card2, c2p);

        Button save = new Button(this);
        save.setText("Simpan");
        save.setTextColor(Ui.WHITE);
        save.setBackground(Ui.card(this, Ui.BLUE));
        LinearLayout.LayoutParams sP = new LinearLayout.LayoutParams(-1, Ui.dp(this, 50));
        sP.topMargin = Ui.dp(this, 18);
        save.setOnClickListener(v -> saveAndFinish());
        root.addView(save, sP);

        Button del = new Button(this);
        del.setText("Hapus profil");
        del.setTextColor(Ui.RED);
        del.setBackground(Ui.card(this, Ui.CARD));
        LinearLayout.LayoutParams dP = new LinearLayout.LayoutParams(-1, Ui.dp(this, 50));
        dP.topMargin = Ui.dp(this, 10);
        del.setOnClickListener(v -> new Thread(() -> {
            Map<String, int[]> m = Profiles.load();
            m.remove(pkg);
            Profiles.save(m);
            runOnUiThread(() -> { Toast.makeText(this, "Profil dihapus", Toast.LENGTH_SHORT).show(); finish(); });
        }).start());
        root.addView(del, dP);

        ScrollView sv = new ScrollView(this);
        sv.addView(root);
        setContentView(sv);

        new Thread(() -> {
            profiles = Profiles.load();
            final int[] cur = profiles.get(pkg);
            runOnUiThread(() -> {
                if (cur != null) {
                    swActive.setChecked(true);
                    sbCpu.setProgress(Math.max(0, cur[0] - 50));
                    sbGpu.setProgress(Math.max(0, cur[1] - 50));
                    int id = cur[2] == 60 ? 60 : (cur[2] == 90 ? 90 : 0);
                    rgFps.check(id);
                } else {
                    rgFps.check(0);
                }
            });
        }).start();
    }

    SeekBar slider() {
        SeekBar s = new SeekBar(this);
        s.setMax(50);
        s.setProgress(50);
        s.setProgressTintList(ColorStateList.valueOf(Ui.BLUE));
        s.setThumbTintList(ColorStateList.valueOf(Ui.BLUE));
        return s;
    }

    RadioButton radio(String label, int id) {
        RadioButton r = new RadioButton(this);
        r.setText(label);
        r.setTextColor(Ui.WHITE);
        r.setId(id);
        r.setButtonTintList(ColorStateList.valueOf(Ui.BLUE));
        return r;
    }

    void saveAndFinish() {
        final boolean active = swActive.isChecked();
        final int cpu = sbCpu.getProgress() + 50;
        final int gpu = sbGpu.getProgress() + 50;
        int checked = rgFps.getCheckedRadioButtonId();
        final int fps = checked == 60 ? 60 : (checked == 90 ? 90 : 0);
        new Thread(() -> {
            Map<String, int[]> m = Profiles.load();
            if (active) m.put(pkg, new int[]{cpu, gpu, fps});
            else m.remove(pkg);
            Profiles.save(m);
            runOnUiThread(() -> {
                Toast.makeText(this, active ? "Profil tersimpan" : "Profil dimatikan", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
