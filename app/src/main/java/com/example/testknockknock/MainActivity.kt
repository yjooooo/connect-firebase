package com.example.testknockknock

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.testknockknock.databinding.ActivityMainBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private lateinit var mediaRecorder: MediaRecorder
    private var state: Boolean = false
    private lateinit var fileName: String

    //데이터베이스의 인스턴스를 가져온다고 생각(즉, Root를 가져온다고 이해하면 쉬움)
    private val databaseReference: DatabaseReference = database.reference

    //private val myRef: DatabaseReference = database.getReference("parentId")
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        getToday()
        setOnBtnPushClick()
        setOnBtnGetClick()
        setOnBtnImgUploadClick()
        setOnBtnAudioUploadClick()
        setOnBtnRecordClick()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
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

    private fun getToday() {
        val currentTime: Date = Calendar.getInstance().getTime()
        val simpleDate = SimpleDateFormat("yyyy-MM-dd")
        val date = simpleDate.format(currentTime)
        fileName = date.toString()
    }

    private fun setOnBtnRecordClick() {
        binding.btnRecordStart.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //Permission is not granted
                val permissions = arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(this, permissions, 0)
            } else {
                Log.d("record", "start")
                startRecording()
            }
            setOnBtnRecordStopClick()
        }
    }

    private fun setOnBtnRecordStopClick() {
        binding.btnRecordStop.setOnClickListener {
            stopRecording()
        }
    }


    @Suppress("DEPRECATION")
    private fun startRecording() {
        //config and create MediaRecorder Object
        val values = ContentValues(10)
        values.put(MediaStore.MediaColumns.TITLE, "Recorded")
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Audio.Media.IS_RINGTONE, 1)
        values.put(MediaStore.Audio.Media.IS_MUSIC, 1)
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp4")
        values.put(MediaStore.Audio.Media.DATA, fileName)
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings/");

        val audioUri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
        val file = audioUri?.let { getContentResolver().openFileDescriptor(it, "w") };

        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource((MediaRecorder.AudioSource.MIC))
        mediaRecorder?.setOutputFormat((MediaRecorder.OutputFormat.MPEG_4))
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        if (file != null) {
            mediaRecorder?.setOutputFile(file.getFileDescriptor())
        }

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Toast.makeText(this, "레코딩 시작되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        if (state) {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            state = false
            Toast.makeText(this, "중지 되었습니다.", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this, "레코딩 상태가 아닙니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setOnBtnAudioUploadClick() {
        binding.btnAudioUpload.setOnClickListener {
            val audioIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(audioIntent, 2)
        }
    }

    private fun setOnBtnImgUploadClick() {
        binding.btnImgUpload.setOnClickListener {
            val imageIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(imageIntent, 1)
        }
    }

    private fun uploadImgUri(file: Uri) {
        //val file = Uri.fromFile(file)
        firebaseStorage.reference.child("imageFile").child("imageUri.png")
            .putFile(file).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("storage", "upload success")
                }
            }
    }

    private fun uploadAudioUri(file: Uri) {
        //val file = Uri.fromFile(file)
        firebaseStorage.reference.child("audioFile").child(fileName + ".mp4")
            .putFile(file).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("storage", "upload success")
                }
            }
    }

    private fun setOnBtnPushClick() {
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

    private fun setOnBtnGetClick() {
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