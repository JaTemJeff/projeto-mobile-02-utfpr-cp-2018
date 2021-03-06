package com.example.jeff.daf.modelo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Jeff on 09/06/2018.
 */

@DatabaseTable
public class Modo {

    @DatabaseField(generatedId=true)
    private int id_modo;

    @DatabaseField
    private String nome_modo;

    @DatabaseField
    private int frequencia_modo;

    @DatabaseField
    private int delay_modo;

    @DatabaseField
    private boolean bluetooth_modo;

    @DatabaseField
    private  boolean notificacao_modo;

    public int getId_modo() {
        return id_modo;
    }

    public void setId_modo(int id_modo) {
        this.id_modo = id_modo;
    }

    public String getNome_modo() {
        return nome_modo;
    }

    public void setNome_modo(String nome_modo) {
        this.nome_modo = nome_modo;
    }

    public int getFrequencia_modo() {
        return frequencia_modo;
    }

    public void setFrequencia_modo(int frequencia_modo) {
        this.frequencia_modo = frequencia_modo;
    }

    public int getDelay_modo() {
        return delay_modo;
    }

    public void setDelay_modo(int delay_modo) {
        this.delay_modo = delay_modo;
    }

    public boolean isBluetooth_modo() {
        return bluetooth_modo;
    }

    public void setBluetooth_modo(boolean bluetooth_modo) {
        this.bluetooth_modo = bluetooth_modo;
    }

    public boolean isNotificacao_modo() {
        return notificacao_modo;
    }

    public void setNotificacao_modo(boolean notificacao_modo) {
        this.notificacao_modo = notificacao_modo;
    }
    @Override
    public String toString(){
        return getNome_modo();
    }
}
