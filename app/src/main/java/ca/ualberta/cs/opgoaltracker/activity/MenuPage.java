/*
 * Copyright 2017 Yongjia Huang, Dichong Song, Mingwei Li, Donglin Han, Long Ma,CMPUT301F17T25 CMPUT301, University of Alberta, All Rights Reserved.
 * You may use distribut, or modify this code under terms and conditions of the ode of Student Behavior at University of Alberta
 * You may find a copy of the license in this project. Otherwise please contact jajayongjia@gmail.com
 */

package ca.ualberta.cs.opgoaltracker.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ca.ualberta.cs.opgoaltracker.R;
import ca.ualberta.cs.opgoaltracker.models.Participant;

public class MenuPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,MyAccountFragment.OnFragmentInteractionListener,NewsFragment.OnFragmentInteractionListener,FriendFragment.OnFragmentInteractionListener,HabitFragment.OnFragmentInteractionListener,HabitEventFragment.OnFragmentInteractionListener{


    Participant currentUser;
    HabitFragment habitFragment = new HabitFragment();
    HabitEventFragment habitEventFragment = new HabitEventFragment();
    FriendFragment friendFragment = new   FriendFragment();
    NewsFragment newsFragment = new   NewsFragment();
    MyAccountFragment myAccountFragment = new MyAccountFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);

        currentUser = getIntent().getParcelableExtra("LOGINUSER");

        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putParcelable("CURRENTUSER",currentUser);
            habitFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.relativeLayout_1, habitFragment).commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Bundle args = new Bundle();
        args.putParcelable("CURRENTUSER",currentUser);

        if (id == R.id.habit) {
            habitFragment.setArguments(args);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativeLayout_1,habitFragment,habitFragment.getTag()).commit();
//            Handle the camera action
        } else if (id == R.id.habitEvent) {
            habitEventFragment.setArguments(args);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativeLayout_1,habitEventFragment,habitEventFragment.getTag()).commit();

        } else if (id == R.id.friends) {
            friendFragment.setArguments(args);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativeLayout_1,friendFragment,friendFragment.getTag()).commit();

        } else if (id == R.id.social) {
            newsFragment.setArguments(args);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativeLayout_1,newsFragment ,newsFragment .getTag()).commit();

        } else if (id == R.id.setting) {
            myAccountFragment.setArguments(args);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativeLayout_1,myAccountFragment ,myAccountFragment .getTag()).commit();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
