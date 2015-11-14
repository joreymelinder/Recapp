package com.jorey.recapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class MainActivity extends FragmentActivity implements RecordFragment.OnFragmentInteractionListener, PlayFragment.OnFragmentInteractionListener{
    static final int NUM_ITEMS = 2;
    RecorderPager recorderPager;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);
        recorderPager = new RecorderPager(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(recorderPager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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