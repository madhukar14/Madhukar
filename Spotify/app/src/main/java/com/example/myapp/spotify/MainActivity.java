package com.example.myapp.spotify;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;

public class MainActivity extends Activity{

    public static final String CLIENT_ID = "b3b26b5ab61542f3ba7623c81a58e9ee";
    private static final String REDIRECT_URI = "spotify-test://callback";
    public  AuthenticationResponse spotresponse = null;
    public String querystring = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SpotifyAuthentication.openAuthWindow(CLIENT_ID, "token", REDIRECT_URI,
                        new String[]{"user-read-private", "streaming"}, null, MainActivity.this);
            }
        });


    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {
            spotresponse = SpotifyAuthentication.parseOauthResponse(uri);
            Intent newintent = new Intent(MainActivity.this, PlaySong.class);
            EditText trackEditText = (EditText) findViewById(R.id.track);
            EditText artistEditText = (EditText) findViewById(R.id.artist);
            querystring = "track:"+trackEditText.getText()+" artist:"+artistEditText.getText();
            newintent.putExtra("token", spotresponse.getAccessToken());
            newintent.putExtra("query", querystring);
            startActivity(newintent);
        }
    }
}