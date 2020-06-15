package com.fuenteoro.ciclismo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.fuenteoro.ciclismo.InfoIndex.*;

public class InfoIndexActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_index);

        mSectionsPagerAdapter =  new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    private static class PlaceholderFragment extends Fragment {
        private static final String ARG_STRING_NUMBER = "section_number";

        private PlaceholderFragment(){
        }

        private static Fragment newInstance(int sectionNumber){

            Fragment fragment = null;
            switch (sectionNumber){
                case 1:
                    fragment = new Fragment_PagOne();
                    break;
                case 2:
                    fragment = new Fragment_PagTwo();
                    break;
                case 3:
                    fragment = new Fragment_PagThree();
                    break;
                case 4:
                    fragment = new Fragment_PagFour();
                    break;
            }

            return fragment;
        }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_pag_one, container, false);
            return rootView;
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SectionsPagerAdapter (FragmentManager fm){
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount(){
            //Total de páginas
            return 4;
        }
    }
}