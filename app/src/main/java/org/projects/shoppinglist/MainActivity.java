package org.projects.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MyDialogFragment.OnPositiveListener, AdapterView.OnItemSelectedListener {
    MyDialogFragment dialog;
    Context context;

    private final int RESULT_CODE_PREFERENCES = 1;

    //save array
    Map<Integer, Product> savedCopies = new HashMap<>();

    ArrayAdapter<Product> adapter;
    ListView listView;
    ArrayList<Product> bag = new ArrayList<>();

    public ArrayAdapter getMyAdapter()
    {
        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Needed to get the toolbar to work on older versions
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Did we have stuff in our bag?
        if(savedInstanceState != null) {
            ArrayList<Product> savedBag = savedInstanceState.getParcelableArrayList("savedBag");
            if(savedBag != null) {
                bag = savedBag;
            }
        }

        //Populate spinner
        final Spinner spinner = (Spinner) findViewById(R.id.inputMeasurement);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
        R.array.measurement_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        //getting our listiew - you can check the ID in the xml to see that it
        //is indeed specified as "list"
        listView = findViewById(R.id.list);
        //here we create a new adapter linking the bag and the
        //listview
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, bag);
        //setting the adapter on the listview
        listView.setAdapter(adapter);
        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //Get input item
        final EditText inputText = findViewById(R.id.inputItem);
        final EditText inputQty = findViewById(R.id.inputSize);

        Button addButton =  findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //adapter.add(inputQty.getText().toString() + " x " + inputText.getText().toString());
                if(inputText.length() > 0 && inputQty.length() > 0) {
                    adapter.add(new Product(inputText.getText().toString(), Integer.parseInt(inputQty.getText().toString()), spinner.getSelectedItem().toString()));
                    resetInputFields();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please fill out all values", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }

    public void saveProductCopy(int position, Product product) {
        savedCopies.put(position, product);
    }

    public void reAddDeletedProducts() {
        for ( Map.Entry<Integer, Product> entry : savedCopies.entrySet()) {
            int key = entry.getKey();
            Product value = entry.getValue();
            // do something with key and/or tab
            bag.add(key, value);
        }
        savedCopies.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            //Start our settingsactivity and listen to result - i.e.
            //when it is finished.
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivityForResult(intent,RESULT_CODE_PREFERENCES);
            //notice the 1 here - this is the code we then listen for in the
            //onActivityResult
        }

        else if (id == R.id.action_delete) {
            SparseBooleanArray positions = listView.getCheckedItemPositions();
            //Check if items are available to be deleted
            if (bag.size() != 0){
                for(int i = bag.size() -1; i > -1; i--) {
                    if(positions.get(i)) {
                        System.out.println("Removed: " + i);
                        saveProductCopy(i, bag.get(i));
                        bag.remove(i);
                    }
                }

                Snackbar snackbar = Snackbar
                        .make(listView, "Items have been deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //This code will ONLY be executed in case that
                                //the user has hit the UNDO button
                                reAddDeletedProducts();
                                adapter.notifyDataSetChanged();
                            }
                        });
                if(positions.size() > 0) {
                    snackbar.show();
                }

                //Clear selection and notify adapter of changes
                listView.clearChoices();
                adapter.notifyDataSetChanged();
                resetInputFields();
            }
        }

        else if (id == R.id.action_clear) {
            showDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    //This is the event handler for the show button
    //This is specified in the xml file that this should
    //be the event handler
    public void showDialog() {
        //showing our dialog.
        dialog = new MyDialog();
        //Here we show the dialog
        //The tag "MyFragement" is not important for us.
        dialog.show(getFragmentManager(), "MyFragment");
    }

    @Override
    public void onPositiveClicked() {
        if (bag.size() != 0){
            bag.clear();
            listView.clearChoices();
            adapter.notifyDataSetChanged();
            resetInputFields();
        }
    }

    public static class  MyDialog extends MyDialogFragment {
        @Override
        protected void negativeClick() {
            //Here we override the method and can now do something
            Toast toast = Toast.makeText(getActivity(),
                    "negative button clicked", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected retrieve the selected item using
        parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void resetInputFields() {
        final EditText inputText = findViewById(R.id.inputItem);
        inputText.setText("");
    }

    public void updateUI(String name)
    {
        TextView myName = findViewById(R.id.myName);
        myName.setText(name);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==RESULT_CODE_PREFERENCES) //the code means we came back from settings
        {
            //I can can these methods like this, because they are static
            String name = MyPreferenceFragment.getName(this);
            String message = "Welcome, "+name;
            Toast toast = Toast.makeText(this,message,Toast.LENGTH_LONG);
            toast.show();
            updateUI(name);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //This method is called before our activity is destroyed
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
		/* Here we put code now to save the state */
        outState.putParcelableArrayList("savedBag", bag);
    }
}
