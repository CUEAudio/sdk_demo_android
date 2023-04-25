package com.cueaudio.demo;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.cueaudio.live.CUEActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.demo_launch_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDemo();
            }
        });

        // To overwrite theme, insert Client Api Key here and uncomment the following code:
        // Note: If you pull your client API Key from the server, it should be cached (e.g., `UserDefaults`) so that the user can access the light show even without a network connection.
        //CUEController.fetchCueTheme(this, <#myApiKey#>);
    }

    private void launchDemo() {
        final Intent i = new Intent(this, CUEActivity.class);

        // To overwrite theme, insert Client Api Key here and uncomment the following code:
        // Note: If you pull your client API Key from the server, it should be cached (e.g., `UserDefaults`)
        // so that the user can access the light show even without a network connection.
        //i.putExtra(CUEActivity.EXTRA_CUE_API_KEY, <#myApiKey#>);

        // Set next flags to disable Trivia loser/winner prize
        //i.putExtra(CUEActivity.EXTRA_IGNORE_TRIVIA_LOSER_PRIZE, true);
        //i.putExtra(CUEActivity.EXTRA_IGNORE_TRIVIA_WINNER_PRIZE, true);

        // Used to disable the navigation menu
        //i.putExtra(CUEActivity.EXTRA_CUE_ENABLE_NAVIGATION_MENU, false);

        // Used to initiate exact CUE trigger from push notification or app start
        //i.putExtra(CUEActivity.EXTRA_CUE_TRIGGER, "311.65.106");

        startActivity(i);
    }

}
