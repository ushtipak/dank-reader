package com.pijupiju.dankreader;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
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
    int textSize;

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

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> getSharedPreferences(String.format("%s_preferences", getPackageName()), MODE_PRIVATE)
                .edit().putInt("offset", scrollView.getScrollY()).apply());

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(String.format("%s_preferences", getPackageName()), MODE_PRIVATE);
        final int offset = sharedPreferences.getInt("offset", 0);
        final int size = sharedPreferences.getInt("size", 18);
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            scrollView.scrollTo(0, offset);
        }, 1337);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        textSize = Math.round(textView.getTextSize() / getResources().getDisplayMetrics().scaledDensity);
        switch (item.getItemId()) {
            case R.id.btnOpenFile:
                return true;
            case R.id.btnZoomIn:
                if (textSize < 20) {
                    int newSize = textSize + 1;
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize);
                    getSharedPreferences(String.format("%s_preferences", getPackageName()), MODE_PRIVATE)
                            .edit().putInt("size", newSize).apply();
                }
                return true;
            case R.id.btnZoomOut:
                if (textSize > 14) {
                    int newSize = textSize - 1;
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize);
                    getSharedPreferences(String.format("%s_preferences", getPackageName()), MODE_PRIVATE)
                            .edit().putInt("size", newSize).apply();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
