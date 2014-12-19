package com.example.TabWithPopupMenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.ActionBarTab.R;

public class MyActivity extends FragmentActivity {

    public static final int TAB_NUM = 3;

    ViewPager pager;
    private PagerAdapter adapter;
    private ViewIndicator indicator;
    private RadioGroup radioGroup;
    private RadioButton button1;
    private RadioButton button2;
    private RadioButton button3;
    private boolean stateFlag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_main);

        initIndicator();
        initRadioGroup();
        initViewPager();
    }

    private void initIndicator() {
        indicator = (ViewIndicator) findViewById(R.id.view_indicator);
        indicator.setPageNum(TAB_NUM);
    }

    private void initRadioGroup() {

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        button1 = (RadioButton) findViewById(R.id.radio_1);
        button2 = (RadioButton) findViewById(R.id.radio_2);
        button3 = (RadioButton) findViewById(R.id.radio_3);

        final PopupMenu popupMenu = new PopupMenu(this, button1);
        popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(MyActivity.this, "click on " + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(0);
                popupMenu.show();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(1);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(2);
            }
        });

        radioGroup.check(R.id.radio_1);
    }

    private void initViewPager() {
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new SignAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(TAB_NUM);
        pager.setCurrentItem(0);
        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {

            private int paged = 0;

            @Override
            public void onPageScrolled(int i, float v, int i1) {
                indicator.draw(i, i1);
            }

            @Override
            public void onPageSelected(int i) {
                paged = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (i == 1)
                    stateFlag = true;

                if (i == 0 && stateFlag) {
                    stateFlag = false;
                    switch (paged) {
                        case 0:
                            radioGroup.check(R.id.radio_1);
                            break;
                        case 1:
                            radioGroup.check(R.id.radio_2);
                            break;
                        case 2:
                            radioGroup.check(R.id.radio_3);
                            break;
                    }
                }
            }
        };
        pager.setOnPageChangeListener(onPageChangeListener);
    }


    private class SignAdapter extends FragmentPagerAdapter {

        private Fragment[] fragments;

        public SignAdapter(FragmentManager fm) {
            super(fm);
            fragments = new Fragment[TAB_NUM];
        }

        @Override
        public Fragment getItem(int arg0) {
            if (fragments[arg0] == null) {
                if (arg0 == 0)
                    fragments[arg0] = new Fragment1();
                else if (arg0 == 1)
                    fragments[arg0] = new Fragment2();
                else if (arg0 == 2)
                    fragments[arg0] = new Fragment3();
            }
            return fragments[arg0];
        }

        @Override
        public int getCount() {
            return TAB_NUM;
        }
    }
}
