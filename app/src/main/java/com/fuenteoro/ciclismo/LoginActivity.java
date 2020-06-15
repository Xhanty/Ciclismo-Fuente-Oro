package com.fuenteoro.ciclismo;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuenteoro.ciclismo.Log_Reg.*;
import com.google.android.material.tabs.TabLayout;

public class LoginActivity extends AppCompatActivity {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSectionsPagerAdapter =  new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.containerlogin);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
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
                    fragment = new Fragment_Login();
                    break;
                case 2:
                    fragment = new Fragment_Register();
                    break;
            }

            return fragment;
        }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
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
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position){
            return getResources().getString(TAB_TITLES[position]);
        }

    }
}
