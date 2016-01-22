package com.jorey.recapp;

import android.app.ActionBar;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

public class MainActivity extends FragmentActivity implements RecordFragment.OnRecordInteractionListener, PlayFragment.OnPlayInteractionListener, DataApi.DataListener, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
    static final int NUM_ITEMS = 2;
    RecorderPager recorderPager;
    ViewPager viewPager;
    private GoogleApiClient mGoogleApiClient;
    private PlayFragment playFragment;
    public String tag = "Main";
    public InputStream assetInputStream;
    public Asset asset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //data layer client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //mGoogleApiClient.connect();
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ActionBar actionBar = getActionBar();
        actionBar.hide();
        setContentView(R.layout.fragment_pager);
        recorderPager = new RecorderPager(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(recorderPager);
    }

    @Override
    public void onRecord() {
        //PlayFragment pf=(PlayFragment)getSupportFragmentManager().findFragmentById(R.id.play_fragment);
        //pf.load();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(tag,"api client connected");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(tag, "api client suspended");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.v(tag, "data changed");
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().equals("/recording")) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                    asset = dataMap.getAsset("recording");
                    //Log.v(tag,"asset: "+asset.getData().toString());

                    //byte[] data=asset.getData();
                    //Log.v(tag,""+data);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Conversation conversation=loadAsset(asset);
                            conversation.save(2048); //shame on you
                        }
                    }).start();

                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    public Conversation loadAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(tag, "Requested an unknown Asset.");
            return null;
        }

        return new Conversation(assetInputStream);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(tag,"api client failed");
    }

    public static class RecorderPager extends FragmentPagerAdapter {
        public RecorderPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0){
                return new RecordFragment();
            }
            else{
                return new PlayFragment();
            }
        }
    }
}