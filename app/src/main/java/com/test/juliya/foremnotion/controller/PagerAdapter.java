package com.test.juliya.foremnotion.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.test.juliya.foremnotion.fragment.ListFragment;
import com.test.juliya.foremnotion.fragment.MainFragment;
import com.test.juliya.foremnotion.fragment.MapFragment;
import com.test.juliya.foremnotion.model.CardModel;

/**
 * Created by juliya on 22.04.2017.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    private UpdateListCallback listener;
    private Fragment[] items = new Fragment[2];

    public PagerAdapter(FragmentManager fm, UpdateListCallback listener) {
        super(fm);
        this.listener = listener;
        items[0] = MapFragment.getInstance(listener);
        items[1] = ListFragment.getInstance();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
            case 1:
                return items[position];

            default:
                return items[0];
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void updateFragment(CardModel cardModel) {
        ((ListFragment)getItem(1)).updateList(cardModel);
        notifyDataSetChanged();
    }
}
