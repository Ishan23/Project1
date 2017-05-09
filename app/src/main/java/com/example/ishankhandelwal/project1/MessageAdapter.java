package com.example.ishankhandelwal.project1;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by Ishan Khandelwal on 12/28/2016.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;


    private String mUserId;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUserId = mFirebaseUser.getUid();
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public void onBindViewHolder(MessageViewHolder messageViewHolder, int i) {
        Message message = messageList.get(i);
        messageViewHolder.vMessage.setText(message.getMessage());
        //messageViewHolder.vSender.setText(message.getSenderName());

        /*if(mUserId.matches(message.getSenderId()))
        {
            Log.d("message id: " + i, "sender");
            View itemView = LayoutInflater.
                    from(messageViewHolder.viewGroup.getContext()).
                    inflate(R.layout.message_card_sender, messageViewHolder.viewGroup, false);
            messageViewHolder.vMessage =  (TextView) itemView.findViewById(R.id.message);
        }
        else
        {
            Log.d("message id: " + i, "receiver");
            View itemView = LayoutInflater.
                    from(messageViewHolder.viewGroup.getContext()).
                    inflate(R.layout.message_card_receiver, messageViewHolder.viewGroup, false);
            messageViewHolder.vMessage =  (TextView) itemView.findViewById(R.id.message);
        }

        messageViewHolder.vMessage.setText(message.getMessage());
*/
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        switch (i)
        {
            case 0:
            {
                View itemView = LayoutInflater.
                        from(viewGroup.getContext()).
                        inflate(R.layout.message_card_sender, viewGroup, false);
                return new MessageViewHolder(itemView, viewGroup);

            }
            case 1:
            {
                View itemView = LayoutInflater.
                        from(viewGroup.getContext()).
                        inflate(R.layout.message_card_receiver, viewGroup, false);
                return new MessageViewHolder(itemView, viewGroup);

            }
            default:
            {
                View itemView = LayoutInflater.
                        from(viewGroup.getContext()).
                        inflate(R.layout.message_card, viewGroup, false);
                return new MessageViewHolder(itemView, viewGroup);

            }
        }



        /*View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.message_card, viewGroup, false);
        return new MessageViewHolder(itemView, viewGroup);*/


        /*View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.message_card, viewGroup, false);*/

        /*Message message = messageList.get(i);
        if(message.getSenderId().matches(mUserId))
        {
            Log.d("message id: " + i, "sender");
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.message_card_sender, viewGroup, false);
            return new MessageViewHolder(itemView, viewGroup);
        }
        else
        {
            Log.d("message id: " + i, "receiver");
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.message_card_receiver, viewGroup, false);
            return new MessageViewHolder(itemView, viewGroup);
        }*/




    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous

        Message message = messageList.get(position);

        if (mUserId.matches(message.getSenderId()))
        {
            return 0;
        }
        else
        {
            return 1;
        }


    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        protected TextView vMessage;
        protected TextView vSender;
        protected ViewGroup viewGroup;


        public MessageViewHolder(View v, ViewGroup viewGroup) {
            super(v);
            vMessage =  (TextView) v.findViewById(R.id.message);
            //vSender = (TextView)  v.findViewById(R.id.sender);
            this.viewGroup = viewGroup;

        }
    }




}



