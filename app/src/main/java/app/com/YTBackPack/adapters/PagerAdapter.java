package app.com.YTBackPack.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.com.YTBackPack.MainActivity;
import app.com.YTBackPack.fragments.ChannelFragment;
import app.com.YTBackPack.fragments.LiveFragment;
import app.com.YTBackPack.fragments.PlayListFragment;
import app.com.YTBackPack.fragments.VideoFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }



    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
            bundle.putCharSequence("SearchKey", MainActivity.SearchKey);
            bundle.putBoolean("WindowMode",MainActivity.isInWindowMode);

        switch (position) {
            case 0:{
                VideoFragment tab = new VideoFragment();
                tab.setArguments(bundle);
                return tab;
            }
            case 1:
                ChannelFragment tab1 = new ChannelFragment();
                tab1.setArguments(bundle);
                return tab1;
            case 2:
                PlayListFragment tab2 = new PlayListFragment();
                tab2.setArguments(bundle);
                return tab2;
            case 3:
                LiveFragment tab3 = new LiveFragment();
                tab3.setArguments(bundle);
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
