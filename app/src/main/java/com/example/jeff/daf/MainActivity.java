package com.example.jeff.daf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Button botaoIniciar;
    private Button botaoSobre;
    private Button botaoPreferencias;
    private AlertDialog.Builder confirmaPararDialog;
    private SeekBar seekbarFrequencia;
    private SeekBar seekbarDelay;
    private TextView exibeDelay;
    private TextView exibeFrequencia;
    private Switch switchativarbluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*Chama activity sobre*/
        botaoSobre = findViewById(R.id.botao_sobre_id);
        botaoSobre.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, SobreActivity.class));
            }
        });

        /*Chama activity preferencias*/
        botaoPreferencias = findViewById(R.id.botao_preferncias_id);
        botaoPreferencias.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, PreferenciasActivity.class));
            }
        });

        //Texto de Exibição dos Seekbar's
        seekbarFrequencia = findViewById(R.id.seekbar_frequencia_id);
        seekbarDelay = findViewById(R.id.seekbar_delay_id);
        exibeDelay = findViewById(R.id.textview_exibe_delay_id);
        exibeFrequencia = findViewById(R.id.textview_exibe_frequencia_id);

        seekbarDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                exibeDelay.setText("Delay:"+ i);
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
                exibeFrequencia.setText("Frequencia:"+ i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /*Mensagens e confirmações do botão iniciar*/
        botaoIniciar = findViewById(R.id.botao_iniciar_id);
        botaoIniciar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                confirmaPararDialog = new AlertDialog.Builder(MainActivity.this);
                confirmaPararDialog.setTitle("Deseja parar a reprodução?");
                confirmaPararDialog.setMessage("Ao pressionar sim a reprodução será interrompida.");
                confirmaPararDialog.setCancelable(false);
                confirmaPararDialog.setIcon(android.R.drawable.ic_lock_silent_mode);

                confirmaPararDialog.setNegativeButton("Não",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MainActivity.this, "Reproduzindo.", Toast.LENGTH_SHORT).show();
                            }
                        });

                confirmaPararDialog.setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MainActivity.this, "Reprodução interrompida.", Toast.LENGTH_SHORT).show();
                            }
                        });
                confirmaPararDialog.create();
                confirmaPararDialog.show();
            }

        });
    }
}
