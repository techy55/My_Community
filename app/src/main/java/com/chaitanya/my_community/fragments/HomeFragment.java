package com.chaitanya.my_community.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.chaitanya.my_community.assoc.AccountsActivity;
import com.chaitanya.my_community.assoc.AnnouncementActivity;
import com.chaitanya.my_community.assoc.AssocComplaintActivity;
import com.chaitanya.my_community.assoc.DuesActivity;
import com.chaitanya.my_community.assoc.ResidentInfoActivity;
import com.chaitanya.my_community.owner.ComplaintsActivity;
import com.chaitanya.my_community.R;
import com.chaitanya.my_community.common.EventsActivity;
import com.chaitanya.my_community.common.ServicesActivity;
import com.chaitanya.my_community.owner.PayMainActivity;
import com.chaitanya.my_community.owner.ViewVisitorsActivity;
import com.chaitanya.my_community.security.SecurityActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FirebaseUser user;
    private String user_ID;
    private boolean userChecked = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private DatabaseReference databaseReference;

    private FirebaseAuth auth;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        auth = FirebaseAuth.getInstance();

        if (auth != null)
            user = auth.getCurrentUser();
        if (user != null)
            user_ID = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("Main").child("Users");

        sharedPreferences = getContext().getSharedPreferences("userCheck", 0);

        if (sharedPreferences.getBoolean("com/chaitanya/my_community/security", false))
            isSecurity(v);
        else if (sharedPreferences.getBoolean("owner", false))
            isOwner(v);
        else if (sharedPreferences.getBoolean("assoc", false))
            isAssoc(v);
        else if (!userChecked || sharedPreferences.getBoolean("userChecked", false))
            checkUser(v);
        // Inflate the layout for this fragment
        return v;
    }

    private void checkUser(final View v) {

        class Check {

            private void checkOwner() {
                if (user != null) {
                    databaseReference.child("Owner")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_ID)) {
                                        // if (dataSnapshot.getValue().equals(user_ID))
                                        isOwner(v);
                                        userChecked = true;
                                        editor = sharedPreferences.edit();
                                        editor.putBoolean("userChecked", userChecked);
                                        editor.putBoolean("owner", true);
                                        editor.apply();
                                        FirebaseMessaging fcm= FirebaseMessaging.getInstance();
                                        fcm.subscribeToTopic("Announcements");
                                        fcm.subscribeToTopic("Events");
                                        fcm.subscribeToTopic("Secuirty");
                                        fcm.subscribeToTopic("Dues").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(getContext(),"Success",Toast.LENGTH_LONG).show();
                                                }else
                                                    Toast.makeText(getContext(),"Failed",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    } else {
                                        checkAssoc();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }

            private void checkAssoc() {
                if (user != null) {
                    databaseReference.child("Association")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_ID)) {
                                        //if(dataSnapshot.getValue().toString().equals(user_ID))
                                        isAssoc(v);
                                        userChecked = true;
                                        editor = sharedPreferences.edit();
                                        editor.putBoolean("userChecked", userChecked);
                                        editor.putBoolean("assoc", true);
                                        editor.apply();
                                    } else {
                                        Toast.makeText(getContext(), "User Doesn't Exist", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }

            private void checkSecurity() {
                if (user != null) {
                    databaseReference.child("Security")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_ID)) {   // if(dataSnapshot.getKey().equals(user_ID))
                                        isSecurity(v);
                                        userChecked = true;
                                        editor = sharedPreferences.edit();
                                        editor.putBoolean("userChecked", userChecked);
                                        editor.putBoolean("com/chaitanya/my_community/security", true);
                                        editor.apply();
                                    } else
                                        checkOwner();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }
        }
        new Check().checkSecurity();
    }

    private void isOwner(View v) {

        Button view_histry, pay_main, dues, services, complaints, events;

        view_histry = (Button) v.findViewById(R.id.view_history);
        pay_main = (Button) v.findViewById(R.id.pay_main);
        dues = (Button) v.findViewById(R.id.dues);
        services = (Button) v.findViewById(R.id.services);
        complaints = (Button) v.findViewById(R.id.complaints);
        events = (Button) v.findViewById(R.id.events);
        GridLayout gridLayout = (GridLayout) v.findViewById(R.id.owner);

        gridLayout.setVisibility(View.VISIBLE);

        view_histry.setOnClickListener(onClickListener);
        pay_main.setOnClickListener(onClickListener);
        dues.setOnClickListener(onClickListener);
        services.setOnClickListener(onClickListener);
        complaints.setOnClickListener(onClickListener);
        events.setOnClickListener(onClickListener);

        databaseReference.child("Owner").child(user_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    editor = sharedPreferences.edit();
                    if (map.get("Name") != null) {
                        editor.putString("Name", map.get("Name").toString().trim());
                    }
                    if (map.get("Phone") != null) {
                        editor.putString("Phone", map.get("Phone").toString().trim());
                    }
                    if (map.get("Flat No") != null) {
                        editor.putString("Flat No", map.get("Flat No").toString().trim());
                    }
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isAssoc(View v) {

        Button accounts, resi_info, dues, services, complaints, events, announcements;

        accounts = (Button) v.findViewById(R.id.accounts);
        resi_info = (Button) v.findViewById(R.id.residents_info);
        dues = (Button) v.findViewById(R.id.assoc_dues);
        services = (Button) v.findViewById(R.id.assoc_services);
        complaints = (Button) v.findViewById(R.id.assoc_complaints);
        events = (Button) v.findViewById(R.id.assoc_events);
        announcements = (Button) v.findViewById(R.id.assoc_announcements);
        GridLayout gridLayout = (GridLayout) v.findViewById(R.id.association);

        gridLayout.setVisibility(View.VISIBLE);

        accounts.setOnClickListener(onClickListener);
        resi_info.setOnClickListener(onClickListener);
        dues.setOnClickListener(onClickListener);
        services.setOnClickListener(onClickListener);
        complaints.setOnClickListener(onClickListener);
        events.setOnClickListener(onClickListener);
        announcements.setOnClickListener(onClickListener);

        databaseReference.child("Association").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    editor = sharedPreferences.edit();
                    if (map.get("Name") != null) {
                        editor.putString("Name", map.get("Name").toString().trim());
                    }
                    if (map.get("Phone") != null) {
                        editor.putString("Phone", map.get("Phone").toString().trim());
                    }
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isSecurity(View v) {

     /*   Button go = (Button) v.findViewById(R.id.go_btn);
        RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.security);

        relativeLayout.setVisibility(View.VISIBLE);
        go.setOnClickListener(onClickListener);*/

        startActivity(new Intent(getContext(), SecurityActivity.class));
        getActivity().finish();
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.complaints:
                    startActivity(new Intent(getContext(), ComplaintsActivity.class));
                    break;
                case R.id.assoc_complaints:
                    startActivity(new Intent(getContext(), AssocComplaintActivity.class));
                    break;
                case R.id.view_history:
                    startActivity(new Intent(getContext(), ViewVisitorsActivity.class));
                    break;
                case R.id.pay_main:
                    startActivity(new Intent(getContext(), PayMainActivity.class));
                    break;
                case R.id.dues:
                    startActivity(new Intent(getContext(), com.chaitanya.my_community.owner.DuesActivity.class));
                    break;
                case R.id.services:
                case R.id.assoc_services:
                    startActivity(new Intent(getContext(), ServicesActivity.class));
                    break;
                case R.id.residents_info:
                    startActivity(new Intent(getContext(), ResidentInfoActivity.class));
                    break;
                case R.id.accounts:
                    startActivity(new Intent(getContext(), AccountsActivity.class));
                    break;
                case R.id.assoc_dues:
                    startActivity(new Intent(getContext(), DuesActivity.class));
                    break;
                case R.id.assoc_events:
                case R.id.events:
                    startActivity(new Intent(getContext(), EventsActivity.class));
                    break;
                case R.id.assoc_announcements:
                    startActivity(new Intent(getContext(), AnnouncementActivity.class));
                    break;
            }
        }
    };

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
