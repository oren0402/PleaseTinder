package com.example.pleasetinder;



import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.pleasetinder.Register.Description;
import com.example.pleasetinder.Register.EmailAndPass;


public class RegisterActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EmailAndPass firstFragment = new EmailAndPass();

        // In case this activity was started with special instructions from an Intent,
        // pass the Intent's extras to the fragment as arguments
        firstFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.nav_host_fragment_Register, firstFragment).commit();
    }

    public void switchFragment() {
        Description secondFragment = new Description();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.nav_host_fragment_Register, secondFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void newActivity() {
        Intent myIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }
}