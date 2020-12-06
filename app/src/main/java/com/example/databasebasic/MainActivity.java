package com.example.databasebasic;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SimpleDatabaseHelper helper = null;
    private EditText txtIsbn = null;
    private EditText txtTitle = null;
    private EditText txtPrice = null;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new SimpleDatabaseHelper(this);
        txtIsbn = findViewById(R.id.txtIsbn);
        txtTitle = findViewById(R.id.txtTitle);
        txtPrice = findViewById(R.id.txtPrice);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onSave(View view) {
        // 保存ボタン押下時処理
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            /*
            登録データをContentValuesオブジェクトにまとめる
            →ContentValuesは、データを列名/値のセットで管理するオブジェクト
             */
            ContentValues cv = new ContentValues();
            cv.put("isbn", txtIsbn.getText().toString());
            cv.put("title", txtTitle.getText().toString());
            cv.put("price", txtPrice.getText().toString());
            /*
            登録/更新
            →第四引数 conflictAlgorithm(=データが重複した時の処理)には、重複した時、該当の行を置き換えるよう指定
             */
            db.insertWithOnConflict("books", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            Toast.makeText(this, "データの登録に成功しました。",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onSearch(View view) {
        // 検索ボタン押下時処理
        String[] cols = {"isbn", "title", "price"};
        String[] params = {txtIsbn.getText().toString()};
        /*
        queryメソッド
        第二引数 String[] columns・・・取得する列
        第三引数 String selection・・・条件式
        第四引数 String[] selectionArgs・・・条件値(第三引数の条件式に使用する値)
         */
        try (SQLiteDatabase db = helper.getReadableDatabase();
             Cursor cs = db.query("books", cols, "isbn = ?",
                     params, null, null, null, null)){
            /*
            Cursorオブジェクト・・・テーブルから取り出したデータ(結果のセット)を保持、そのデータの読み取り手段を提供するオブジェクト
            →queryメソッドによる検索結果がCursor型の変数csに格納されている
             */
            if (cs.moveToFirst()){
                /*
                CursorクラスのmoveToFirstメソッドによって、結果のセットの先頭行に、カーソルを移動する
                →先頭行に移動できる(=データが存在する)場合はtrueを、先頭行に移動できない(=データが存在しない)場合はfalseを返す
                 */
                txtTitle.setText(cs.getString(1));
                txtPrice.setText(cs.getString(2));
                /*
                現在カーソルが示す行(=カレントレコード)から列の値取り出す為、getXXXメソッドを使用
                →取得するカラムの型に応じて、XXXを変更する
                　引数のcolumnIndexは、0から始まる。そのため、今回はタイトルと金額のカラムの値を取り出したい為
                　作成したテーブルの2番目、3番目(title,price)を指定する
                 */
            } else {
                Toast.makeText(this, "データがありません。",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onDelete(View view) {
        // 削除ボタン押下時処理
        try (SQLiteDatabase db = helper.getWritableDatabase()){
            String[] params = {txtIsbn.getText().toString()};
            /*
            deleteメソッド
            第二引数 whereClause・・・削除の条件式
            第三引数 whereArgs・・・バインド変数に渡す値
             */
            db.delete("books", "isbn = ?", params);
            Toast.makeText(this, "データの削除に成功しました。",
                    Toast.LENGTH_SHORT).show();
        }
    }
}