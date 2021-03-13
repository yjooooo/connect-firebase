package com.example.testknockknock

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.testknockknock.databinding.ActivityMainBinding
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    //데이터베이스의 인스턴스를 가져온다고 생각(즉, Root를 가져온다고 이해하면 쉬움)
    private val databaseReference: DatabaseReference = database.reference
    //private val myRef: DatabaseReference = database.getReference("parentId")
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setOnBtnPushClick()
        setOnBtnGetClick()
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
    }

}