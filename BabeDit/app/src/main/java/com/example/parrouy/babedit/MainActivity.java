package com.example.parrouy.babedit;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private Button button;
    private EditText mot;
    public static String EXTRA_MESSAGE="RecordActivity";
    private static final int DELETE_ID = Menu.FIRST;
    private String[] listeSon = new String[0];
    private ArrayList<String> liste = new ArrayList<String>();
    private ArrayAdapter<String> arrayAdapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String test = Environment.getExternalStorageDirectory().getAbsolutePath();
        test+="/SonsBabeDit/";
        File dir = new File(test);

        lv = (ListView) findViewById(R.id.listeSons);
        liste = listerRepertoire(dir);
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                liste);

        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                goToListenActivity(liste.get(position));
                //finish();
            }
        });

        mot = (EditText) findViewById(R.id.saisi);
        button = (Button) findViewById(R.id.valider);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mot.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(),"Renseigner le nom du fichier",Toast.LENGTH_SHORT).show();
                else
                    goToRecordActivity();
            }
        });
        registerForContextMenu(lv);

    }

    public void goToRecordActivity(){
        Intent intent = new Intent(this, RecordActivity.class);
        String message = mot.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void goToListenActivity(String message){
        Intent intent = new Intent(this, ListenActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu,v,menuInfo);
        menu.add(0,DELETE_ID, 0, "Supprimer ce fichier");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                long l = info.id;
                int id = (int) l;
                deleteFichier(id);
        }
        return super.onContextItemSelected(item);
    }

    public ArrayList<String> listerRepertoire(File repertoire){
        String [] listefichiers;

        int i;
        listefichiers=repertoire.list();
        for(i=0;i<listefichiers.length;i++){
            if(listefichiers[i].endsWith(".3gp")==true){
                liste.add(listefichiers[i]);
                Log.e("fichier",listefichiers[i]);
            }
        }
        return liste;
    }

    public void deleteFichier(int id){
        String test = Environment.getExternalStorageDirectory().getAbsolutePath();
        test+="/SonsBabeDit/"+liste.get(id).toString();

        String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        dir += "/SonsBabeDit/";
        File directory = new File(dir);

        Log.e("delete",test);
        File filetodelete = new File(test);
        try {
            filetodelete.getCanonicalFile().delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        liste.clear();
        liste=listerRepertoire(directory);
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                liste);

        lv.setAdapter(arrayAdapter);

    }

}
