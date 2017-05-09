package com.example.ishankhandelwal.project1;

/**
 * Created by Ishan Khandelwal on 12/27/2016.
 */
public class Contact {

    private String name;
    private String number;
    private String userID;



    public Contact()
    {

    }

    public Contact(String display_name, String contactNumber) {
        name = display_name;
        number = contactNumber;
    }

    public Contact(String mUserID, String mName, String mNumber)
    {
        userID = mUserID;
        name = mName;
        number = mNumber;
    }



    public String getName()
    {
        return name;
    }

    public void setName(String mName)
    {
        name = mName;
    }

    public String getContactNumber()
    {
        return number;
    }

    public void setContactNumber(String mNumber)
    {
        number = mNumber;
    }

    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String mUserID)
    {
        userID = mUserID;
    }
}
