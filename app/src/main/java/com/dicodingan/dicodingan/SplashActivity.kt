package com.dicodingan.dicodingan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = FirebaseAuth.getInstance()
        handler = Handler()
        if(auth.currentUser == null){
            handler.postDelayed({
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
                finish()
            },2000)
        }else{
            handler.postDelayed({
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            },2000)
        }

    }
}