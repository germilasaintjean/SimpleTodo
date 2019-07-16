package com.example.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public final static int EDIT_REQUEST_CODE = 20;
    public final static String ITEM_TEXT = "itemTEXT";
    public final static String ITEM_POSITION = "itemPosition";

    java.util.ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    EditText etNewItem;
    private org.apache.commons.io.FileUtils FilesUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readItems();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListenner();
    }

    private void readItems() {
        try {
            items = new ArrayList<String>(FileUtils.readLines(getDataFile(), String.valueOf(Charset.defaultCharset())));

        } catch (IOException e) {
            Log.e( "MainActivity", "Error reading file", e);
            items = new ArrayList<String>();
        }
    }


    private void setupListViewListenner() {
        Log.i("MainActivity", "Setting up Listenner on List view");
        lvItems.setOnItemLongClickListener((parent, view, position, id) -> {
            Log.i("MainActivity", "Item removed from List:" + position);
            items.remove(position);
            itemsAdapter.notifyDataSetChanged();
            writeItems();
            return true;

        });
        lvItems.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(MainActivity.this, EditItemActivity.class);
            i.putExtra(ITEM_TEXT, items.get(position));
            i.putExtra(ITEM_POSITION, position);
            startActivityForResult(i, EDIT_REQUEST_CODE);
        });

    }

    private void writeItems() {
        try {
            FilesUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing file", e);
//            e.printStackTrace();
        }
    }

    private File getDataFile() {
        return  new File(getFilesDir(), "todo.txt");
    }

    public void onAddItem(View view) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        etNewItem.setText("");
        writeItems();
        Toast.makeText(getApplicationContext(), "Item added to list", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE) {
            String updatedItem = data.getExtras().getString(ITEM_TEXT);
            int position = data.getExtras().getInt(ITEM_POSITION);
            items.set(position, updatedItem);
            itemsAdapter.notifyDataSetChanged();
            writeItems();
            Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
        }

    }
}


