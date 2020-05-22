package com.br.dentin.memoria.fragmentos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.br.dentin.R;
import com.br.dentin.memoria.JogoMemoriaActivity;

public class MemoriaMenuFragment extends Fragment {

    private  String[] menuArr = {"Fácil - 2x2","Médio - 4x4","Difícil - 6x6"};
    int[] flags = new int[]{
            R.drawable.account_voice,
            R.drawable.volume_off_white_36,
            R.drawable.help_white_36,
    };


    private  int []   levelArr= {2,4,6}; //2x2 , 4x4, 6x6 grid size
    private  int []   timer = {30000,45000,60000}; // millsecond for timer
    private  int []   getPointsFromOneMatching = {20,40,60}; //by the level,get point to final result if there is 2 card matching.

    private Context context;
    private int resultRequested;
    private int resultOk;
    private String avatar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.memoria_menu_fragment, container, false);
        context = getActivity();

        resultRequested = this.getArguments().getInt("result_request");
        resultOk = this.getArguments().getInt("result_ok");
        avatar = this.getArguments().getString("avatar");

        ListView lstViewNivel = view.findViewById(R.id.listViewNivel);
        ArrayAdapter<String> adapterArr = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_2, menuArr);
        lstViewNivel.setAdapter(adapterArr);

        lstViewNivel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // move to the game activity
                Intent i = new Intent(context, JogoMemoriaActivity.class);
                i.putExtra("row", levelArr[position]);
                i.putExtra("col", levelArr[position]);
                i.putExtra("time", timer[position]);
                i.putExtra("point", getPointsFromOneMatching[position]);
                i.putExtra("avatar", avatar);

                startActivityForResult(i, resultRequested);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == resultRequested) {
            if (resultCode == resultOk) {
                String result = data.getStringExtra("result");
                //Toast.makeText(context, "onActivityResult" + result, Toast.LENGTH_LONG).show();
            }
        }
        //Toast.makeText(context, "onActivityResult" + requestCode, Toast.LENGTH_LONG).show();
    }
}
