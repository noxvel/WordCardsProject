package com.vseznaikastas.wordcards;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class SQLiteAdapter {

    public static final String DATABASE_NAME = "WORDS_DATABASE";
    public static final String DATABASE_TABLE = "WORDS_TABLE";
    public static final int DATABASE_VERSION = 1;
    public static final String KEY_ID = "_id";
    public static final int ID_COLUMN = 0;

    // Название и номер п/п (индекс) каждого поля
    public static final String KEY_WORD = "Word";
    public static final int WORD_COLUMN = 1;

    public static final String KEY_TRANSLATE = "Translate";
    public static  final int TRANSLATE_COLUMN = 2;

    //create table MY_DATABASE (ID integer primary key, Content text not null);
    private static final String SCRIPT_CREATE_DATABASE =
            "create table " + DATABASE_TABLE + " (" + KEY_ID
                    + " integer primary key autoincrement, " + KEY_WORD + " text not null, "
                    + KEY_TRANSLATE + " text not null);";

    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase sqLiteDatabase;

    private Context context;

    private Cursor cursor;

    public SQLiteAdapter(Context c){
        context = c;
    }

    public SQLiteAdapter openToRead() throws android.database.SQLException {
        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        cursor = getAll();
        return this;
    }

    public SQLiteAdapter openToWrite() throws android.database.SQLException {
        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        cursor = getAll();
        return this;
    }

    public void close(){
        sqLiteHelper.close();
    }

    public long insert(Word word){

        ContentValues values = new ContentValues();
        values.put(KEY_WORD, word.getWord());
        values.put(KEY_TRANSLATE, word.getTranslate());
        return sqLiteDatabase.insert(DATABASE_TABLE, null, values);
    }




    public boolean deleteAll(){
        return sqLiteDatabase.delete(DATABASE_TABLE, null, null) > 0;
    }

    public boolean deleteItem(Word wordToRemove) {
        boolean isDeleted = (sqLiteDatabase.delete(DATABASE_TABLE, KEY_ID + "=?",
                new String[] { wordToRemove.getStringId() })) > 0;
        //refresh();
        return isDeleted;
    }
/*
    public boolean updateItem(long id, String newName) {
        ContentValues values = new ContentValues();
        values.put(KEY_NOTE, newName);
        boolean isUpdated = (database.update(TABLE_NAME, values, KEY_ID + "=?",
                new String[] {id+""})) > 0;
        return isUpdated;
    }
*/
    public boolean updateItem(Word wordToEdit) {

        ContentValues values = new ContentValues();
        //values.put(KEY_WORD, wordToEdit.getWord());
        values.put(KEY_TRANSLATE, wordToEdit.getTranslate());
        boolean isUpdated = (sqLiteDatabase.update(DATABASE_TABLE, values, KEY_ID + "=?",
                new String[] {wordToEdit.getStringId()})) > 0;
        return isUpdated;
}

    //Вызывает обновление вида
    private void refresh() {
        cursor = getAll();
    }

    //Check the word in database
    public boolean checkTheWord(String word){
        boolean isWordIn = false;

        String[] columns = new String[]{KEY_WORD};
        Cursor cursor = sqLiteDatabase.query(DATABASE_TABLE, columns,
                null, null, null, null, null);

        int index_CONTENT = cursor.getColumnIndex(KEY_WORD);
        for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
            if(word.equals(cursor.getString(index_CONTENT))){
                isWordIn = true;
            }
        }
        return isWordIn;
    }

/*
    public String queueAll(){
        String[] columns = new String[]{KEY_CONTENT};
        Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns,
                null, null, null, null, null);
        String result = "";

        int index_CONTENT = cursor.getColumnIndex(KEY_CONTENT);
        for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
            result = result + cursor.getString(index_CONTENT) + "\n";
        }

        return result;
    }
*/
    public Word getItem(int position) {
        if (cursor.moveToPosition(position)) {
            long id = cursor.getLong(ID_COLUMN);
            String word = cursor.getString(WORD_COLUMN);
            String translate = cursor.getString(TRANSLATE_COLUMN);
            Word selectedWord = new Word(id , word, translate);
            return selectedWord;
        } else {
            throw new CursorIndexOutOfBoundsException(
                    "Cant move cursor to position");
        }
    }


    public Cursor getAll(){
        String[] result_columns = new String[] {KEY_ID, KEY_WORD, KEY_TRANSLATE};
        Cursor allRows = sqLiteDatabase.query(true, DATABASE_TABLE, result_columns,
                null, null, null, null, null, null);
        return  allRows;

    }


    public long count() {
        return DatabaseUtils.queryNumEntries(sqLiteDatabase, DATABASE_TABLE);
    }

    //Helper class
    public class SQLiteHelper extends SQLiteOpenHelper {

        public SQLiteHelper(Context context, String name,
                            CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(SCRIPT_CREATE_DATABASE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }

    }

}