package com.pijupiju.dankreader;

import android.os.Bundle;
import android.os.Handler;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    ScrollView scrollView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        initViews();
    }

    private void initViews() {
        scrollView = findViewById(R.id.scrollView);
        String s = "";
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = this.getResources().openRawResource(R.raw.input);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            try {
                if ((s = bufferedReader.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            stringBuilder.append(s).append("\n");
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        textView = findViewById(R.id.textView);
        textView.setText(stringBuilder);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                getSharedPreferences(String.format("%s_preferences", getPackageName()), MODE_PRIVATE)
                        .edit().putInt("offset", scrollView.getScrollY()).apply();
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        final int offset = getSharedPreferences(String.format("%s_preferences", getPackageName()), MODE_PRIVATE)
                .getInt("offset", 0);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, offset);
            }
        }, 1337);

    }
}
