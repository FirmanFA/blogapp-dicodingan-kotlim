package com.dicodingan.dicodingan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.Login
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        database = Firebase.database.reference

        if(auth.currentUser != null){
            val user = auth.currentUser
            createUserIfNotExist(user!!.uid)
//            val update = UserProfileChangeRequest.Builder().setDisplayName("ea")
        }
        callbackManager = CallbackManager.Factory.create()
        val loginFbBtn = findViewById<LoginButton>(R.id.loginFbBtn)
        loginFbBtn.setPermissions("email","public_profile")

        loginFbBtn.registerCallback(callbackManager, object:
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                handleFacebookAccessToken(result!!.accessToken)
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(baseContext, "Login failed. "+ error?.localizedMessage.toString(),
                    Toast.LENGTH_SHORT).show()
            }

        })  


        googleSignInBtn.setOnClickListener {
            googleSignIn()
        }

        loginFbBtn.setLogoutText("Continue with Facebook")

        button2.setOnClickListener {
            logout()
        }

    }

    private fun logout(){
        auth.signOut()
        LoginManager.getInstance().logOut()
        googleSignInClient.signOut()
    }

    private fun googleSignIn(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    fun register(view: View){
        val intent = Intent(this,RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun createUserIfNotExist(userId:String){

        database.child("users").addListenerForSingleValueEvent(
            object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
//                Toast.makeText(this@LoginActivity, snapshot.childrenCount.toString(), Toast.LENGTH_SHORT).show()
                if(snapshot.childrenCount>0){
                    var exist = false
                    for (data in snapshot.children) {
                        if (data.child("uid").value == userId) {
                            exist = true
                        }
                    }
                    
                    if (exist){
                        val intent = Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        val intent = Intent(this@LoginActivity,UserDetailActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    
                }else{
                    val intent = Intent(this@LoginActivity,UserDetailActivity::class.java)
                    startActivity(intent)
                    finish()

                }
            }

        })
    }

    fun loginEmailPass(view: View){
        auth.signInWithEmailAndPassword(loginEmailTxt.text.toString().trim(),loginPassTxt.text
            .toString().trim()).addOnCompleteListener(this){it->
            if (it.isSuccessful){
                val user = auth.currentUser
//                Toast.makeText(this, user!!.uid, Toast.LENGTH_SHORT).show()
                createUserIfNotExist(user!!.uid)
//                Toast.makeText(this, "login email pass sukses", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(baseContext, "Login failed. "+it.exception.toString(),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build())

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
        // [END auth_fui_create_intent]
    }

    private fun handleFacebookAccessToken(token:AccessToken){
        Log.i("respon","handle akeses token $token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener(this){it
            if (it.isSuccessful){
                Log.i("respon","Masok")
                val user = auth.currentUser
//                Toast.makeText(this, user!!.uid, Toast.LENGTH_SHORT).show()
                createUserIfNotExist(user!!.uid)
//                Toast.makeText(this, "login fb sukses", Toast.LENGTH_SHORT).show()
            }else{
                Log.i("respon","gagal",it.exception)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode,resultCode,data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val acc =  task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(acc.idToken!!)
            }catch (e:ApiException){
                Toast.makeText(baseContext, "Login failed. $e",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //login sukses masuk main
                    val user = auth.currentUser
//                    Toast.makeText(this, user!!.uid, Toast.LENGTH_SHORT).show()
                    createUserIfNotExist(user!!.uid)
                } else {
                    Toast.makeText(baseContext, "Login failed. "+ task.exception
                        ?.localizedMessage.toString(),
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun delete() {
        // [START auth_fui_delete]
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                // ...
            }
        // [END auth_fui_delete]
    }

    companion object {

        private const val RC_SIGN_IN = 123
        private const val EMAIL = "email"
    }
}