package com.jorey.recapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
    static final int NUM_ITEMS = 2;
    PlanetFragmentPagerAdapter planetFragmentPagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);
        planetFragmentPagerAdapter = new PlanetFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(planetFragmentPagerAdapter);
        Button button = (Button)findViewById(R.id.goto_first);
        button.setOnClickListener(btnListener);
        button = (Button)findViewById(R.id.goto_previous);
        button.setOnClickListener(btnListener);
        button = (Button)findViewById(R.id.goto_next);
        button.setOnClickListener(btnListener);
        button = (Button)findViewById(R.id.goto_next);
        button.setOnClickListener(btnListener);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.goto_first:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.goto_previous:
                    viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
                    break;
                case R.id.goto_next:
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                    break;
                case R.id.goto_last:
                    viewPager.setCurrentItem(NUM_ITEMS - 1);
                    break;
            }
        }
    };

    public static class PlanetFragmentPagerAdapter extends FragmentPagerAdapter {
        public PlanetFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();
            return SwipeFragment.newInstance(position);
        }
    }

    public static class SwipeFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.fragment_play, container, false);
            TextView tv = (TextView)swipeView.findViewById(R.id.text);

            Bundle args = getArguments();
            int position = args.getInt("position");
            /*String planet = Planet.PLANETS[position];
            int imgResId = getResources().getIdentifier(planet, "drawable", "com.javapapers.android.androidswipeableviews.app");
            img.setImageResource(imgResId);
            tv.setText(Planet.PLANET_DETAIL.get(planet)+" - Wikipedia.");*/
            return swipeView;
        }

        static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            swipeFragment.setArguments(args);
            return swipeFragment;
        }
    }
}