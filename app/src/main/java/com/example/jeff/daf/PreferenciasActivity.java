package com.example.jeff.daf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.example.jeff.daf.modelo.Modo;
import com.example.jeff.daf.persistencia.DatabaseHelper;
import com.example.jeff.daf.utils.UtilsGUI;

import java.util.List;

import static android.os.Build.ID;

public class PreferenciasActivity extends AppCompatActivity {

    private ListView listViewModo;
    private ArrayAdapter<Modo> listaAdapter;

    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;
    private AlertDialog.Builder exibeModo;

    private static final int REQUEST_NOVO_MODO    = 1;
    private static final int REQUEST_ALTERAR_MODO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        listViewModo = findViewById(R.id.listview_modo_id);
        listViewModo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Modo modo = (Modo) adapterView.getItemAtPosition(i);
                PreferenciasActivity.alterar(PreferenciasActivity.this, REQUEST_ALTERAR_MODO, modo);
            }

        });

        popularLista();

        registerForContextMenu(listViewModo);

    }

    public static void alterar(Activity activity, int requestCode, Modo modo){

        Intent intent = new Intent(activity, PreferenciasActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, modo.getId_modo());

        activity.startActivityForResult(intent, ALTERAR);
    }

    public static void nova(Activity activity, int requestCode){

        Intent intent = new Intent(activity, PreferenciasActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, NOVO);
    }


    private void popularLista(){

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

        listViewModo.setAdapter(listaAdapter);
    }

    private void excluirModo(final Modo modo){

        String mensagem = getString(R.string.deseja_realmente_apagar)
                + "\n" + modo.getNome_modo();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                try {
                                    DatabaseHelper conexao = DatabaseHelper.getInstance(PreferenciasActivity.this);

                                    conexao.getModoDao().delete(modo);

                                    listaAdapter.remove(modo);

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                } catch (java.sql.SQLException e) {
                                    e.printStackTrace();
                                }

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };
        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }

    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == REQUEST_NOVO_MODO || requestCode == REQUEST_ALTERAR_MODO)
                && resultCode == Activity.RESULT_OK){

            popularLista();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_modos, menu);
        return true;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.lista_modos, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Modo modo = (Modo) listViewModo.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menu_item_alterar_id:
                PreferenciasActivity.alterar(this,
                        REQUEST_ALTERAR_MODO,
                        modo);
                return true;


            case R.id.menu_item_apagar_id:
                excluirModo(modo);
                return true;

            case R.id.menu_item_abrir_id:
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

}
