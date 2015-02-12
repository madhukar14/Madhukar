package com.example.myapp.spotify;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.playback.Config;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class PlaySong extends ActionBarActivity implements
        PlayerNotificationCallback, ConnectionStateCallback {

    public String uriStr = "";
    public String token = "";
    public String query = "";
    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        query = intent.getStringExtra("query");
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(token);
        SpotifyService spoty = api.getService();
        spoty.searchTracks(query,new Callback<TracksPager>() {
            public String dispStr = "Sorry no tracks available for your query";
            @Override
            public void success(TracksPager tracksPager, Response response) {
                Pager<Track> tracks = tracksPager.tracks;
                if(tracks.items.size() > 0){
                    String trackName = tracks.items.get(0).name;
                    String album = tracks.items.get(0).album.name;
                    String artists = "Artists involved: ";
                    List<ArtistSimple> artistList = tracks.items.get(0).artists;
                    for(ArtistSimple a: artistList){
                        artists = artists +" "+a.name+",";
                    }
                    dispStr = "Playing "+trackName+" from album "+album+"\n"+artists;

                    uriStr = tracks.items.get(0).uri;
                    Config playerConfig = new Config(PlaySong.this, token, MainActivity.CLIENT_ID);
                    Spotify spotify = new Spotify();
                    mPlayer = spotify.getPlayer(playerConfig, PlaySong.this, new Player.InitializationObserver() {
                        @Override
                        public void onInitialized(Player player) {
                            mPlayer.addConnectionStateCallback(PlaySong.this);
                            mPlayer.addPlayerNotificationCallback(PlaySong.this);
                            mPlayer.play(uriStr);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView display = (TextView)findViewById(R.id.play);
                        display.setText(dispStr);
                    }
                });

            }
            @Override
            public void failure(RetrofitError retrofitError) {
                String abc = "madhukar";
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_song, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoggedIn() {

        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }
}
