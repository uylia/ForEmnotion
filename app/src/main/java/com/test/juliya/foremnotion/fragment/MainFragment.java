package com.test.juliya.foremnotion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.juliya.foremnotion.R;
import com.test.juliya.foremnotion.controller.PagerAdapter;
import com.test.juliya.foremnotion.controller.UpdateListCallback;
import com.test.juliya.foremnotion.model.CardModel;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juliya on 22.04.2017.
 */

public class MainFragment extends Fragment implements UpdateListCallback {
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.pager)
    ViewPager viewPager;

    private PagerAdapter pagerAdapter;

    public static MainFragment getInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, view);
        initTabs();
        initPager();
        return view;
    }

    private void initTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.title_tab1)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.title_tab2)));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initPager() {
        pagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onGetNewPoint(CardModel cardModel) {
        pagerAdapter.updateFragment(cardModel);
    }
}
