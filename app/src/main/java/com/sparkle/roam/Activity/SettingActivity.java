package com.sparkle.roam.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.sparkle.roam.R;
import com.sparkle.roam.Utils.Constants;
import com.sparkle.roam.Utils.MyPref;

public class SettingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    private Spinner spinner;
    private static final String[] paths = { "Account ID", "Name", "DueDate"};
    private MyPref myPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        myPref = new MyPref(this);
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        spinner = (Spinner) findViewById(R.id.spinner);

        toolbar.setTitle("Settings");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingActivity.this, R.layout.spinner_item, paths);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition("Account ID");
        spinner.setSelection(spinnerPosition);
        if (myPref.getPref(Constants.SPINER_POSITION,0) == 0) {
            spinner.setSelection(0);
        }else if (myPref.getPref(Constants.SPINER_POSITION,1) == 1){
            spinner.setSelection(1);
        }else if (myPref.getPref(Constants.SPINER_POSITION,2) == 2){
            spinner.setSelection(2);
        }
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if(adapterView.getId() == R.id.spinner)
        {
            switch (position){
                case 0:
                    myPref.setPref(Constants.SPINER_POSITION,position);
                    myPref.setPref(Constants.SPINER_SELECTION,Constants.SPINER_AccID);
                    break;
                case 1:
                    myPref.setPref(Constants.SPINER_POSITION,position);
                    myPref.setPref(Constants.SPINER_SELECTION,Constants.SPINER_Name);
                    break;
                case 2:
                    myPref.setPref(Constants.SPINER_POSITION,position);
                    myPref.setPref(Constants.SPINER_SELECTION,Constants.SPINER_DueDate);
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
