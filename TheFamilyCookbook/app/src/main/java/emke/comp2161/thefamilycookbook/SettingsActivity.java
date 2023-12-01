package emke.comp2161.thefamilycookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;


public class SettingsActivity extends AppCompatActivity {

    int currentTheme;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets theme of activity as the stored theme
        SharedPreferences sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        currentTheme = sharedPreferences.getInt("currentTheme", R.style.Theme_TheFamilyCookbook);
        setTheme(currentTheme);
        setContentView(R.layout.activity_settings);

        ImageButton backBtn = findViewById(R.id.setting_back_btn);
        CardView defaultTheme = findViewById(R.id.theme_default);
        CardView dinerTheme = findViewById(R.id.theme_diner);
        CardView kitchenTheme = findViewById(R.id.theme_kitchen);
        CardView retroTheme = findViewById(R.id.theme_retro);
        webView = findViewById(R.id.web_view);

        //Loads Url in web view for feedback
        webView.setWebViewClient(new load());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://docs.google.com/forms/d/e/1FAIpQLSdVrGYKbrAnqK3hkLZpm0tM8tOkXlrUrIi1A7XoC-smoNJrhw/viewform?usp=sf_link");

        //On click listener to change theme to default
        defaultTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentTheme != R.style.Theme_TheFamilyCookbook) {
                    saveTheme(R.style.Theme_TheFamilyCookbook);
                }
            }
        });

        //On click listener to change theme to diner
        dinerTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentTheme != R.style.Theme_Diner) {
                    saveTheme(R.style.Theme_Diner);
                }
            }
        });

        //On click listener to change theme to retro
        retroTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentTheme != R.style.Theme_Retro) {
                    saveTheme(R.style.Theme_Retro);
                }
            }
        });

        //On click listener to change theme to kitchen
        kitchenTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentTheme != R.style.Theme_Kitchen) {
                    saveTheme(R.style.Theme_Kitchen);
                }
            }
        });

        //returns user to the activity they were previously at
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    /*
    Stores the selected theme in shared preferences so that it can effect all activities in the app
     */
    private void saveTheme(int themeID) {
        SharedPreferences sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentTheme", themeID);
        editor.commit();
        resetView();
    }

    //Resets settings activity with newly pressed theme
    private void resetView(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    //Class to load google form Url in WebView
    private class load extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String request) {
            view.loadUrl(request);

            return true;
        }
    }
}