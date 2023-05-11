package aara.technologies.rewarddragon.activities;

import static aara.technologies.rewarddragon.utils.Constant.REWARDMESSAGE;
import static aara.technologies.rewarddragon.utils.Constant.onRefresh;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import aara.technologies.rewarddragon.BonusDialogActivity;
import aara.technologies.rewarddragon.R;
import aara.technologies.rewarddragon.services.DataServices;
import aara.technologies.rewarddragon.services.InternetCheck;
import aara.technologies.rewarddragon.services.RetrofitInstance;
import aara.technologies.rewarddragon.utils.Constant;
import aara.technologies.rewarddragon.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebViewActivity extends AppCompatActivity {

    public static final int REQUEST_SELECT_FILE = 100;
    private static final String TAG = "WebViewActivity";
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();


    public ValueCallback<Uri[]> uploadMessage;
    WebView webView;
    ProgressBar progressBar;
    //    String link="https://cdn.htmlgames.com/CastleMysteries/";
//    String link="https://cdn.htmlgames.com/TicTacToe/";
    Chronometer chronometer;
    //Declare timer
    CountDownTimer cTimer = null;
    String gameLink;
    int gameId;

    TextView timerTv;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progressBar);
        chronometer = findViewById(R.id.chronometer);
        timerTv = findViewById(R.id.timerTv);
        gameLink = getIntent().getStringExtra("link");
        gameId = getIntent().getIntExtra("gameId", -1);
//        gameLink = "https://play.famobi.com/jigsaw-puzzle-deluxe";
//        gameId = 6;


        chronometer.start();
        new Handler().post(() -> startTimer());


        if (new InternetCheck().isNetworkAvailable(getApplicationContext())) {
            webView.setWebViewClient(new MyBrowser());
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.getSettings().setAllowContentAccess(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
//            webView.setWebViewClient(new MyBrowser());

            webView.getSettings().setAllowContentAccess(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

            webView.getSettings().setAppCachePath(getApplicationContext().getApplicationContext().getCacheDir().getAbsolutePath());
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            webView.getSettings().setDatabaseEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String request) {
                    view.loadUrl(request);
                    //progressBar.setVisibility(View.GONE);
                    return super.shouldOverrideUrlLoading(view, request);
                }
            });
            webView.setWebChromeClient(new WebChromeClient() {

                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                    // make sure there is no existing message

                    if (uploadMessage != null) {
                        uploadMessage.onReceiveValue(null);
                        uploadMessage = null;
                    }

                    uploadMessage = filePathCallback;

                    Intent intent = fileChooserParams.createIntent();
                    try {
                        startActivityForResult(intent, REQUEST_SELECT_FILE);
                    } catch (ActivityNotFoundException e) {
                        uploadMessage = null;
                        Toast.makeText(getApplicationContext(), "Cannot open file chooser", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    return true;
                }
            });

            webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "RewardDragon");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
                        Toast.LENGTH_LONG).show();
            });
            new Handler().post(() -> webView.loadUrl(gameLink));

        } else {
            Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "onDestroy: " + "webView" + chronometer.getText().toString());
        // super.onDestroy();
    }

    //start timer function
    void startTimer() {

        int MIN = 5;
        int SEC = MIN * 60;
        int MILLI = SEC * 1000;
        cTimer = new CountDownTimer(MILLI, 1000) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                String time = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
                Log.e("Time", "onTick: " + time);
                timerTv.setText(time);

            }

            public void onFinish() {
                new Handler().post(() -> WebViewActivity.this.finish());
            }
        };
        cTimer.start();


        //


    }

    //cancel timer
    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        super.onStop();

        playedGameTime(Constant.INSTANCE.convertHHSStoSeconds(chronometer.getText().toString()));
        cancelTimer();
        Log.e("onStop", "onStop: " + "testa");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            //progressBar.setVisibility(View.VISIBLE);
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void playedGameTime(int timeSpend) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_profile", Objects.requireNonNull(new SharedPrefManager(getApplicationContext()).getUser().getId()).toString());
        map.put("is_end", 1);
        map.put("time_spent", timeSpend);
        map.put("game_name", gameId);
        map.put("next_availability_time", LocalDateTime.now().plusHours(24).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s", Locale.getDefault())));
        Log.e("playedGameTime", "onResponse: map " + map);
        DataServices services = new RetrofitInstance().getInstance().create(DataServices.class);
        Call<JsonObject> call = services.playedGameTime(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                Log.e(TAG, "onResponse: " + response.code());
                Log.e(TAG, "onResponse: " + response.body());
                if (response.code() == 200) {
                    try {
                        JSONObject obj = new JSONObject(new Gson().toJson(response.body()));
                        JSONObject obj2 = obj.getJSONObject("reward_points_data");
                        if (obj2.length() > 0) {
                            String points = obj2.getString("reward_points");
                            String message = obj2.getString("reward_message");
                            if (Integer.parseInt(points) > 0) {
                                startActivity(new
                                        Intent(WebViewActivity.this, BonusDialogActivity.class)
                                        .putExtra(Constant.REWARDPOINTS, points)
                                        .putExtra(REWARDMESSAGE, message));
                                finish();
                            }
                            Log.i(TAG, "onResponse: reward_points " + points + " reward_message " + message);

                        } else {
                            finish();
                            Log.i(TAG, "onResponse: reward_points null");

                        }


                    } catch (Exception e) {
                        Log.i(TAG, "onResponse: error " + e.getMessage());

                    }
                    onRefresh.refresh();
                }

            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("playedGameTime t", "onFailure: " + t.getMessage());
            }
        });
    }

    private class MyBrowser extends WebViewClient {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("mailto:")) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("whatsapp:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } else {
                if (!new InternetCheck().isNetworkAvailable(getApplicationContext())) {

                    Toast toast = Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                } else {
                    view.loadUrl(url);
                }

//                if (!progressBar.isAnimating()) {
//                    progressBar.setVisibility(View.VISIBLE);
//                }

                // Toast.makeText(getApplicationContext(),url,Toast.LENGTH_LONG).show();
            }
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
//            if (progressBar.isAnimating()) {
//                progressBar.setVisibility(View.GONE);
//            }

        }
    }

}
