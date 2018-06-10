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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.example.jeff.daf.database.ModoDAO;
import com.example.jeff.daf.persistencia.DatabaseManager;
import com.example.jeff.daf.modelo.Modo;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private Button botaoIniciar;
    private Button botaoParar;
    private Button botaoNovoModo;
    private AlertDialog.Builder confirmaSalvarModorDialog;
    private SeekBar seekbarFrequencia;
    private SeekBar seekbarDelay;
    private TextView exibeDelay;
    private TextView exibeFrequencia;
    private Switch switchativarbluetooth;
    private CheckBox checkboxnotificacao;
    private Spinner selecionaModo;
    /*
    private EditText nomeModo;
    private EditText inputFrequencia;
    private EditText inputDelay;*/

    private static final String ARQUIVO_PREFERENCIA = "ArqPreferencia";

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

        DatabaseManager.init(this);
        ModoDAO dao = null;
        dao = new ModoDAO(this);


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
                exibeDelay.setText("Delay: " + i + " Ms");
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
                exibeFrequencia.setText("Frequencia: " + i + " Mhz");
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

        final Modo modocasa = new Modo();
        if(modocasa.getNome_modo() == null){
            modocasa.setNome_modo("Modo Casa");
            modocasa.setFrequencia_modo(5);
            modocasa.setDelay_modo(2);
            modocasa.setBluetooth_modo(false);
            modocasa.setNotificacao_modo(true);
            dao.create(modocasa);
        }

        selecionaModo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (selecionaModo.getSelectedItem().toString()) {
                    case "Modo Casa":
                        seekbarDelay.setMax(0);
                        seekbarDelay.setMax(10);
                        seekbarDelay.setProgress(modocasa.getDelay_modo());
                        seekbarFrequencia.setMax(0);
                        seekbarFrequencia.setMax(4);
                        seekbarFrequencia.setProgress(modocasa.getFrequencia_modo());
                        switchativarbluetooth.setChecked(modocasa.isBluetooth_modo());
                        checkboxnotificacao.setChecked(modocasa.isNotificacao_modo());
                        break;


                    case "Modo Apresentação":
                        seekbarDelay.setMax(0);
                        seekbarDelay.setMax(10);
                        seekbarDelay.setProgress(5);
                        seekbarFrequencia.setMax(0);
                        seekbarFrequencia.setMax(4);
                        seekbarFrequencia.setProgress(2);
                        switchativarbluetooth.setChecked(true);
                        checkboxnotificacao.setChecked(true);
                        break;

                    case "Nenhum Modo Selecionado":
                        seekbarDelay.setMax(0);
                        seekbarDelay.setMax(10);
                        seekbarDelay.setProgress(0);
                        seekbarFrequencia.setMax(0);
                        seekbarFrequencia.setMax(4);
                        seekbarFrequencia.setProgress(0);
                        switchativarbluetooth.setChecked(false);
                        checkboxnotificacao.setChecked(false);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        final EditText nomeModo = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        nomeModo.setLayoutParams(lp);

        botaoNovoModo = findViewById(R.id.button_novo_modo_id);
        botaoNovoModo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)  {
                confirmaSalvarModorDialog = new AlertDialog.Builder(MainActivity.this);
                confirmaSalvarModorDialog.setTitle("Criar novo modo");
                //confirmaSalvarModorDialog.setMessage("Digite o nome do modo");
                //confirmaSalvarModorDialog.setCancelable(false);
                //nomeModo.setText("Digite o nome do modo");
                //inputDelay.setText(getText(seekbarDelay.getProgress()).toString());
                //inputFrequencia.setText(getText(seekbarFrequencia.getProgress()).toString());
                confirmaSalvarModorDialog.setMessage("Digite o nome do modo:");
                confirmaSalvarModorDialog.setView(nomeModo);
                //confirmaSalvarModorDialog.setView(inputDelay);
                //confirmaSalvarModorDialog.setView(inputFrequencia);
                confirmaSalvarModorDialog.setIcon(android.R.drawable.ic_input_add);

                confirmaSalvarModorDialog.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final Modo novoModo = new Modo();
                        ModoDAO dao = new ModoDAO(MainActivity.this);
                        novoModo.setNome_modo(nomeModo.getText().toString());
                        novoModo.setFrequencia_modo(seekbarFrequencia.getProgress());
                        novoModo.setDelay_modo(seekbarDelay.getProgress());
                        novoModo.setNotificacao_modo(checkboxnotificacao.isChecked());
                        novoModo.setBluetooth_modo(switchativarbluetooth.isChecked());

                        try{
                            dao.create(novoModo);
                        }
                        catch (android.database.SQLException e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Falha ao salvar modo.", Toast.LENGTH_SHORT).show();
                        }

                        Toast.makeText(MainActivity.this, "Novo modo salvo com sucesso.", Toast.LENGTH_SHORT).show();
                    }
                });
                confirmaSalvarModorDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Modo não foi salvo.", Toast.LENGTH_SHORT).show();
                    }
                });
                confirmaSalvarModorDialog.create();
                confirmaSalvarModorDialog.show();
            }
        });


        botaoIniciar = findViewById(R.id.botao_iniciar_id);
        botaoIniciar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //boolean mStartRecording = true;
                //boolean mStartPlaying = true;

                //onRecord(mStartRecording);
                //onPlay(mStartPlaying);
            }
        });



        botaoParar = findViewById(R.id.botao_parar_id);
        botaoParar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //stopRecording();
               // stopPlaying();
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

