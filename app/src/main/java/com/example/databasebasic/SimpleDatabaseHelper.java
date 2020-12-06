package com.example.databasebasic;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

public class SimpleDatabaseHelper extends SQLiteOpenHelper {
    static final private String DBNAME = "sample.sqlite";
    static final private int VERSION = 1;

    // コンストラクタ
    SimpleDatabaseHelper(Context context){
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
    }

    /**
     * 【概要】 データベース作成時にテーブルとテストデータを作成
     * @param db データベース
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create文
        db.execSQL("CREATE TABLE books(" +
                "isbn TEXT PRIMARY KEY, title TEXT, price INTEGER)");
        // 登録データ
        String[] isbns = {
                "978-4-7980-4512-2", "978-4-7980-4179-7", "978-4-7741-8030-4",
                "978-4-7741-9617-6", "978-4-7981-3547-2"};
        String[] titles = {
                "テスト１", "テスト２", "テスト３", "テスト４", "テスト５"};
        int[] prices = {3000, 3500, 2680, 2780, 3200};

        // トランザクションを開始
        db.beginTransaction();
        try {
            // insert文準備
            // ?→バインド変数
            SQLiteStatement sql = db.compileStatement(
                    "INSERT INTO books(isbn, title, price) VALUES(?, ?, ?)"
            );
            // 値を順番に代入しながら、insertを実行
            for (int i = 0; i < isbns.length; i++){
                // 準備したinsert文の、1番目の？に、変数isbnsのi番目の要素をセット
                sql.bindString(1, isbns[i]);
                // 準備したinsert文の、2番目の？に、変数titlesのi番目の要素をセット
                sql.bindString(2, titles[i]);
                // 準備したinsert文の、3番目の？に、変数pricesのi番目の要素をセット
                sql.bindLong(3, prices[i]);
                sql.executeInsert();
            }
            // トランザクションを成功
            db.setTransactionSuccessful();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            // トランザクションを終了
            db.endTransaction();
        }

    }

    /**
     * 【概要】 データベースをバージョンアップした時、テーブルを再作成
     * @param db データベース
     * @param oldVersion 古いバージョン
     * @param newVersion 新しいバージョン
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS books");
        onCreate(db);
    }
}
