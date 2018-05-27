package com.example.jeff.daf;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private Button botaoIniciar;
    private Button botaoParar;
    private AlertDialog.Builder confirmaPararDialog;
    private SeekBar seekbarFrequencia;
    private SeekBar seekbarDelay;
    private TextView exibeDelay;
    private TextView exibeFrequencia;
    private Switch switchativarbluetooth;
    private CheckBox checkboxnotificacao;
    Spinner selecionaModo;

    //Gravacao de audio
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = null;

    //private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    //private PlayButton   mPlayButton = null;
    private MediaPlayer   mPlayer = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";


        //Texto de Exibição dos Seekbar's
        seekbarFrequencia = findViewById(R.id.seekbar_frequencia_id);
        seekbarDelay = findViewById(R.id.seekbar_delay_id);
        exibeDelay = findViewById(R.id.textview_exibe_delay_id);
        exibeFrequencia = findViewById(R.id.textview_exibe_frequencia_id);
        switchativarbluetooth = findViewById(R.id.switch_headset_bluetooh_id);
        checkboxnotificacao = findViewById(R.id.checkbox_notificacao_id);


        seekbarDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                exibeDelay.setText("Delay:" + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbarFrequencia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                exibeFrequencia.setText("Frequencia:" + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Itens do spinner
        selecionaModo = findViewById(R.id.spinner_id);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.itens_spiner, android.R.layout.simple_spinner_item);
        selecionaModo.setAdapter(adapter);

        switch (selecionaModo.getSelectedItem().toString()) {
            case "Modo Casa":
                seekbarDelay.setProgress(5);
                seekbarFrequencia.setProgress(2);
                switchativarbluetooth.setSelected(false);
                checkboxnotificacao.setSelected(false);

            case "Modo Apresentação":
                seekbarDelay.setProgress(5);
                seekbarFrequencia.setProgress(2);
                switchativarbluetooth.setSelected(true);
                checkboxnotificacao.setSelected(true);


            case "Modo Personalizado":
                seekbarDelay.setProgress(0);
                seekbarFrequencia.setProgress(0);
                switchativarbluetooth.setSelected(false);
                checkboxnotificacao.setSelected(false);
        }

        botaoIniciar = findViewById(R.id.botao_iniciar_id);
        botaoIniciar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                /*
                boolean mStartRecording = true;
                boolean mStartPlaying = true;

                onPlay(mStartPlaying);
                onRecord(mStartRecording);
                */
            }

        });

        botaoParar = findViewById(R.id.botao_parar_id);
        botaoIniciar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //stopPlaying();
                //stopRecording();
            }

        });

    }

    //Infla Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    //Chama activity's do menu de opções
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.opcao_menu_sobre_id:
                startActivity(new Intent(MainActivity.this, SobreActivity.class));
                return true;
            case R.id.opcao_menu_preferencias_id:
                startActivity(new Intent(MainActivity.this, PreferenciasActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}

