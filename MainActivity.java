package com.musicviz;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Equalizer;

public class MainActivity extends Activity {
    private CarView carView;
    private Visualizer visualizer;
    private Equalizer equalizer;
    private boolean whiteCar = false;
    private int bass = 70, treble = 50;
    private TextView status;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        buildUi();
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 10);
        } else startAudioTools();
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 12, 16, 12);
        root.setBackgroundColor(Color.rgb(8,8,10));

        TextView title = new TextView(this);
        title.setText("MUSICVIZ  •  BASS VISUALIZER");
        title.setTextColor(Color.WHITE); title.setTextSize(20); title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(-1, 48));

        carView = new CarView(this);
        root.addView(carView, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout colors = new LinearLayout(this);
        Button black = makeButton("ЧЁРНАЯ ВАЗ 2110");
        Button white = makeButton("БЕЛАЯ ВАЗ 2110");
        black.setOnClickListener(v -> { whiteCar = false; carView.setWhite(false); });
        white.setOnClickListener(v -> { whiteCar = true; carView.setWhite(true); });
        colors.addView(black, new LinearLayout.LayoutParams(0, 48, 1));
        colors.addView(white, new LinearLayout.LayoutParams(0, 48, 1));
        root.addView(colors);

        addSlider(root, "БАС", 0, 100, bass, value -> { bass=value; applyEqualizer(); });
        addSlider(root, "ВЕРХНИЕ ЧАСТОТЫ", 0, 100, treble, value -> { treble=value; applyEqualizer(); });

        status = new TextView(this);
        status.setText("Ожидание аудио • разреши доступ к микрофону");
        status.setTextColor(Color.LTGRAY); status.setTextSize(12); status.setGravity(Gravity.CENTER);
        root.addView(status, new LinearLayout.LayoutParams(-1, 32));
        setContentView(root);
    }

    private interface ValueChanged { void onChange(int value); }
    private void addSlider(LinearLayout root, String label, int min, int max, int initial, ValueChanged cb) {
        TextView t = new TextView(this); t.setText(label); t.setTextColor(Color.WHITE); t.setTextSize(12);
        root.addView(t);
        SeekBar bar = new SeekBar(this); bar.setMax(max-min); bar.setProgress(initial-min);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean u) { if(u) cb.onChange(p+min); }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });
        root.addView(bar, new LinearLayout.LayoutParams(-1, 34));
    }

    private Button makeButton(String text) {
        Button b = new Button(this); b.setText(text); b.setTextSize(10); b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(45,45,52)));
        return b;
    }

    private void startAudioTools() {
        try {
            visualizer = new Visualizer(0);
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                public void onWaveFormDataCapture(Visualizer v, byte[] data, int rate) {
                    float level=0; for(byte b:data) level=Math.max(level, Math.abs((int)b-128)/128f);
                    final float audioLevel = level;\n                    runOnUiThread(() -> carView.setAudioLevel(audioLevel));
                }
                public void onFftDataCapture(Visualizer v, byte[] data, int rate) {}
            }, Visualizer.getMaxCaptureRate()/2, true, false);
            visualizer.setEnabled(true);
            status.setText("Аудиовизуализация активна (если устройство разрешает захват выхода)");
        } catch (Exception e) {
            status.setText("Нет доступа к системному аудиопотоку на этом устройстве");
        }
        try {
            equalizer = new Equalizer(0, 0);
            equalizer.setEnabled(true);
            applyEqualizer();
        } catch (Exception e) {
            Toast.makeText(this, "Системный эквалайзер недоступен на этом устройстве", Toast.LENGTH_LONG).show();
        }
    }

    private void applyEqualizer() {
        if (equalizer == null) return;
        try {
            short[] range = equalizer.getBandLevelRange();
            short[] bands = equalizer.getBandLevelRange().length > 0 ? new short[]{0} : new short[0];
            for (short i=0; i<equalizer.getNumberOfBands(); i++) {
                int center = equalizer.getCenterFreq(i)/1000;
                int value = center < 250 ? (bass-50)*2 : (center > 3000 ? (treble-50)*2 : 0);
                short level = (short)Math.max(range[0], Math.min(range[1], value*10));
                equalizer.setBandLevel(i, level);
            }
        } catch (Exception ignored) {}
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == 10 && results.length>0 && results[0]==PackageManager.PERMISSION_GRANTED) startAudioTools();
        else if (status != null) status.setText("Разрешение на аудио не выдано — анимация будет демонстрационной");
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        try { if (visualizer != null) { visualizer.setEnabled(false); visualizer.release(); } } catch(Exception ignored){}
        try { if (equalizer != null) equalizer.release(); } catch(Exception ignored){}
    }
}
