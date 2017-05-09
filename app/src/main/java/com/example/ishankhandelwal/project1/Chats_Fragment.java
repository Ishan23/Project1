package com.example.ishankhandelwal.project1;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Chats_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Chats_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */



public class Chats_Fragment extends Fragment {
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
    ArrayAdapter<String> adapter;
    Cursor cursor;
    int counter;
    ////////////////////////////////////////////////////

    private DatabaseReference mDatabase;
    private String mUserId;
    private String mUserName;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private String username;


    ArrayList<Contact> allContacts;
    ArrayList<String> chatContacts;
    ArrayAdapter<String> adapter_allContacts;




    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Chats_Fragment() {
        // Required empty public constructor
    }

    /**
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







        setHasOptionsMenu(true);


    }

    private void initializeFirebase()
    {
        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserId = mFirebaseUser.getUid();
        Log.d("userID",mUserId);



        allContacts = new ArrayList<Contact>();



        mDatabase.child("user_directory").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    Contact contact = new Contact();
                    contact.setUserID(dataSnapshot.getValue(Contact.class).getUserID());
                    contact.setName(dataSnapshot.getValue(Contact.class).getName());
                    contact.setContactNumber(dataSnapshot.getValue(Contact.class).getContactNumber());
                    //Log.d("User Name: ", contact.getName());
                    allContacts.add(contact);
                    Log.d("Total contacts", "" + allContacts.size());
                    Log.d("test",dataSnapshot.getValue(Contact.class).getUserID());

                    if(dataSnapshot.getValue(Contact.class).getUserID().matches(mUserId)) //get current username
                    {
                        mUserName = dataSnapshot.getValue(Contact.class).getName();
                        Log.d("Current user", mUserName);
                        username = mUserName;
                    }

                    getContacts();
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void getContacts()
    {
        Log.d("CODE LOCATION:","Inside getContacts()");

        Collections.sort(allContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        chatContacts = new ArrayList<String>();

        int i=0;
        for(int j=0; j<allContacts.size();j++)
        {
            i++;
            Log.d("iteration",""+i);
            chatContacts.add(allContacts.get(j).getName());
        }

        //Collections.sort(chatContacts, String.CASE_INSENSITIVE_ORDER);



        // ListView has to be updated using a ui thread
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {


                adapter = new ArrayAdapter<String>
                        (getActivity(), R.layout.contact_item, R.id.text2, chatContacts);

                mListView.setAdapter(adapter);


            }
        });


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


        initializeFirebase();
        //getContacts();



        // Set onclicklistener to the list item.
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //TODO Do whatever you want with the list data

                Toast.makeText(getActivity(), chatContacts.get(position), Toast.LENGTH_SHORT).show();

                //open chat
                Intent intent = new Intent();
                intent.putExtra("senderID", mUserId);


                Log.d("user is",mUserName);
                intent.putExtra("senderName",mUserName);

                intent.putExtra("receiverID",allContacts.get(position).getUserID());
                intent.putExtra("receiverName",chatContacts.get(position));
                intent.setClass(getActivity(), Chat.class);
                startActivity(intent);
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
}
