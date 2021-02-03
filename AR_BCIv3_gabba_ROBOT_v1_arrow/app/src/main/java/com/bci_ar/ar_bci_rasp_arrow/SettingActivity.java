package com.bci_ar.ar_bci_rasp_arrow;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;

public class SettingActivity extends AppCompatActivity {

    private SharedPreferences shPref;
    private Spinner color_square1;
    private Spinner color_square2;
    private Spinner color_background;
    private Spinner freq_square1;
    private Spinner freq_square2;
    private EditText x_square1;
    private EditText y_square1;
    private EditText z_square1;
    private EditText x_square2;
    private EditText y_square2;
    private EditText z_square2;

    private EditText SERVER_IP0;
    private EditText SERVER_IP1;
    private EditText SERVER_IP2;
    private EditText SERVER_IP3;
    private EditText SERVER_PORT;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        shPref = getSharedPreferences(getString(R.string.shared_preferences),MODE_PRIVATE);

        SERVER_IP0= (EditText) findViewById(R.id.editText_IP0);
        SERVER_IP1= (EditText) findViewById(R.id.editText_IP1);
        SERVER_IP2= (EditText) findViewById(R.id.editText_IP2);
        SERVER_IP3= (EditText) findViewById(R.id.editText_IP3);
        SERVER_PORT= (EditText) findViewById(R.id.editText_PORT);




        color_square1=(Spinner)  findViewById(R.id.spinner_color_square1);
        color_square2=(Spinner)  findViewById(R.id.spinner_color_square2);
        color_background= (Spinner) findViewById(R.id.spinner_background);

        x_square1= (EditText) findViewById(R.id.editText_Square1_x);
        y_square1= (EditText) findViewById(R.id.editText_Square1_y);
        z_square1= (EditText) findViewById(R.id.editText_Square1_z);
        freq_square1= (Spinner) findViewById(R.id.spinner_Square1_freq);

        x_square2= (EditText) findViewById(R.id.editText_Square2_x);
        y_square2= (EditText) findViewById(R.id.editText_Square2_y);
        z_square2= (EditText) findViewById(R.id.editText_Square2_z);
        freq_square2= (Spinner) findViewById(R.id.spinner_Square2_freq);

    }

    @Override
    protected void onResume(){
        super.onResume();


        int color1 = shPref.getInt("Color_square1",0);
        int color2 = shPref.getInt("Color_square2",0);
        int color_background_position = shPref.getInt("Color_background",1);
        int freq1 = shPref.getInt("Freq_square1",1);
        int freq2 = shPref.getInt("Freq_square2",0);

        SERVER_IP0.setText(shPref.getString("ServerIP0","192"));
        SERVER_IP1.setText(shPref.getString("ServerIP1","168"));
        SERVER_IP2.setText(shPref.getString("ServerIP2","1"));
        SERVER_IP3.setText(shPref.getString("ServerIP3","11"));

        SERVER_PORT.setText(shPref.getString("ServerPORT","4444"));



        color_square1.setSelection(color1);
        color_square2.setSelection(color2);
        color_background.setSelection(color_background_position);

        x_square1.setText(shPref.getString("X_square1",getString(R.string.X1)));
        y_square1.setText(shPref.getString("Y_square1",getString(R.string.Y1)));
        z_square1.setText(shPref.getString("Z_square1",getString(R.string.Z1)));
        freq_square1.setSelection(freq1);

        x_square2.setText(shPref.getString("X_square2",getString(R.string.X2)));
        y_square2.setText(shPref.getString("Y_square2",getString(R.string.Y2)));
        z_square2.setText(shPref.getString("Z_square2",getString(R.string.Z2)));
        freq_square2.setSelection(freq2);

    }

    @Override
    protected void onPause(){
        super.onPause();

        // Save persistence data
        // Write to Shared Preferences (config_file)
        SharedPreferences.Editor editor= shPref.edit();

        editor.putString("ServerIP0",SERVER_IP0.getText().toString());
        editor.putString("ServerIP1",SERVER_IP1.getText().toString());
        editor.putString("ServerIP2",SERVER_IP2.getText().toString());
        editor.putString("ServerIP3",SERVER_IP3.getText().toString());
        editor.putString("ServerPORT",SERVER_PORT.getText().toString());



        editor.putInt("Color_square1",color_square1.getSelectedItemPosition());
        editor.putInt("Color_square2",color_square2.getSelectedItemPosition());
        editor.putInt("Color_background",color_background.getSelectedItemPosition());
//      editor.putString("Color_background",color_background.getSelectedItem().toString());

        editor.putString("X_square1",x_square1.getText().toString());
        editor.putString("Y_square1",y_square1.getText().toString());
        editor.putString("Z_square1",z_square1.getText().toString());
        editor.putInt("Freq_square1",freq_square1.getSelectedItemPosition());
        editor.putString("Freq_square1_string",freq_square1.getSelectedItem().toString());


        editor.putString("X_square2",x_square2.getText().toString());
        editor.putString("Y_square2",y_square2.getText().toString());
        editor.putString("Z_square2",z_square2.getText().toString());
        editor.putInt("Freq_square2",freq_square2.getSelectedItemPosition());
        editor.putString("Freq_square2_string",freq_square2.getSelectedItem().toString());

        editor.apply();




    }


    // ------------------ by Gabba ------------------

    // Funzione richiamata quando viene cliccato sul bottone 'Impostazioni'
    public void applySetting(View view){

        // Write to Shared Preferences (config_file)
        SharedPreferences.Editor editor= shPref.edit();

        editor.putString("ServerIP0",SERVER_IP0.getText().toString());
        editor.putString("ServerIP1",SERVER_IP1.getText().toString());
        editor.putString("ServerIP2",SERVER_IP2.getText().toString());
        editor.putString("ServerIP3",SERVER_IP3.getText().toString());
        editor.putString("ServerPORT",SERVER_PORT.getText().toString());


        editor.putInt("Color_square1",color_square1.getSelectedItemPosition());
        editor.putInt("Color_square2",color_square2.getSelectedItemPosition());
        editor.putInt("Color_background",color_background.getSelectedItemPosition());
//      editor.putString("Color_background",color_background.getSelectedItem().toString());

        editor.putString("X_square1",x_square1.getText().toString());
        editor.putString("Y_square1",y_square1.getText().toString());
        editor.putString("Z_square1",z_square1.getText().toString());
        editor.putInt("Freq_square1",freq_square1.getSelectedItemPosition());
        editor.putString("Freq_square1_string",freq_square1.getSelectedItem().toString());


        editor.putString("X_square2",x_square2.getText().toString());
        editor.putString("Y_square2",y_square2.getText().toString());
        editor.putString("Z_square2",z_square2.getText().toString());
        editor.putInt("Freq_square2",freq_square2.getSelectedItemPosition());
        editor.putString("Freq_square2_string",freq_square2.getSelectedItem().toString());


        editor.apply();

        Toast.makeText(getApplicationContext(), "Settings applied", Toast.LENGTH_SHORT).show();

    }

}
