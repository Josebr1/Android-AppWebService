package br.com.treinaweb.appwebservice;


import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;

import android.os.AsyncTask;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    SimpleCursorAdapter simpleCursorAdapter;
    ProgressBar progressBar;
    Button btnCarregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnCarregar = (Button) findViewById(R.id.button);

    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            String[] from = {
              ClienteDbHelper.C_ID, ClienteDbHelper.C_NOME, ClienteDbHelper.C_EMAIL
            };

            int[] to = {
              R.id.txtId, R.id.txtNome, R.id.txtEmail
            };

            simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.list_clientes, null, from, to, 0);
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(simpleCursorAdapter);
            LoaderManager mLoader = getLoaderManager();
            mLoader.initLoader(1, null, loaderManager);
        }catch (Exception e){
            Log.i("ErroStart", e.getMessage());
        }

    }


    public void carregar(View view){
        progressBar.setVisibility(View.VISIBLE);
        btnCarregar.setClickable(false);
        new buscaClientAsync().execute("http://theplanet.besaba.com/clientes.json");
    }

    private LoaderManager.LoaderCallbacks<Cursor> loaderManager = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getApplicationContext(),
                    ClienteProvider.CONTENT_URI, null, null, null,
                    ClienteDbHelper.C_NOME + " ASC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if(loader.getId() == 1){
                simpleCursorAdapter.swapCursor(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            simpleCursorAdapter.swapCursor(null);
        }
    };

    public String readJson(String url) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try{
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if(statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                String line;
                while ((line = reader.readLine()) != null){
                    stringBuilder.append(line);
                }

            }else{
                Log.i("Json", "Erro durante o download do arquivo");
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    private class buscaClientAsync extends AsyncTask<String, Void, Integer>{

        @Override
        protected Integer doInBackground(String... params) {

            try{
                String jsonText = readJson(params[0]);
                JSONArray jsonArray = new JSONArray(jsonText);
                ContentResolver resolver = getContentResolver();

                resolver.delete(ClienteProvider.CONTENT_URI, null, null);

                for(int i =0; i < jsonArray.length(); i++){
                    JSONObject data = jsonArray.getJSONObject(i);
                    String nome = data.getString("nome");
                    String email = data.getString("email");

                    ContentValues values = new ContentValues();
                    values.put(ClienteDbHelper.C_NOME, nome);
                    values.put(ClienteDbHelper.C_EMAIL, email);
                    Log.i("Json:", values.toString());
                    resolver.insert(ClienteProvider.CONTENT_URI, values);
                }
            }catch (Exception e){

            }

            return null;

        }

        @Override
        protected void onPostExecute(Integer integer) {

            progressBar.setVisibility(View.INVISIBLE);
            btnCarregar.setClickable(true);

            LoaderManager loader = getLoaderManager();
            loader.restartLoader(1, null, loaderManager);

        }
    }

}
