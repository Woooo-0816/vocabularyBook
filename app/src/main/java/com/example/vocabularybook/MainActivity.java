package com.example.vocabularybook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    private ArrayList<Word> words = new ArrayList<>();
    private MyDatabaseHelper dbHelper;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("帮助：");
                dialog.setMessage("长按可以删除" + "\n" + "横屏也可使用");
                dialog.setPositiveButton("确定", null).show();
                break;
            default:
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dbHelper = new MyDatabaseHelper(this, "WordBook.db", null, 2);

        try {
            dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        words = initWords();
        final WordAdapter adapter = new WordAdapter(MainActivity.this, R.layout.word, words);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Word word = words.get(i);
                try {
                    RightFragment rightFragment = (RightFragment) getSupportFragmentManager().findFragmentById(R.id.right_fragment);
                    rightFragment.refresh(word);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, word.getExplain(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        //长摁删除
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final Word word = words.get(i);

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("删除提醒！");
                dialog.setMessage("确定要删除所选单词？");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String ENG = word.getContent();
                        String CHN = word.getExplain();
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete("Word", "ENG = ? AND CHN = ?", new String[]{ENG, CHN});
                        Toast.makeText(MainActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                        try {
                            words.clear();
                            SQLiteDatabase db1 = dbHelper.getWritableDatabase();
                            //Cursor cursor = db.rawQuery("Select * from Word", null);
                            String[] coiumns = {"ENG", "CHN", "SEN"};
                            Cursor cursor = db1.query("Word", coiumns, null, null, null, null, null);
                            if (cursor.moveToFirst()) {
                                do {
                                    words.add(new Word(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
                                } while (cursor.moveToNext());
                            }
                            cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ListView listView = findViewById(R.id.list_view);
                        listView.setAdapter(adapter);
                        refresh();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dialog.show();
                return true;
            }
        });


        //添加按钮
        try {
            Button new_btn = findViewById(R.id.new_btn);
            new_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                    LayoutInflater layoutInflater = getLayoutInflater();
                    final View view1 = layoutInflater.inflate(R.layout.addword, null);
                    builder.setView(view1);
                    builder.setTitle("添加单词");
                    builder.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText edit_eng_txt = view1.findViewById(R.id.addWord);
                            EditText edit_chn_txt = view1.findViewById(R.id.addExp);
                            EditText edit_sen_txt = view1.findViewById(R.id.addSen);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            Word newWord = new Word(edit_eng_txt.getText().toString(), edit_chn_txt.getText().toString(), edit_sen_txt.getText().toString());
                            try {
                                values.put("ENG", newWord.getContent());
                                values.put("CHN", newWord.getExplain());
                                values.put("SEN", newWord.getSentence());
                                db.insert("Word", null, values);
                                Toast.makeText(MainActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                            }

                            try {
                                words.clear();
                                SQLiteDatabase db1 = dbHelper.getWritableDatabase();
                                //Cursor cursor = db.rawQuery("Select * from Word", null);
                                String[] coiumns = {"ENG", "CHN", "SEN"};
                                Cursor cursor = db1.query("Word", coiumns, null, null, null, null, null);
                                if (cursor.moveToFirst()) {
                                    do {
                                        words.add(new Word(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
                                    } while (cursor.moveToNext());
                                }
                                cursor.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ListView listView = findViewById(R.id.list_view);
                            listView.setAdapter(adapter);
                        }
                    });
                    builder.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  修改按钮
        try {
            Button fix_btn = findViewById(R.id.fix_btn);
            fix_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                    LayoutInflater layoutInflater = getLayoutInflater();
                    final View view1 = layoutInflater.inflate(R.layout.addword, null);
                    builder.setView(view1);
                    builder.setTitle("修改");
                    builder.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText edit_eng_txt = view1.findViewById(R.id.addWord);
                            EditText edit_chn_txt = view1.findViewById(R.id.addExp);
//                            EditText edit_sen_txt = view1.findViewById(R.id.addSen);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            try {
                                ContentValues values = new ContentValues();
                                values.put("CHN", edit_chn_txt.getText().toString());
                                db.update("Word", values, "ENG = ?", new String[]{edit_eng_txt.getText().toString()});
                                try {
                                    words.clear();
                                    SQLiteDatabase db1 = dbHelper.getWritableDatabase();
                                    String[] coiumns = {"ENG", "CHN", "SEN"};
                                    Cursor cursor = db1.query("Word", coiumns, null, null, null, null, null);
                                    if (cursor.moveToFirst()) {
                                        do {
                                            words.add(new Word(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
                                        } while (cursor.moveToNext());
                                    }
                                    cursor.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                            }
                            ListView listView = findViewById(R.id.list_view);
                            listView.setAdapter(adapter);
                            refresh();
                        }
                    });
                    builder.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        //模糊查询
        try {
            Button fuzzy_que_btn = findViewById(R.id.fuzzy_que_btn);
            fuzzy_que_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    words.clear();
                    EditText text = findViewById(R.id.query);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    try {
                        Cursor cursor = db.rawQuery("Select * from Word where CHN = ?", new String[]{text.getText().toString()});

                        if (cursor.moveToFirst()) {
                            do {
                                words.add(new Word(cursor.getString(cursor.getColumnIndex("ENG")),cursor.getString(cursor.getColumnIndex("CHN")), cursor.getString(cursor.getColumnIndex("SEN"))));
//                            Log.e("messagesss","word");
                            } while (cursor.moveToNext());
                        }
                        cursor.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    refresh();
                    ListView listView = findViewById(R.id.list_view);
                    listView.setAdapter(adapter);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        EditText text = findViewById(R.id.query);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                EditText text = findViewById(R.id.query);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    words.clear();
                    String str = "%" + text.getText().toString() + "%";
                    Cursor cursor = db.rawQuery("Select * from Word where ENG like ?", new String[]{str});
                    if (cursor.moveToFirst()) {
                        do {
                            words.add(new Word(cursor.getString(cursor.getColumnIndex("ENG")),cursor.getString(cursor.getColumnIndex("CHN")), cursor.getString(cursor.getColumnIndex("SEN"))));
//                            Log.e("messagesss","word");
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "查无此词", Toast.LENGTH_SHORT).show();
                }
                ListView listView = findViewById(R.id.list_view);
                listView.setAdapter(adapter);
            }
        });
    }


    private void refresh() {
        try {
            RightFragment rightFragment = (RightFragment) getSupportFragmentManager().findFragmentById(R.id.right_fragment);
            rightFragment.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Word> initWords() {
        ArrayList<Word> words = new ArrayList<>();
//        words.add(new Word("hello", "你好", "hello,myfriend"));
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //Cursor cursor = db.rawQuery("Select * from Word", null);
            String[] coiumns = {"ENG", "CHN", "SEN"};
            Cursor cursor = db.query("Word", coiumns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    words.add(new Word(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return words;
    }


}
