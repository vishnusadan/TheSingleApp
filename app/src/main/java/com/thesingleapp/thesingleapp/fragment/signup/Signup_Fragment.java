package com.thesingleapp.thesingleapp.fragment.signup;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.userdata.UserData;

public class Signup_Fragment extends Fragment implements View.OnClickListener{

    private ImageButton submit;
    private ImageView backbtn;
    private EditText username,phoneno,email,password,confirmpassword;
    private ConstraintLayout mainLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View signup =inflater.inflate(R.layout.fragment_signup,container,false);

        username = signup.findViewById(R.id.username);
        email = signup.findViewById(R.id.email);
        phoneno = signup.findViewById(R.id.phonenumber);
        password = signup.findViewById(R.id.password);
        confirmpassword = signup.findViewById(R.id.confirmpassword);
        submit = signup.findViewById(R.id.submit);
        backbtn = signup.findViewById(R.id.backbutton);
        mainLayout = signup.findViewById(R.id.mainlayout);
        submit.setOnClickListener(this);
        backbtn.setOnClickListener(this);


        return signup;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){


            case R.id.backbutton:

                getActivity().finish();

                break;


            case R.id.submit:

                if (username.getText().toString().equals("")){

                    username.setError("Please Fill Username");

                }else if (email.getText().toString().equals("") || !email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")){

                    email.setError("Please Fill Email Correctly");

                }else if (phoneno.getText().toString().equals("") || phoneno.getText().length()<10 || phoneno.getText().length()>14){

                    phoneno.setError("Please Fill Phone number Correctly");

                }else if (password.getText().toString().equals("") ){

                    password.setError("Please Fill Password ");

                }
                else if(password.getText().length()<8)
                {
                    password.setError("Minimum 8 Characters");
                }
                else if (confirmpassword.getText().toString().equals("")){

                    confirmpassword.setError("Please Fill Confirm Password");

                }
                else if(confirmpassword.getText().length()<8)
                {
                    confirmpassword.setError("Minimum 8 Characters");
                }
                else if(password.getText().toString().equals(confirmpassword.getText().toString())) {

                    UserData.SIGNUPUSERNAME=username.getText().toString();
                    UserData.SIGNUPEMAIL=email.getText().toString();
                    UserData.SIGNUPPHONENO=phoneno.getText().toString();
                    UserData.SIGNUPPASSWORD=password.getText().toString();

                    Fragment mFragment = new Signup_One_Fragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.linear, mFragment).commit();

                }else {

                    password.setError("Password Mismatch");
                    confirmpassword.setError("Password Mismatch");

                }
                break;
        }
    }


    }

