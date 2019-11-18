package com.chaitanya.my_community.other;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chaitanya.my_community.R;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<DataModel> dataSet;
    private String user = "";
    private String activity = "";

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        TextView textVisitorName, textEventName, textProviderName,textName;
        TextView textStatus, textAmount, textAvailable, textWorkingHrs, textContact,textFlatNo;
        TextView textDate,textDesc,textRContact;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textVisitorName = (TextView) itemView.findViewById(R.id.textVisitorName);
            this.textStatus = (TextView) itemView.findViewById(R.id.textStatus);
            this.textDate = (TextView) itemView.findViewById(R.id.textDate);
        }

        public MyViewHolder(View itemView, Boolean temp) {
            super(itemView);
            this.textEventName = (TextView) itemView.findViewById(R.id.textName);
            this.textDate = (TextView) itemView.findViewById(R.id.textDate);
        }

        public MyViewHolder(View itemView, Boolean temp, Boolean temp1) {
            super(itemView);
            this.textEventName = (TextView) itemView.findViewById(R.id.textName);
            TextView textMonth = (TextView) itemView.findViewById(R.id.textVName);
            textMonth.setText("Month");

            TextView Date = (TextView) itemView.findViewById(R.id.Date);
            Date.setVisibility(View.GONE);

            TextView Amount = (TextView) itemView.findViewById(R.id.Amount);
            Amount.setVisibility(View.VISIBLE);
            this.textAmount = (TextView) itemView.findViewById(R.id.textAmount);
            textAmount.setVisibility(View.VISIBLE);
        }

        public MyViewHolder(View itemView, boolean b, boolean b1, boolean b2) {
            super(itemView);
            this.textProviderName = (TextView) itemView.findViewById(R.id.textProviderName);
            this.textAvailable = (TextView) itemView.findViewById(R.id.textAvailable);
            this.textContact = (TextView) itemView.findViewById(R.id.textContact);
            this.textWorkingHrs = (TextView) itemView.findViewById(R.id.textWorkingHRs);
        }

        public MyViewHolder(View itemView, ViewGroup parent, boolean b, boolean b1, boolean b2, boolean b3, String s) {
            super(itemView);
            this.textEventName = (TextView) itemView.findViewById(R.id.textName);

            TextView Desc = (TextView) itemView.findViewById(R.id.Date);
            Desc.setText("Desc");

            this.textDesc = (TextView) itemView.findViewById(R.id.textDate);
        }

        public MyViewHolder(View itemView, boolean b, boolean b1, boolean b2, String s, boolean b3) {
            super(itemView);
            this.textName = (TextView) itemView.findViewById(R.id.textName);

            this.textFlatNo = (TextView) itemView.findViewById(R.id.textflatno);

            this.textRContact = (TextView) itemView.findViewById(R.id.textContact);
        }
    }

    public CustomAdapter(ArrayList<DataModel> data) {
        this.dataSet = data;
    }

    public CustomAdapter(ArrayList<DataModel> data, String user) {
        this.dataSet = data;
        this.user = user;
    }

    public CustomAdapter(ArrayList<DataModel> data, String user, String activity) {
        this.dataSet = data;
        this.user = user;
        this.activity=activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        MyViewHolder myViewHolder;

        if (user.equals("owner") || user.equals("assoc")) {

            View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.commoncardview, parent, false);

            if (activity.equals("Dues"))
                myViewHolder = new MyViewHolder(view, false, false);
            else if(activity.equals("Services"))
                myViewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.servicescardview, parent, false), false, false, false);
            else if(activity.equals("Announce"))
                myViewHolder = new MyViewHolder(view, parent, false, false, false, false,"");
            else if (activity.equals("residentInfo"))
                myViewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.residentinfo, parent, false), false, false, false,"",true);
            else
                myViewHolder = new MyViewHolder(view, false);

        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardviewlayout, parent, false);
            myViewHolder = new MyViewHolder(view);
        }


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        if (user.equals("owner") || user.equals("assoc")) {

            if (activity.equals("Dues"))
            {
                TextView textEventName = holder.textEventName;
                TextView textAmt = holder.textAmount;

                textEventName.setText(dataSet.get(listPosition).getMonth());
                textAmt.setText(dataSet.get(listPosition).getAmount());
            }
            else if (activity.equals("Announce"))
            {
                TextView textEventName = holder.textEventName;
                TextView textDesc = holder.textDesc;

                textEventName.setText(dataSet.get(listPosition).getName());
                textDesc.setText(dataSet.get(listPosition).getDate());
            }
            else if (activity.equals("residentInfo"))
            {
                TextView textName = holder.textName;
                TextView textContact = holder.textRContact;
                TextView flatNo = holder.textFlatNo;

                textName.setText(dataSet.get(listPosition).getName());
                flatNo.setText(dataSet.get(listPosition).getStatus());
                textContact.setText(dataSet.get(listPosition).getDate());
            }
            else if(activity.equals("Services"))
            {
                TextView textProviderName = holder.textProviderName;
                TextView textAvailable = holder.textAvailable;
                TextView textContact = holder.textContact;
                TextView textWorkingHrs = holder.textWorkingHrs;

                textProviderName.setText(dataSet.get(listPosition).getProviderName());
                textAvailable.setText(dataSet.get(listPosition).getAvailable());
                textContact.setText(dataSet.get(listPosition).getContact());
                textWorkingHrs.setText(dataSet.get(listPosition).getWorkhrs());
            }
            else
            {
                TextView textEventName = holder.textEventName;
                TextView textDate = holder.textDate;

                textEventName.setText(dataSet.get(listPosition).getName());
                textDate.setText(dataSet.get(listPosition).getDate());
            }
        } else {
            TextView textVisitorName = holder.textVisitorName;
            TextView textStatus = holder.textStatus;
            TextView textDate = holder.textDate;

            textVisitorName.setText(dataSet.get(listPosition).getName());
            textStatus.setText(dataSet.get(listPosition).getStatus());
            textDate.setText(dataSet.get(listPosition).getDate());
        }


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
