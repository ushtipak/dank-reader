package com.pijupiju.dankreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
        textView = findViewById(R.id.textView);
        scrollView = findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> getSharedPreferences(String.format("%s_preferences", getPackageName()), MODE_PRIVATE)
                .edit().putInt("offset", scrollView.getScrollY()).apply());

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(String.format("%s_preferences", getPackageName()), MODE_PRIVATE);
        final int offset = sharedPreferences.getInt("offset", 0);
        final int size = sharedPreferences.getInt("size", 18);
        final String file = sharedPreferences.getString("file", "");
        final Handler handler = new Handler();
        if (!file.equals("")) {
            loadFile(new File(file));
            handler.postDelayed(() -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
                scrollView.scrollTo(0, offset);
            }, 1337);
        }
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
                openFile();
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

    private void openFile() {
        Intent intent = new Intent(getApplicationContext(), FileChooser.class);
        intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    if (uri.getPath() != null) {
                        File file = new File(uri.getPath());
                        loadFile(file);
                        getSharedPreferences(
                                String.format("%s_preferences", getPackageName()), MODE_PRIVATE)
                                .edit().putString("file", uri.getPath()).apply();
                    }
                }
            }
        }
    }

    private void loadFile(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        textView.setText(stringBuilder);
    }

}
