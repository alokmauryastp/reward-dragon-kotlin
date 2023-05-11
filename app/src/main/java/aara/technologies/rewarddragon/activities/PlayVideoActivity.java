package aara.technologies.rewarddragon.activities;

import static aara.technologies.rewarddragon.utils.Constant.REWARDMESSAGE;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import aara.technologies.rewarddragon.BonusDialogActivity;
import aara.technologies.rewarddragon.R;
import aara.technologies.rewarddragon.services.DataServices;
import aara.technologies.rewarddragon.services.RetrofitInstance;
import aara.technologies.rewarddragon.utils.Constant;
import aara.technologies.rewarddragon.utils.SharedPrefManager;
import aara.technologies.rewarddragon.utils.Youtube.Ui.DefaultPlayerUiController;
import aara.technologies.rewarddragon.utils.Youtube.core.PlayerConstants;
import aara.technologies.rewarddragon.utils.Youtube.core.YouTubePlayer;
import aara.technologies.rewarddragon.utils.Youtube.core.listeners.AbstractYouTubePlayerListener;
import aara.technologies.rewarddragon.utils.Youtube.core.listeners.YouTubePlayerListener;
import aara.technologies.rewarddragon.utils.Youtube.core.options.IFramePlayerOptions;
import aara.technologies.rewarddragon.utils.Youtube.core.utils.YouTubePlayerUtils;
import aara.technologies.rewarddragon.utils.Youtube.core.views.YouTubePlayerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayVideoActivity extends AppCompatActivity {

    private static final String TAG = "PlayVideoActivity";
    YouTubePlayerView youTubePlayerView;
    Chronometer chronometer;
    //CustomLoader customLoader;
    int videoId;
    String point, reward_message;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        // customLoader = new CustomLoader(getApplicationContext(), android.R.style.Theme_Translucent_NoTitleBar);

        youTubePlayerView = findViewById(R.id.youtube);
        videoId = getIntent().getIntExtra("videoId", -1);
        Log.e("TAG", "onCreate: " + videoId);


        chronometer = findViewById(R.id.chronometer);

        initYouTubePlayerView();
    }


    private void initYouTubePlayerView() {
        getLifecycle().addObserver(youTubePlayerView);
        YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                // using pre-made custom ui
                DefaultPlayerUiController defaultPlayerUiController = new DefaultPlayerUiController(youTubePlayerView, youTubePlayer);
                youTubePlayerView.setCustomPlayerUi(defaultPlayerUiController.getRootView());

                YouTubePlayerUtils.loadOrCueVideo(
                        youTubePlayer,
                        getLifecycle(),
                        getIntent().getStringExtra("link"),
                        0f);
            }

            @Override
            public void onVideoDuration(@NonNull YouTubePlayer youTubePlayer, float duration) {
                super.onVideoDuration(youTubePlayer, duration);
                Log.e("onVideoDuration", "onVideoDuration: " + duration);

            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);
                //    Log.i(TAG, "initYouTubePlayerView: 3");

                Log.e("onStateChange", "onStateChange: " + state);
                if (state == PlayerConstants.PlayerState.PLAYING) {

                    if (chronometer.getText().equals("00:00")) {
                        chronometer.setBase(SystemClock.elapsedRealtime());
                    }
                    chronometer.start();

                    Log.i(TAG, "onStateChange: " + chronometer.getText());
//                    chronometer.
                } else if (state == PlayerConstants.PlayerState.PAUSED) {
                    chronometer.stop();

                    int i = youTubePlayerView.getScrollBarFadeDuration();
                    Log.e("onStateChange", "onStateChange: " + i);

                }
            }
        };

        // disable web ui
        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).build();

        youTubePlayerView.initialize(listener, options);

    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            youTubePlayerView.enterFullScreen();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            youTubePlayerView.exitFullScreen();
        }
    }

    public void watchTimeData() {

        //   customLoader.show();
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_profile", Objects.requireNonNull(new SharedPrefManager(getApplicationContext()).getUser().getId()).toString());
        map.put("leader_ship_task", videoId);
        map.put("spent_time", chronometer.getText().toString());
        map.put("spent_time_seconds", Constant.INSTANCE.convertHHSStoSeconds(chronometer.getText().toString()));
        map.put("bonus_point", 0);
        Log.e("TAG", "watchTimeData: map " + map);
        DataServices services = new RetrofitInstance().getInstance().create(DataServices.class);
        Call<JsonObject> call = services.watchTimeData(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Log.e(TAG, "onResponse: " + response.code());
                Log.e(TAG, "onResponse: " + response.body());
                //   customLoader.dismiss();

                try {
                    if (response.code() == 200) {
                        JSONObject obj = new JSONObject(new Gson().toJson(response.body()));

                        showBonusDialog(obj);

                    } else {
                        finish();
                        Log.i(TAG, "response code: " + response.code());

                    }


                } catch (JSONException e) {
                    finish();
                    Log.i(TAG, "error: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                //  customLoader.dismiss();
                finish();
                Log.e("watchTimeData t", "onFailure: " + t.getMessage());
            }
        });
    }

    private void showBonusDialog(JSONObject obj) throws JSONException {
        JSONObject obj2 = obj.getJSONObject("reward_points_data");
        if (obj2.length() > 0) {
            String points = obj2.getString("reward_points");
            String message = obj2.getString("reward_message");
            if (Integer.parseInt(points) > 0) {
                startActivity(new
                        Intent(PlayVideoActivity.this, BonusDialogActivity.class)
                        .putExtra(Constant.REWARDPOINTS, points)
                        .putExtra(REWARDMESSAGE, message));
                finish();
                Log.i(TAG, "onResponse: reward_points " + points + " reward_message " + message);
            }
        } else {
            finish();
            Log.i(TAG, "onResponse: reward_points null");

        }
    }

    public void inspiredLivingData() {

//        customLoader.show();
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_profile", Objects.requireNonNull(new SharedPrefManager(getApplicationContext()).getUser().getId()).toString());
        map.put("finance_and_art_video", videoId);
        map.put("spent_time", chronometer.getText().toString());
        map.put("spent_time_seconds", Constant.INSTANCE.convertHHSStoSeconds(chronometer.getText().toString()));
        map.put("bonus_point", 0);
        Log.e(TAG, "inspiredLivingData: map " + map);
        DataServices services = new RetrofitInstance().getInstance().create(DataServices.class);
        Call<JsonObject> call = services.inspiredLivingData(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Log.e(TAG, "inspiredLivingData onResponse: " + response.body());

                try {
                    if (response.code() == 200) {
                        JSONObject obj = new JSONObject(new Gson().toJson(response.body()));

                        showBonusDialog(obj);

                    } else {
                        finish();
                        Log.i(TAG, "response code: " + response.code());

                    }


                } catch (JSONException e) {
                    finish();
                    Log.i(TAG, "error: " + e.getMessage());
                    e.printStackTrace();
                }
//                customLoader.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
//                customLoader.dismiss();
                Log.e("watchTimeData t", "onFailure: " + t.getMessage());
            }
        });
    }

    public void skillHobbyTimeData() {
//        customLoader.show();

        HashMap<String, Object> map = new HashMap<>();
        map.put("user_profile", Objects.requireNonNull(new SharedPrefManager(getApplicationContext()).getUser().getId()).toString());
        map.put("skill_and_hobby", videoId);
        map.put("spent_time", chronometer.getText());
        map.put("spent_time_seconds", Constant.INSTANCE.convertHHSStoSeconds(chronometer.getText().toString()));
        Log.e("TAG", "skillHobbyTimeData: map " + map);
        DataServices services = new RetrofitInstance().getInstance().create(DataServices.class);
        Call<JsonObject> call = services.skillHobbyTimeData(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Log.e(TAG, "skillHobbyTimeData" + "onResponse: " + response.code());
                Log.e(TAG, "skillHobbyTimeData onResponse: " + response.body());
                Log.e(TAG, "skillHobbyTimeData onResponse: " + response.message());

                try {
                    if (response.code() == 200) {
                        JSONObject obj = new JSONObject(new Gson().toJson(response.body()));
                        showBonusDialog(obj);
                    } else {
                        finish();
                        Log.i(TAG, "response code: " + response.code());

                    }


                } catch (JSONException e) {
                    finish();
                    Log.i(TAG, "error: " + e.getMessage());
                    e.printStackTrace();
                }
//                customLoader.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
//                customLoader.dismiss();
                finish();
                Log.e("skillHobbyTimeData t", "onFailure: " + t.getMessage());
            }
        });
    }

    public void learningMaterialTimeData() {
//        customLoader.show();
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_profile", Objects.requireNonNull(new SharedPrefManager(getApplicationContext()).getUser().getId()).toString());
        map.put("learning_material", videoId);
        map.put("spent_time", chronometer.getText());
        map.put("spent_time_seconds", Constant.INSTANCE.convertHHSStoSeconds(chronometer.getText().toString()));
        Log.e("learning_material", "onResponse: " + map);
        DataServices services = new RetrofitInstance().getInstance().create(DataServices.class);
        Call<JsonObject> call = services.learningMaterialTimeData(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Log.e("learning_material", "onResponse: " + response.code());
                Log.e("learning_material", "onResponse: " + response.body());

                try {
                    if (response.code() == 200) {
                        JSONObject obj = new JSONObject(new Gson().toJson(response.body()));

                        showBonusDialog(obj);

                    } else {
                        finish();
                        Log.i(TAG, "response code: " + response.code());

                    }


                } catch (JSONException e) {
                    finish();
                    Log.i(TAG, "error: " + e.getMessage());
                    e.printStackTrace();
                }
//                customLoader.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
//                customLoader.dismiss();
                finish();
                Log.e("learning_material t", "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.e("backPressed", "onBackPressed: ");


//        if (getIntent().getStringExtra("from").equals("health")) {
//            watchTimeData();
//        } else {
        super.onBackPressed();
        // }
    }

    @Override
    protected void onDestroy() {
        Log.e("onDestroy", "onDestroy: ");
        System.out.println("Text : " + chronometer.getText());
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date start = sdf.parse("00:00");
            Date end = sdf.parse(chronometer.getText().toString());
            long spend_time = end.getTime() - start.getTime();
            Log.i(TAG, "difference: " + spend_time);
            float seconds = (spend_time / 100000F);

            Log.i(TAG, "onStop: " + point + " " + reward_message + " spend_Time " + seconds);

            if (seconds >= 1) {
                switch (getIntent().getStringExtra("from")) {
                    case "skill":
                        skillHobbyTimeData();
                        break;
                    case "learning":
                        learningMaterialTimeData();
                        break;

                    case "health":
                        watchTimeData();
                        break;

                    case "inspiredLiving":
                        inspiredLivingData();
                        break;
                    default:
                        break;
                }
            }

        } catch (Exception e) {
            Log.i(TAG, "difference: " + e.getMessage());

            e.printStackTrace();
        }


        testing();

    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    public void testing() {
        Log.e("onDestroy", "testing: " + chronometer.getText());
    }

}