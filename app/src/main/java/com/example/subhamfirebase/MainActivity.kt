package com.example.subhamfirebase

import android.content.ContentValues.TAG
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.subhamfirebase.ui.theme.SubhamFirebaseTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            val userList= remember {
                mutableStateOf<List<User>>(emptyList())
            }
            LaunchedEffect(Unit) {
                fetchStudents {users->
                    userList.value=users
                }
            }
            SubhamFirebaseTheme {
                //addUserScreen()
                /*LazyColumn (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ){
                    items(userList.value){user->
                        Text(text = "NAME:${user.name}, Age:${user.age}, SIC:${user.sic}")
                    }
                }*/
                deleteUserScreen()
            }
        }
    }
    val db=Firebase.firestore
    fun addUsers(name:String, age:Int,sic:String){
        val User=User(name,age,sic)
        /*db.collection("students")
            .add(User)
            .addOnSuccessListener{docRef->
                Log.d(TAG,"DOCUMENT SNAPSHOT ADDED WITH ID: ${docRef.id}")
            }
            .addOnFailureListener{e->
                Log.w(TAG,"Error Adding Document",e)
            }*/
        db.collection("students")
            .document(sic)
            .set(User)
    }

    fun fetchStudents(onResult: (List<User>)->Unit){
        db.collection("students")
            .get()
            .addOnSuccessListener {result->
                val userList=result.map{
                    document->document.toObject(User::class.java)
                }
                onResult(userList)
            }
            .addOnFailureListener{e->
                Log.w(TAG,"Error getting documents",e)
            }

    }
    fun deleteStudent(sic: String) {
        val docRef = db.collection("students").document(sic)
        val updates = hashMapOf<String, Any>(
            "sic" to FieldValue.delete()
        )
        docRef.update(updates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Field 'sic' successfully deleted!")
                } else {
                    Log.w(TAG, "Error deleting 'sic' field", task.exception)
                }
            }
    }
    @Composable
    fun addUserScreen(){
        var name by remember{ mutableStateOf("") }
        var age by remember {
            mutableStateOf("")
        }
        var sic by remember {
            mutableStateOf("")
        }
        Column(
            modifier=Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            TextField(value = name, onValueChange = {name=it},
                label={ Text(text = "ENTER NAME")},
                modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = age, onValueChange = {age=it},
                label={ Text(text = "ENTER AGE")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = sic, onValueChange = {sic=it},
                label={ Text(text = "ENTER SIC")},
                //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth())
            Button(onClick = { addUsers(name, age.toInt(), sic) }) {
                Text("Add to FireStore DB")
            }


        }
    }
    @Composable
    fun deleteUserScreen() {
        var sic by remember { mutableStateOf("") }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = sic,
                onValueChange = { sic = it },
                label = { Text(text = "ENTER SIC TO DELETE") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { deleteStudent(sic) }) {
                Text("Delete from FireStore DB")
            }
        }
    }


}
//DATA CLASS
data class User(
    val name:String= " ",
    val age:Int=0,
    val sic:String=" "
)
