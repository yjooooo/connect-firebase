package com.example.testknockknock

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.testknockknock.databinding.ActivityMainBinding
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    //데이터베이스의 인스턴스를 가져온다고 생각(즉, Root를 가져온다고 이해하면 쉬움)
    private val databaseReference: DatabaseReference = database.reference
    private val myRef: DatabaseReference = database.getReference("parentId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        lateinit var parentId: String
        binding.btnPush.setOnClickListener {
            parentId = binding.edtParentId.text.toString()
            databaseReference.child("parentId").child(parentId).setValue(parentId+parentId)
            Toast.makeText(this, "push", Toast.LENGTH_SHORT).show()
        }

        lateinit var getId: String
        binding.btnGet.setOnClickListener {
            getId = binding.edtGetId.text.toString()
            val myValue: DatabaseReference = myRef.child(getId)
            myValue.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val text = snapshot.getValue(String::class.java)
                    text?.let{Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()}
                    binding.txtGetValue.text = text
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }


        //val myRef: DatabaseReference = database.getReference("parentId") //Root밑에 있는 “users”라는 위치를 참조함

    }
}