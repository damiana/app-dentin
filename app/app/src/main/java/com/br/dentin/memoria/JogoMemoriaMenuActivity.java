package com.br.dentin.memoria;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.br.dentin.R;
import com.br.dentin.memoria.fragmentos.MemoriaMenuFragment;
import com.br.dentin.memoria.fragmentos.Tab2Fragment;
import com.br.dentin.memoria.fragmentos.TabAdapter;
import com.google.android.material.tabs.TabLayout;

public class JogoMemoriaMenuActivity extends AppCompatActivity {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    static final int RESULT_REQUEST = 1;
    static final int RESULT_OK = -1;

    private int[] tabIcons = {
            R.drawable.gamepad_variant_outline,
            R.drawable.account_voice,
            //R.drawable.star
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogo_memoria_menu);


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        MemoriaMenuFragment memoriaFragment = new MemoriaMenuFragment();

        adapter = new TabAdapter(getSupportFragmentManager(), this);
        adapter.addFragment(memoriaFragment, "Memoria Menu", tabIcons[0]);
        adapter.addFragment(new Tab2Fragment(), "Tab 2", tabIcons[1]);
        //adapter.addFragment(new Tab3Fragment(), “Tab 3”, tabIcons[2]);

        Intent intent = getIntent();
        String avatar = intent.getStringExtra("avatar");

        Bundle bundleMenu = new Bundle();
        bundleMenu.putInt("result_request",RESULT_REQUEST);
        bundleMenu.putInt("result_ok",RESULT_OK);
        bundleMenu.putString("avatar", avatar);

        memoriaFragment.setArguments(bundleMenu);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        highLightCurrentTab(0);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                highLightCurrentTab(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void highLightCurrentTab(int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(adapter.getTabView(i));
        }
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        assert tab != null;
        tab.setCustomView(null);
        tab.setCustomView(adapter.getSelectedTabView(position));
    }
}
