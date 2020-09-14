package com.dicodingan.dicodingan

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicodingan.dicodingan.model.User
import com.dicodingan.dicodingan.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_user_detail.*
import java.text.SimpleDateFormat
import java.util.*

class UserDetailActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        logo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
        name.setText(auth.currentUser!!.displayName)
        birthDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _,
                                                                        year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR,year)
                calendar.set(Calendar.MONTH,month)
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                val format = "yyyy-MM-dd"
                val sdf = SimpleDateFormat(format, Locale.US)

                val tglRaw = sdf.format(calendar.time)

                val tgl = tglRaw.replace("/","-")

                birthDate.setText(tgl)
            },
                calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()

        }

    }
    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data!=null){
            Toast.makeText( this,"Sukses", Toast.LENGTH_SHORT).show()
            selectedPhotoUri = data.data
            try {
                selectedPhotoUri?.let {
                    if(Build.VERSION.SDK_INT < 28){
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,
                        selectedPhotoUri)
                        logo.setImageBitmap(bitmap)
                    }else{
                        val source = ImageDecoder.createSource(this.contentResolver,
                        selectedPhotoUri!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        logo.setImageBitmap(bitmap)
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun createDetailUser(v:View){
        database.child("users").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount>0){
                        var exist = false
                        for(data in snapshot.children){
                            if (data.child("username").value.toString() == username.text.toString()){
                                exist = true
                            }
                        }

                        if(exist){
                            Toast.makeText(
                                this@UserDetailActivity,
                                "username tidak tersedia",
                                Toast.LENGTH_SHORT
                            ).show()
                        }else{
                            insertAndUploadToFirebase(
                                auth.currentUser?.email!!
                                ,auth.currentUser!!.uid
                                ,username.text.toString()
                                ,birthDate.text.toString()
                                ,name.text.toString())
                        }

                    }
                }
            })
    }

    private fun insertUserDetail(email:String,uid:String,userName: String
                                 ,birthDate:String,displayName:String,profileImageUrl:String ){
        val userProfile = UserProfile(
            birthDate,
            displayName,
            profileImageUrl
        )
        val user = User(
            email,
            uid,
            userName,
            userProfile
        )
        database.child("users").child(uid).setValue(user).addOnCompleteListener {
            if (it.isSuccessful){
                //ke main
                Toast.makeText(this, "insert user sukses", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }else{
                //error
                Toast.makeText(this, "gagal"+it.exception?.localizedMessage,
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun insertAndUploadToFirebase(email:String,uid:String,userName: String
                            ,birthDate:String,displayName:String){

        if (selectedPhotoUri != null){
            val filename = UUID.randomUUID().toString()
            val imageRef = FirebaseStorage.getInstance().getReference("/images/$filename")
            imageRef.putFile(selectedPhotoUri!!).addOnSuccessListener {
                imageRef.downloadUrl.addOnCompleteListener {
                    //it is the url to put in database
                    insertUserDetail(email,uid, userName, birthDate, displayName,
                        it.result.toString())
                }

            }
        }else{
            insertUserDetail(email,uid, userName, birthDate, displayName, "")
        }

    }
}