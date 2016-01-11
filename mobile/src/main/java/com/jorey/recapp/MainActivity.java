package com.jorey.recapp;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MainActivity extends FragmentActivity implements RecordFragment.OnRecordInteractionListener, PlayFragment.OnPlayInteractionListener{
    static final int NUM_ITEMS = 2;
    RecorderPager recorderPager;
    ViewPager viewPager;
    private PlayFragment playFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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