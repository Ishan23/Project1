package com.example.ishankhandelwal.project1;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ////////////////////////////////////////////////////
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    private ListView mListView;
    private ProgressDialog pDialog;
    private Handler updateBarHandler;
    ArrayList<String> contactList;
    ArrayList<String> complete_contact;
    ArrayList<Contact> allContacts;
    ArrayAdapter<String> adapter;
    Cursor cursor;
    int counter;

    private DatabaseHandler db;


    ////////////////////////////////////////////////////

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    public ContactsFragment() {
        // Required empty public constructor

    }

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Chats_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Chats_Fragment newInstance(String param1, String param2) {
        Chats_Fragment fragment = new Chats_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();

        contactList = new ArrayList<String>();

        db = new DatabaseHandler(getContext());

        setHasOptionsMenu(true);


    }

    public void getContacts() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Reading contacts...");
        pDialog.setCancelable(false);
        pDialog.show();


        complete_contact = new ArrayList<String>();
        allContacts = new ArrayList<Contact>();
        String contactNumber = null;
        String contactNumbers[] = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        StringBuffer output;

        ContentResolver contentResolver = getActivity().getContentResolver();
        cursor = contentResolver.query(CONTENT_URI, null,null, null, null);
        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {
            counter = 0;
            while (cursor.moveToNext()) {
                output = new StringBuffer();
                // Update the progress message
                updateBarHandler =new Handler();
                updateBarHandler.post(new Runnable() {
                    public void run() {
                        pDialog.setMessage("Reading contacts : "+ counter++ +"/"+cursor.getCount());
                    }
                });

                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

                int i= 0;

                if (hasPhoneNumber > 0) {

                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);


                    ////////////////
                    if(phoneCursor.moveToFirst())
                    {
                        contactNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));

                        contactNumber = contactNumber.replaceAll("\\s+", "");  //removes white spaces

                        if(contactNumber.length()>10)   //to remove the country code
                        {
                            if(contactNumber.length()==11)  //remove initial zero
                            {
                                contactNumber = contactNumber.substring(1);
                            }
                            else if (contactNumber.length()==13)    //remove country code
                            {
                                contactNumber = contactNumber.substring(3);
                            }
                        }


                        if(contactNumber.length()>=10)  //only for India
                        {
                            output.append(name);
                            output.append("\nPhone number: " + contactNumber);
                            contactList.add(name);
                            complete_contact.add(output.toString());
                            //Log.d(name, "contact length: " + contactNumber.length());

                            //
                            /*Contact contact = new Contact();
                            contact.setName(DISPLAY_NAME);
                            contact.setContactNumber(contactNumber);
                            allContacts.add(contact);*/

                            db.addContact(new Contact(name, contactNumber));
                        }
                    }


                    ////////////////
                    phoneCursor.close();

                }

                editor.putBoolean("contacts_synced", true);
                editor.commit(); // commit changes

                Collections.sort(contactList, String.CASE_INSENSITIVE_ORDER);
                Collections.sort(complete_contact, String.CASE_INSENSITIVE_ORDER);

                Collections.sort(allContacts, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact o1, Contact o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });




            }
            // ListView has to be updated using a ui thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    adapter = new ArrayAdapter<String>
                            (getActivity(), R.layout.contact_item, R.id.text2, contactList);

                    /*View view = inflater.inflate(R.layout.activity_contacts, container, false);
                    mListView = (ListView) view.findViewById(R.id.list);*/
                    mListView.setAdapter(adapter);



                }
            });
            // Dismiss the progressbar after 500 millisecondds
            updateBarHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pDialog.cancel();
                }
            }, 500);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    //updateBarHandler =new Handler();
                    // Since reading contacts takes more time, let's run it on a separate thread.
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            getContacts();
                        }
                    }).start();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contacts, menu);
        super.onCreateOptionsMenu(menu,inflater);

        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);

                /*if (TextUtils.isEmpty(newText)) {
                    mListView.clearTextFilter();
                }
                else {
                    mListView.setFilterText(newText.toString());
                }*/
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

        });

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(getActivity().getApplicationContext(), ContactsActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        searchView.setSubmitButtonEnabled(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contacts, container, false);
        mListView = (ListView) view.findViewById(R.id.list);

        handleIntent(getActivity().getIntent());



        if(pref.getBoolean("contacts_synced", false))
        {
            //read contacts
            readContacts();
        }
        else {
            //sync contacts

            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.READ_CONTACTS)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);


                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {

                getContacts();
            }

        }


        // Set onclicklistener to the list item.
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //TODO Do whatever you want with the list data

                Toast.makeText(getActivity(), contactList.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void handleIntent(Intent intent)
    {
        if(Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query)
    {
        Log.d("QUERY:",query);
        Toast.makeText(getActivity(), query, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try
        {
            mListener = (OnFragmentInteractionListener) context;
        }catch (RuntimeException e)
        {
            Log.e(context.toString()," must implement OnFragmentInteractionListener");
        }
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void readContacts()
    {
        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<Contact> contacts = db.getAllContacts();

        contactList.clear();

        //final List<String> contactList = new ArrayList<String>();

        for (Contact cn : contacts) {
            String log = "Name: " + cn.getName() + " ,Phone: " + cn.getContactNumber();
            // Writing Contacts to log
            Log.d("Name: ", log);


            contactList.add(cn.getName());
        }

        Collections.sort(contactList, String.CASE_INSENSITIVE_ORDER);

        // ListView has to be updated using a ui thread
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adapter = new ArrayAdapter<String>
                        (getActivity(), R.layout.contact_item, R.id.text2, contactList);

                    /*View view = inflater.inflate(R.layout.activity_contacts, container, false);
                    mListView = (ListView) view.findViewById(R.id.list);*/
                mListView.setAdapter(adapter);


            }
        });

    }

}
