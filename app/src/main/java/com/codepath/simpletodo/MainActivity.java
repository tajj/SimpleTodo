package com.codepath.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public static final int EDIT_REQUEST_CODE = 20;
    public static final String ITEM_TEXT = "itemText";
    public static final String ITEM_POSITION = "itemPosition";

    //declaring stateful objects
    ArrayList<String>items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //reference to listview
        lvItems = (ListView) findViewById(R.id.lvItems);

        //initialize items list
readItems();
        //initialize adapter using items list
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);

        //wire adapter to view
        lvItems.setAdapter(itemsAdapter);

        //mock item
        //items.add("First to do item");
       // items.add("Second todo item");

        //setup listener on creation
        setupListViewListener();
    }

    private void setupListViewListener(){
        //set itemLongClickListener
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                               @Override
                                               public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                                   items.remove(position);
                                                   itemsAdapter.notifyDataSetChanged();
                                                   Log.i("MainActivity", "Removed item " + position);
                                                   writeItems();
                                                   return true;

                                               }
                                           });
          lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener()

            {
                @Override
                public void onItemClick (AdapterView < ? > parent, View view,int position, long id){
                // first parameter is the context, second is the class of the activity to launch
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                // put "extras" into the bundle for access in the edit activity
                i.putExtra(ITEM_TEXT, items.get(position));
                i.putExtra(ITEM_POSITION, position);
                // brings up the edit activity with the expectation of a result
                startActivityForResult(i, EDIT_REQUEST_CODE);

               }


            });
    }


    public void onAddItem(View v) {
        //obtain refrence from EditText
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);

        //grab Edittext's content as string
        String itemText = etNewItem.getText().toString();

        //add item to list via adapter
        itemsAdapter.add(itemText);

        //clear Editetext setting ti to empty string
        etNewItem.setText("");

        writeItems();

        //toast
        Toast.makeText(getApplicationContext(), "Item added to list", Toast.LENGTH_SHORT).show();

    }

    //returns file where datas stored
    private File getDataFile(){
        return new File(getFilesDir(), "todo.txt");


    }

    //read items from file
    private void readItems(){
        try{
            //make array w content from file
            items = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e){
            //print error to console
            e.printStackTrace();
            //just load empty list
            items = new ArrayList<>();

        }

    }

    private void writeItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing file", e);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // EDIT_REQUEST_CODE defined with constants
        if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE) {
            // extract updated item value from result extras
            String updatedItem = data.getExtras().getString(ITEM_TEXT);
            // get the position of the item which was edited
            int position = data.getExtras().getInt(ITEM_POSITION, 0);
            // update the model with the new item text at the edited position
            items.set(position, updatedItem);
            // notify the adapter the model changed
            itemsAdapter.notifyDataSetChanged();
            // notify the user the operation completed OK
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        }
    }

}
