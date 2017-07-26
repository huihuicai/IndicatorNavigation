package com.huihuicai.wynne.indicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.huihuicai.wynne.indicator.view.CustomTabLayout;

public class TabPagerActivity extends AppCompatActivity {

    private CustomTabLayout tlNavigation;
    private ViewPager viewPager;
    private String[] titles = {"技术部", "销售部", "运营部", "测试部", "质管部", "吃饭部"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_pager);
        tlNavigation = (CustomTabLayout) findViewById(R.id.tl_navigation);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(mAdapter);
        tlNavigation.initIndicator(viewPager, titles);
    }

    private FragmentStatePagerAdapter mAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int i) {
            return BaseFragment.getInstance(titles[i]);
        }

        @Override
        public int getCount() {
            return titles.length;
        }
    };
}
