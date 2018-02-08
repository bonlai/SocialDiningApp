package com.bonlai.socialdiningapp.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.models.Interest;

import java.util.ArrayList;
import java.util.List;

public class InterestArrayAdapter extends ArrayAdapter<Interest> {
        private final Context mContext;
        private final List<Interest> mInterest;
        private final List<Interest> mInterestAll;
        private final int mLayoutResourceId;

        public InterestArrayAdapter(Context context, int resource, List<Interest> interest) {
            super(context, resource, interest);
            this.mContext = context;
            this.mLayoutResourceId = resource;
            this.mInterest = new ArrayList<>(interest);
            this.mInterestAll = new ArrayList<>(interest);
        }

        public int getCount() {
            return mInterest.size();
        }

        public Interest getItem(int position) {
            return mInterest.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

/*        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                    convertView = inflater.inflate(mLayoutResourceId, parent, false);
                }
                Interest interest = getItem(position);
                TextView name = (TextView) convertView.findViewById(R.id.textView);
                name.setText(interest.name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                public String convertResultToString(Object resultValue) {
                    return ((Department) resultValue).name;
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    List<Department> departmentsSuggestion = new ArrayList<>();
                    if (constraint != null) {
                        for (Department department : mDepartmentsAll) {
                            if (department.name.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                                departmentsSuggestion.add(department);
                            }
                        }
                        filterResults.values = departmentsSuggestion;
                        filterResults.count = departmentsSuggestion.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    mDepartments.clear();
                    if (results != null && results.count > 0) {
                        // avoids unchecked cast warning when using mDepartments.addAll((ArrayList<Department>) results.values);
                        for (Object object : (List<?>) results.values) {
                            if (object instanceof Department) {
                                mDepartments.add((Department) object);
                            }
                        }
                        notifyDataSetChanged();
                    } else if (constraint == null) {
                        // no filter, add entire original list back in
                        mDepartments.addAll(mDepartmentsAll);
                        notifyDataSetInvalidated();
                    }
                }
            };
        }*/
    }
