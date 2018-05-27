package org.projects.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MyDialogFragment.OnPositiveListener, AdapterView.OnItemSelectedListener {
    MyDialogFragment dialog;

    private final int RESULT_CODE_PREFERENCES = 1;
    //Firebase authentication
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    //firebase
    DatabaseReference firebaseRoot;
    DatabaseReference firebaseUserRoot;
    DatabaseReference firebase;

    //CrashLytics
    //Crashlytics.getInstance().crash(); // Force a crash


    //save array
    Map<String, Product> savedCopies = new HashMap<>();

    //Adapter
    ArrayAdapter<CharSequence> spinnerAdapter;
    Spinner spinner;

    //Preference
    FirebaseListAdapter<Product> adapter;
    ListView listView;
    ArrayList<Product> bag = new ArrayList<>();

    public FirebaseListAdapter<Product> getMyAdapter() {
        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase authentication
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            //Not signed in, launch the Sign In Activity and close the MainAcitivty
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        firebaseRoot = FirebaseDatabase.getInstance().getReference();
        firebaseUserRoot = FirebaseDatabase.getInstance().getReference().child("/users/" + mFirebaseUser.getUid() + "/items");
        firebase = FirebaseDatabase.getInstance().getReference().child("/users/" + mFirebaseUser.getUid() + "/items");


        //Set theme according to time of the day / night
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);

        //Needed to get the toolbar to work on older versions
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Fetch one item from firebase - In this case, we fetch the name of the user if available.
        FirebaseDatabase.getInstance().getReference().child("/users/" + mFirebaseUser.getUid() + "/username").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String shoppingName = dataSnapshot.getValue(String.class);
                        if(shoppingName != null) {
                            String foundName = shoppingName;
                            if(foundName != null) {
                                getSupportActionBar().setTitle(foundName + "'s Shopping List");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

        boolean chosenUnit = MyPreferenceFragment.chooseUnit(this);

        //Did we have stuff in our bag?
        // Not needed with firebase
//        if(savedInstanceState != null) {
//            ArrayList<Product> savedBag = savedInstanceState.getParcelableArrayList("savedBag");
//            if(savedBag != null) {
//                bag = savedBag;
//            }
//        }

        // Get spinner in view
        spinner = (Spinner) findViewById(R.id.inputMeasurement);
        // Populate spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        // Set unit according to preference
        updateUI(chosenUnit);

        //Get input item
        final EditText inputText = findViewById(R.id.inputItem);
        final EditText inputQty = findViewById(R.id.inputSize);

        Button addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //adapter.add(inputQty.getText().toString() + " x " + inputText.getText().toString());
                if (inputText.length() > 0 && inputQty.length() > 0) {
                    //Før Firebase: adapter.add(new Product(inputText.getText().toString(), Integer.parseInt(inputQty.getText().toString()), spinner.getSelectedItem().toString()));
                    Product p = new Product(inputText.getText().toString(), Integer.parseInt(inputQty.getText().toString()), spinner.getSelectedItem().toString());
                    firebase.push().setValue(p);
                    getMyAdapter().notifyDataSetChanged();
                    resetInputFields();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please fill out all values", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        //Firebase
        Query query = FirebaseDatabase.getInstance().getReference().child("/users/" + mFirebaseUser.getUid() + "/items");

        //Populate listview
        FirebaseListOptions<Product> options = new FirebaseListOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .setLayout(android.R.layout.simple_list_item_multiple_choice)
                .build();

        adapter = new FirebaseListAdapter<Product>(options) {
            @Override
            protected void populateView(View v, Product product, int position) {
                TextView textView = (TextView) v.findViewById(android.R.id.text1);
                //Output text
                textView.setText(Html.fromHtml(product.toString()));
                // Get params of view
                ViewGroup.LayoutParams params = v.getLayoutParams();
                // Set height of each listview item
                params.height = 180;
                v.setLayoutParams(params);
            }
        };

        //getting our listiew - you can check the ID in the xml to see that it
        //is indeed specified as "list"
        listView = findViewById(R.id.list);
        //here we create a new adapter linking the bag and the
        //listview
        //adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, bag);
        //setting the adapter on the listview
        listView.setAdapter(adapter);
        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }


    public void saveProductCopy(String key, Product ware) {
        //Add key and ware to hashMap as key/value pair
        savedCopies.put(key, ware);
    }

    public void reAddDeletedProducts() {
        //Loop through the hashmap
        for (Map.Entry<String, Product> entry : savedCopies.entrySet()) {
            String key = entry.getKey();
            Product value = entry.getValue();
            // Re-add to firebase with key and value
            firebase.child(key).setValue(value);
        }
        //Delete saved copies
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, RESULT_CODE_PREFERENCES);
            //notice the 1 here - this is the code we then listen for in the
            //onActivityResult
        } else if (id == R.id.action_delete) {
            //Delete saved copies if any
            savedCopies.clear();
            //Create array with mappings of checked positions: boolean true at checked positions, false if unchecked
            SparseBooleanArray positions = listView.getCheckedItemPositions();
            //Check if items are available to be deleted
            if (getMyAdapter().getCount() != 0) {
                //Loop through the list from the bottom and up to prevent getting wrong key, as the list shifts.
                for (int i = getMyAdapter().getCount() - 1; i > -1; i--) {
                    //Check if anything is at the selected position
                    if (positions.get(i)) {
                        //Get the firebase key of the item at the selected position
                        String key = getMyAdapter().getRef(i).getKey();
                        //Get the product at the specific position
                        Product ware = getMyAdapter().getItem(i);
                        //Add product
                        saveProductCopy(key, ware);
                        //Delete product
                        getMyAdapter().getRef(i).setValue(null);
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
                                getMyAdapter().notifyDataSetChanged();
                            }
                        });

                //Did anything get deleted?
                if (positions.size() > 0) {
                    snackbar.show();
                }

                //Clear selection and notify adapter of changes
                listView.clearChoices();
                getMyAdapter().notifyDataSetChanged();
                resetInputFields();
            }
        } else if (id == R.id.action_clear) {
            showDialog();
        } else if (id == R.id.action_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);

            StringBuilder sb = new StringBuilder();
            sb.append("My shopping list: ");
            for (Product bagItem : adapter.getSnapshots()) { //Create string of items
                sb.append("\n");
                sb.append(bagItem.toString());
            }
            String listToSend = sb.toString();

            intent.putExtra(Intent.EXTRA_TEXT, listToSend); //add the text to the intent
            intent.setType("text/plain"); //MIME type
            startActivity(intent);
        } else if (id == R.id.action_log_out) {
            mFirebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
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
        //Clear whole shoppinglist
        firebaseUserRoot.setValue(null);
        listView.clearChoices();
        adapter.notifyDataSetChanged();
        resetInputFields();
    }

    public static class MyDialog extends MyDialogFragment {
        @Override
        protected void negativeClick() {
            Toast toast = Toast.makeText(getActivity(),
                    "Clear cancelled", Toast.LENGTH_SHORT);
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

    public void updateUI(boolean unit) {
        //Set UI according to chosen unit (imperiaæl / metric)
        if (unit) {
            spinnerAdapter = ArrayAdapter.createFromResource(this,
                    R.array.measurement_array_metric, android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(spinnerAdapter);
            spinner.setOnItemSelectedListener(this);
        } else {
            spinnerAdapter = ArrayAdapter.createFromResource(this,
                    R.array.measurement_array_imperial, android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(spinnerAdapter);
            spinner.setOnItemSelectedListener(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CODE_PREFERENCES) //the code means we came back from settings
        {
            //I can can these methods like this, because they are static
            boolean unit = MyPreferenceFragment.chooseUnit(this);
            String message;
            if (unit) {
                message = "Metric units chosen";
            } else {
                message = "Imperial units chosen";
            }
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            updateUI(unit);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //This method is called before our activity is destroyed
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /* Here we put code now to save the state */
        //outState.putParcelableArrayList("savedBag", bag);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
