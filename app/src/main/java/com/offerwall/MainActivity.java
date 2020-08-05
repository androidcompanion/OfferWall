package com.offerwall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.offerwallcompanion.OffersFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OffersFragment offersFragment = new OffersFragment(MainActivity.this,TestNextAcitivity.class,"http://fbbooster.get-fans-for-musically.com/api/get_appadsbyid.php");

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.offer_container, offersFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}