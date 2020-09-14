package com.dicodingan.dicodingan

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicodingan.dicodingan.model.Post
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user_detail.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val navigationItemSelectListener =
        BottomNavigationView.OnNavigationItemSelectedListener {
        when(it.itemId){
            R.id.navigation_satu -> {
                val fragment =  MainFragment.newInstance("oke","oke")
                addFragment(fragment)
                true
            }
            R.id.navigation_dua -> {
                val fragment =  BlankFragment.newInstance("oke","oke")
                addFragment(fragment)
                true
            }
            else -> false
        }
    }

    private val navigationItemReselectedListener =
        BottomNavigationView.OnNavigationItemReselectedListener {
            when(it.itemId){
                R.id.navigation_satu -> {
                    //refresh
                }
                R.id.navigation_dua -> {
                    //refresh
                }
            }
        }

    private fun addFragment(fragment: Fragment){

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrameLayout,fragment,fragment.javaClass.simpleName)
            .commit()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainNavigation.setOnNavigationItemSelectedListener(navigationItemSelectListener)
        mainNavigation.setOnNavigationItemReselectedListener(navigationItemReselectedListener)
        val fragment = MainFragment.newInstance("pe","pe")
        addFragment(fragment)

    }




}