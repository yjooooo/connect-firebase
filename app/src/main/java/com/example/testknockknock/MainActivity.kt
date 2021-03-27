package com.example.testknockknock

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.testknockknock.databinding.ActivityMainBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    //데이터베이스의 인스턴스를 가져온다고 생각(즉, Root를 가져온다고 이해하면 쉬움)
    private val databaseReference: DatabaseReference = database.reference
    //private val myRef: DatabaseReference = database.getReference("parentId")
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setOnBtnPushClick()
        setOnBtnGetClick()
        setOnBtnImgUploadClick()
        setOnBtnAudioUploadClick()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when(requestCode){
                1 -> {
                    val selectedImage: Uri? = data.data
                    uploadImgUri(selectedImage!!)
                }
                2 -> {
                    val selectedAudio: Uri? = data.data
                    uploadAudioUri(selectedAudio!!)
                }
            }
        }
    }

    private fun setOnBtnAudioUploadClick(){
        binding.btnAudioUpload.setOnClickListener {
            val audioIntent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(audioIntent, 2)
        }
    }

    private fun setOnBtnImgUploadClick(){
        binding.btnImgUpload.setOnClickListener {
            val imageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(imageIntent, 1)
        }
    }

    private fun uploadImgUri(file: Uri){
        //val file = Uri.fromFile(file)
        firebaseStorage.reference.child("imageFile").child("imageUri.png")
            .putFile(file).addOnCompleteListener {
                if(it.isSuccessful){
                    Log.d("storage", "upload success")
                }
            }
    }

    private fun uploadAudioUri(file: Uri){
        //val file = Uri.fromFile(file)
        firebaseStorage.reference.child("audioFile").child("audioUri.mp4")
            .putFile(file).addOnCompleteListener {
                if(it.isSuccessful){
                    Log.d("storage", "upload success")
                }
            }
    }

    private fun setOnBtnPushClick(){
        lateinit var parentId: String
        lateinit var childName: String
        binding.btnPush.setOnClickListener {
            parentId = binding.edtParentId.text.toString()
            childName = binding.edtChildName.text.toString()
            databaseReference.child(parentId).child(parentId + "의 child " + childName)
                .setValue("아이 이름은 " + childName + "입니다.")
            Toast.makeText(this, "push", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setOnBtnGetClick(){
        lateinit var getId: String
        binding.btnGet.setOnClickListener {
            getId = binding.edtGetId.text.toString()
            //val myRef: DatabaseReference = database.getReference(parentId)
            val myValue: DatabaseReference = databaseReference.child(getId)
                .child(getId + "의 child ${binding.edtGetChildNum.text}")
            myValue.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val text = snapshot.getValue(String::class.java)
                    text?.let { Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show() }
                        ?: Toast.makeText(this@MainActivity, "존재하지 않는 아이입니다.", Toast.LENGTH_SHORT)
                            .show()
                    binding.txtGetValue.text = text
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
//        val myValue: DatabaseReference = databaseReference.child("heoji")
//            .child( "heoji의 child ab")
//        myValue.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val text = snapshot.getValue(String::class.java)
//                text?.let { Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show() }
//                    ?: Toast.makeText(this@MainActivity, "존재하지 않는 아이입니다.", Toast.LENGTH_SHORT)
//                        .show()
//                binding.txtGetValue.text = text
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
    }

}