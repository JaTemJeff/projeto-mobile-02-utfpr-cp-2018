package com.example.jeff.daf;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.jeff.daf.persistencia.DatabaseHelper;
import com.example.jeff.daf.persistencia.DatabaseManager;
import com.example.jeff.daf.modelo.Modo;
import com.example.jeff.daf.utils.UtilsGUI;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<Modo> listaAdapter;
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
    private Spinner selecionaCor;
    private String idModo;

    private ConstraintLayout layout;
    private static final String ARQUIVO = "preferencias_cor";
    private static final String COR     = "COR";

    private int opcao = Color.BLUE;
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
                exibeDelay.setText(i + " Ms");
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
                exibeFrequencia.setText(i + " Mhz");
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

        /*
        final Modo modocasa = new Modo();
        modocasa.setNome_modo("Modo Casa");
        modocasa.setFrequencia_modo(5);
        modocasa.setDelay_modo(2);
        modocasa.setBluetooth_modo(false);
        modocasa.setNotificacao_modo(true);
        if(dao.findById(modocasa.getId_modo()).toString() == null){
            dao.create(modocasa);
        }*/

        popularListaSpiner();

        final EditText nomeModo = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        nomeModo.setLayoutParams(lp);

        botaoNovoModo = findViewById(R.id.button_novo_modo_id);
        botaoNovoModo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View view)  {
                confirmaSalvarModorDialog = new AlertDialog.Builder(MainActivity.this);
                confirmaSalvarModorDialog.setTitle(R.string.criar_modo);

                /*confirmaSalvarModorDialog.setMessage("Digite o nome do modo");
                confirmaSalvarModorDialog.setCancelable(false);
                nomeModo.setText("Digite o nome do modo");
                inputDelay.setText(getText(seekbarDelay.getProgress()).toString());
                inputFrequencia.setText(getText(seekbarFrequencia.getProgress()).toString());*/

                confirmaSalvarModorDialog.setMessage(R.string.nome_do_modo);
                nomeModo.setText("");
                if(nomeModo.getParent()!=null)
                    ((ViewGroup)nomeModo.getParent()).removeView(nomeModo); // <- fix
                confirmaSalvarModorDialog.setView(nomeModo);

                /*confirmaSalvarModorDialog.setView(inputDelay);
                confirmaSalvarModorDialog.setView(inputFrequencia);*/

                confirmaSalvarModorDialog.setIcon(android.R.drawable.ic_input_add);
                confirmaSalvarModorDialog.setPositiveButton(R.string.salvar_modo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final Modo novoModo = new Modo();
                        ModoDAO dao = new ModoDAO(MainActivity.this);
                        novoModo.setNome_modo(nomeModo.getText().toString());
                        novoModo.setFrequencia_modo(seekbarFrequencia.getProgress());
                        novoModo.setDelay_modo(seekbarDelay.getProgress());
                        novoModo.setNotificacao_modo(checkboxnotificacao.isChecked());
                        novoModo.setBluetooth_modo(switchativarbluetooth.isChecked());
                        String nome  = UtilsGUI.validaCampoTexto(MainActivity.this, nomeModo, R.string.nome_vazio);
                        if (nome == null){
                            return;
                        }
                        try {
                            DatabaseHelper conexao = DatabaseHelper.getInstance(MainActivity.this);
                            conexao.getModoDao().create(novoModo);
                            setResult(Activity.RESULT_OK);
                            //finish();

                        } catch (android.database.SQLException e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, R.string.salvar_modo, Toast.LENGTH_SHORT).show();
                            return;
                        } catch (java.sql.SQLException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(MainActivity.this, R.string.salvo_sucesso_modo, Toast.LENGTH_SHORT).show();
                        popularListaSpiner();

                    }
                });
                confirmaSalvarModorDialog.setNegativeButton(R.string.cancelar_modo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Modo não foi salvo.", Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_CANCELED);

                    }
                });
                confirmaSalvarModorDialog.create();
                confirmaSalvarModorDialog.show();
            }
        });

        layout = findViewById(R.id.layout_principal_id);
        lerPreferenciaCor();
        //muda cor de fundo
        selecionaCor = findViewById(R.id.spinner_cor_id);
        ArrayAdapter adapterCor = ArrayAdapter.createFromResource(this, R.array.itens_spiner_cor, android.R.layout.simple_spinner_item);
        selecionaCor.setAdapter(adapterCor);
        selecionaCor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (selecionaCor.getSelectedItemPosition()) {
                    case 0:
                        break;

                    case 1:
                        salvarPreferenciaCor(Color.WHITE);
                        break;

                    case 2:
                        salvarPreferenciaCor(Color.YELLOW);
                        break;

                    case 3:
                        salvarPreferenciaCor(Color.GRAY);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        selecionaModo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            Modo modoSpiner = new Modo();

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                modoSpiner = (Modo) adapterView.getItemAtPosition(i);
                seekbarDelay.setMax(0);
                seekbarDelay.setMax(10);
                seekbarDelay.setProgress(modoSpiner.getDelay_modo());
                seekbarFrequencia.setMax(0);
                seekbarFrequencia.setMax(4);
                seekbarFrequencia.setProgress(modoSpiner.getFrequencia_modo());
                switchativarbluetooth.setChecked(modoSpiner.isBluetooth_modo());
                checkboxnotificacao.setChecked(modoSpiner.isNotificacao_modo());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        popularListaSpiner();
    }

    private void popularListaSpiner(){

        List<Modo> lista = null;

        try {
            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            lista = conexao.getModoDao()
                    .queryBuilder()
                    .orderBy("nome_modo", true)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        listaAdapter = new ArrayAdapter<Modo>(this,
                android.R.layout.simple_list_item_1,
                lista);

        selecionaModo.setAdapter(listaAdapter);
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

   //Shared preferences
    private void mudaCorFundo(){
        layout.setBackgroundColor(opcao);
    }

    private void lerPreferenciaCor(){
        SharedPreferences shared =
                getSharedPreferences(ARQUIVO,
                        Context.MODE_PRIVATE);

        opcao = shared.getInt(COR, opcao);

        mudaCorFundo();
    }

    private void salvarPreferenciaCor(int novoValor){

        SharedPreferences shared =
                getSharedPreferences(ARQUIVO,
                        Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putInt(COR, novoValor);

        editor.commit();

        opcao = novoValor;

        mudaCorFundo();
    }
}

