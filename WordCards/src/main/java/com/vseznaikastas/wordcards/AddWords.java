package com.vseznaikastas.wordcards;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import android.os.StrictMode;

public class AddWords extends FragmentActivity implements UpdateDialog.NoticeDialogListener{
    private SQLiteAdapter mySQLiteAdapter;
    ListView listView;
    Cursor cursor;
    SimpleCursorAdapter scAdapter;


    EditText wordEdit;
    EditText translateEdit;
    Button createCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_words_layout);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Put translate
        // Set your Windows Azure Marketplace client info
        Translate.setClientId("");
        Translate.setClientSecret("");


        wordEdit = (EditText) findViewById(R.id.edit_word);
        translateEdit = (EditText) findViewById(R.id.edit_translate);
        createCard = (Button) findViewById(R.id.create_card);
        //hide soft keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        translateEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Toast.makeText(getApplicationContext(), "got the focus", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });


        //write network call in Main UI Thread
        /*if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/

//        mySQLiteAdapter = new SQLiteAdapter(this);
//        mySQLiteAdapter.openToWrite();
//        mySQLiteAdapter.deleteAll();
//        mySQLiteAdapter.insert(new Word("House","Дом"));
//        mySQLiteAdapter.insert(new Word("Car","Машина"));
//        mySQLiteAdapter.insert(new Word("Dog","Собака"));
//        mySQLiteAdapter.insert(new Word("ываыав","dsfgs"));
//        mySQLiteAdapter.insert(new Word("Rock","Камень"));
//        mySQLiteAdapter.insert(new Word("Sea","Море"));


//        mySQLiteAdapter.insert("House","Дом");
//        mySQLiteAdapter.insert("Car","Машина");
//        mySQLiteAdapter.insert("Dog","Собака");
//        mySQLiteAdapter.insert("Sun","Солнце");
//        mySQLiteAdapter.insert("Rock","Камень");
//        mySQLiteAdapter.insert("Sea","Море");
//        mySQLiteAdapter.close();

        mySQLiteAdapter = new SQLiteAdapter(this);
        mySQLiteAdapter.openToWrite();
        cursor = mySQLiteAdapter.getAll();


        // формируем столбцы сопоставления
        String[] from = new String[] { SQLiteAdapter.KEY_WORD, SQLiteAdapter.KEY_TRANSLATE};
        int[] to = new int[] { R.id.list_text1, R.id.list_text2};


        //создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.list_item,
                cursor, from, to, 0);

        listView = (ListView)findViewById(R.id.listView);
        //listView = (ListView) findViewById(R.id.myListView);
        listView.setAdapter(scAdapter);


        registerForContextMenu(listView);
    }


    public void createCard(View view) throws Exception {
        String word = wordEdit.getText().toString().trim();
        String translate = translateEdit.getText().toString().trim();


        if("".equals(word)){
            Toast.makeText(this,"Put the word",Toast.LENGTH_LONG).show();
        }
        else if(mySQLiteAdapter.checkTheWord(word)){
            Toast.makeText(this,"This word is already in database",Toast.LENGTH_LONG).show();
        }
        else if(!"".equals(translate)){
            Word newWord = new Word(word,translate);
            mySQLiteAdapter.insert(newWord);
            refreshListOfWords();
        //}else if(isOnline()){
        //   GetTranslateFromNetwork addNewCard = new GetTranslateFromNetwork();
        //    addNewCard.execute(word, translate);
        }else{
            Toast.makeText(this,"Network connection is lost or word field is empty",Toast.LENGTH_LONG).show();
        }
        finishInput();
    }


    // Clear input field and hide display keyboard
    private void finishInput() {
        wordEdit.getText().clear();
        translateEdit.getText().clear();

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mgr.hideSoftInputFromWindow(wordEdit.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(translateEdit.getWindowToken(), 0);
    }

    //Async task class for create new card and add it to stack
    class GetTranslateFromNetwork extends AsyncTask<String, Void, Word> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mySQLiteAdapter.deleteAll();
            createCard.setEnabled(false);
        }

        @Override
        protected Word doInBackground(String... params) {
            Word newWord = null;
            try {
                String word = params[0];
                String translate = Translate.execute(params[0], Language.ENGLISH, Language.RUSSIAN);
                newWord = new Word(word,translate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return newWord;
        }

        @Override
        protected void onPostExecute(Word newWord) {
            super.onPostExecute(newWord);
            mySQLiteAdapter.insert(newWord);
            refreshListOfWords();
            createCard.setEnabled(true);

        }
    }

    //Check network
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_words, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ctx_edit_list, menu);
        //menu.setHeaderTitle("Контекстное меню");
        //menu.add(0, Menu.FIRST, Menu.NONE, "Удалить запись");
    }




    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //Определяем имя пункта на котором было вызвано контекстное меню
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.ctx_delete_word:
                //String name = ((TextView) info.targetView).getText().toString();
                //удаляем выбраный пункт списка
                Word w = new Word(info.id);
                mySQLiteAdapter.deleteItem(w);

                refreshListOfWords();
                return true;
            case R.id.ctx_edit_word:
                showEditDialog(info.id);
        }

        return super.onContextItemSelected(item);
    }

    private void showEditDialog(long idOfSelectedWord){
        FragmentManager fm = getSupportFragmentManager();
        UpdateDialog updateDialog = new UpdateDialog();
        Bundle args = new Bundle();
        args.putLong("idOfWord", idOfSelectedWord);
        updateDialog.setArguments(args);
        updateDialog.show(fm, "UpdateDialog");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mySQLiteAdapter.close();
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Bundle mArgs = dialog.getArguments();
        long myValue = mArgs.getLong("idOfWord");

        Dialog dialogView = dialog.getDialog();
        EditText ed = (EditText) dialogView.findViewById(R.id.username);
        String s = String.valueOf(ed.getText());

        //Change list item
        Word w = new Word(myValue);
        w.setTranslate(s);
        mySQLiteAdapter.updateItem(w);

        refreshListOfWords();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    //Refresh list of words
    private void refreshListOfWords(){
        //mySQLiteAdapter.insert(new Word("Dog","Собака"));
        cursor = mySQLiteAdapter.getAll();
        scAdapter.changeCursor(cursor);
        scAdapter.notifyDataSetChanged();
        //listView.setAdapter(scAdapter);
    }
}

