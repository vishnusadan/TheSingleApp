package com.thesingleapp.thesingleapp.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import com.google.android.gms.common.ConnectionResult;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.activity.HomeScreen;
import com.thesingleapp.thesingleapp.activity.SearchScreen;
import java.util.ArrayList;
import java.util.List;
import static android.content.Context.MODE_PRIVATE;
import static com.thesingleapp.thesingleapp.fragment.SideNavigation.Matches_Fragment.screen;

public class FilterDialogFragment extends DialogFragment {

    //Login save in share preference
    private SharedPreferences.Editor filterPrefsEditor;
    private String gender,onlineitem,premiumitem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_dialog, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText fromage = view.findViewById(R.id.fromage);
        final EditText toage = view.findViewById(R.id.toage);
        final CheckBox ch_male = view.findViewById(R.id.ch_male);
        final CheckBox ch_female = view.findViewById(R.id.ch_female);

        final Spinner onlinespinner = view.findViewById(R.id.ch_seek_male);
        final Spinner premiumspinner = view.findViewById(R.id.premium_ed);

        // Shared prefference intiated
        SharedPreferences filterPreferences = getActivity().getSharedPreferences("filterdata", MODE_PRIVATE);
        filterPrefsEditor = filterPreferences.edit();

        String seekingfor = filterPreferences.getString("seekingfor", "");
        String online = filterPreferences.getString("online", "");
        String fromagev = filterPreferences.getString("fromage", "");
        String toagev = filterPreferences.getString("toage", "");
        String premium = filterPreferences.getString("premium", "");



        fromage.setText(fromagev);
        toage.setText(toagev);

        if (seekingfor.equals("M")){

            ch_male.setChecked(true);

        }else if(seekingfor.equals("F")) {

            ch_female.setChecked(true);

        }else {

            ch_male.setChecked(false);
            ch_female.setChecked(false);
        }

        if (online.equals("Yes")){

            onlinespinner.setSelection(1);

        }else {

            onlinespinner.setSelection(2);

        }

        if (premium.equals("Yes")){

            premiumspinner.setSelection(1);

        }else {

            premiumspinner.setSelection(2);

        }


        // Spinner click listener
        onlinespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                if (parent.getItemAtPosition(position).toString().equals("Yes")){

                    onlineitem = "T";

                }else if(parent.getItemAtPosition(position).toString().equals("No")) {

                    onlineitem = "F";

                }else {

                    onlineitem = "B";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        premiumspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                if (parent.getItemAtPosition(position).toString().equals("Yes")){

                    premiumitem = "T";

                }else if(parent.getItemAtPosition(position).toString().equals("No")) {

                    premiumitem = "F";
                }else {

                    premiumitem = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ch_male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){

                    gender = "M";

                    ch_female.setChecked(false);
                }
            }
        });

        ch_female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(compoundButton.isChecked()){

                    gender = "F";

                    ch_male.setChecked(false);
                }

            }
        });

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Both");
        categories.add("Yes");
        categories.add("No");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        onlinespinner.setAdapter(dataAdapter);
        premiumspinner.setAdapter(dataAdapter);

        ImageButton btnDone = view.findViewById(R.id.submit);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fromage.getText().equals(""))
                {

                    fromage.setText(0);


                }
                if(toage.getText().equals(""))
                {
                    toage.setText(0);
                }

                filterPrefsEditor.putString("seekingfor", gender);
                filterPrefsEditor.putString("online", onlineitem);
                filterPrefsEditor.putString("fromage", fromage.getText().toString());
                filterPrefsEditor.putString("toage", toage.getText().toString());
                filterPrefsEditor.putString("premium", premiumitem);
                filterPrefsEditor.putBoolean("saveFilter", true);
                filterPrefsEditor.commit();


                if(screen.equals("Home")) {

                    getDialog().dismiss();
                    startActivity(new Intent(getActivity(), HomeScreen.class));

                }else {

                    getDialog().dismiss();
                    startActivity(new Intent(getActivity(), SearchScreen.class));
                    getActivity().finish();

                }


            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean setFullScreen = false;
        if (getArguments() != null) {
            setFullScreen = getArguments().getBoolean("fullScreen");
        }

        if (setFullScreen)
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public interface DialogListener {
        void onConnectionFailed(@NonNull ConnectionResult connectionResult);

        void onFinishEditDialog(String inputText);
    }


}

