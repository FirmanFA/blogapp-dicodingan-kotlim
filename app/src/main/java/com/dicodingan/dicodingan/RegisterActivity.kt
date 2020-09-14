package com.dicodingan.dicodingan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.login.Login
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

    }

    fun register(view:View){
        auth.createUserWithEmailAndPassword(RegisEmailTxt.text.toString().trim(),RegisPassTxt.text
            .toString().trim())
            .addOnCompleteListener(this){it ->
                if (it.isSuccessful){
                    auth.currentUser?.sendEmailVerification()?.addOnCompleteListener(this){its ->
                        if (its.isSuccessful){
                            LoginActivity().finish()
                            val intent = Intent(this,UserDetailActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }else{
                    Toast.makeText(baseContext, "register failed. "+it.exception
                        ?.localizedMessage.toString(),
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}