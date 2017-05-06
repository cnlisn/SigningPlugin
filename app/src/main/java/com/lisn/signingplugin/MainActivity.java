package com.lisn.signingplugin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CrashHandler.getInstance().init(getApplicationContext());
        Button bt_lx = (Button) findViewById(R.id.bt_lx);
        bt_lx.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.bt_lx){
            Intent intent = new Intent(this, SigningActivity.class);
//            startActivityForResult(intent,188);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==188){
            Log.e("---", "onActivityResult: "+data.getStringExtra("qm_path")+"==="+data.getStringExtra("Sp_path") );
        }
    }
}
