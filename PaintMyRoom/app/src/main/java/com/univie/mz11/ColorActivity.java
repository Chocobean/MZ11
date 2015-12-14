package com.univie.mz11;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class ColorActivity extends Activity implements SeekBar.OnSeekBarChangeListener,
        View.OnClickListener {

    SeekBar redSeekBar, greenSeekBar, blueSeekBar;
    TextView textR, textG, textB;
    View favView1, favView2, favView3, favView4, favView5;
    View colView;
    Button btnSelect;
    int r,g,b;

    public ColorActivity() {
        r=0; g=0; b=0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.color);

        redSeekBar = (SeekBar)findViewById(R.id.seekBar);
        greenSeekBar = (SeekBar)findViewById(R.id.seekBar2);
        blueSeekBar = (SeekBar)findViewById(R.id.seekBar3);
        textR = (TextView)findViewById(R.id.textView);
        textG = (TextView)findViewById(R.id.textView2);
        textB = (TextView)findViewById(R.id.textView3);
        colView = findViewById(R.id.colorView);
        btnSelect = (Button)findViewById(R.id.button);

        favView1 = findViewById(R.id.favView1);
        favView2 = findViewById(R.id.favView2);
        favView3 = findViewById(R.id.favView3);
        favView4 = findViewById(R.id.favView4);
        favView5 = findViewById(R.id.favView5);

        favView1.setOnClickListener(this);
        favView2.setOnClickListener(this);
        favView3.setOnClickListener(this);
        favView4.setOnClickListener(this);
        favView5.setOnClickListener(this);

        redSeekBar.setOnSeekBarChangeListener(this);
        greenSeekBar.setOnSeekBarChangeListener(this);
        blueSeekBar.setOnSeekBarChangeListener(this);

        SharedPreferences sp = this.getPreferences( Context.MODE_PRIVATE );
        favView1.setBackgroundColor(sp.getInt("1C", -256));
        favView2.setBackgroundColor(sp.getInt("2C", -65536));
        favView3.setBackgroundColor(sp.getInt("3C", -65281));
        favView4.setBackgroundColor(sp.getInt("4C", -3355444));
        favView5.setBackgroundColor(sp.getInt("5C", -16776961));

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favView5.setBackgroundColor(((ColorDrawable)favView4.getBackground()).getColor());
                favView4.setBackgroundColor(((ColorDrawable)favView3.getBackground()).getColor());
                favView3.setBackgroundColor(((ColorDrawable)favView2.getBackground()).getColor());
                favView2.setBackgroundColor(((ColorDrawable)favView1.getBackground()).getColor());
                favView1.setBackgroundColor(((ColorDrawable)colView.getBackground()).getColor());

                SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("6C", (((ColorDrawable)colView.getBackground()).getColor()));
                editor.commit();
                finish();
            }
        });

        favView1.callOnClick();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPreferences sp = this.getPreferences( Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt("1C", ((ColorDrawable)favView1.getBackground()).getColor());
        editor.putInt("2C", ((ColorDrawable)favView2.getBackground()).getColor());
        editor.putInt("3C", ((ColorDrawable)favView3.getBackground()).getColor());
        editor.putInt("4C", ((ColorDrawable)favView4.getBackground()).getColor());
        editor.putInt("5C", ((ColorDrawable)favView5.getBackground()).getColor());

        //editor.commit();
        editor.apply();
    }

    public void onClick(View view) {
        ColorDrawable cd = (ColorDrawable)view.getBackground();
        int colorID = cd.getColor();
        colView.setBackgroundColor(colorID);

        float[] hsv = new float[3];
        Color.colorToHSV(colorID, hsv);
        int outputColor = Color.HSVToColor(hsv);
        r = Color.red(outputColor);
        g = Color.green(outputColor);
        b = Color.blue(outputColor);

        redSeekBar.setProgress(     (int)((r / 255.0) * 100));
        greenSeekBar.setProgress(   (int)((g / 255.0) * 100));
        blueSeekBar.setProgress(    (int)((b / 255.0) * 100));
    }

    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
        if (seekBar == redSeekBar) {
            r = (int)((progress / 100.0) * 255);
        }
        else if (seekBar == greenSeekBar) {
            g = (int)((progress / 100.0) * 255);
        }
        else if (seekBar == blueSeekBar) {
            b = (int)((progress / 100.0) * 255);
        }

        textR.setText(""+r);
        textG.setText(""+g);
        textB.setText(""+b);

        colView.setBackgroundColor(Color.rgb(r,g,b));
    }

    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}