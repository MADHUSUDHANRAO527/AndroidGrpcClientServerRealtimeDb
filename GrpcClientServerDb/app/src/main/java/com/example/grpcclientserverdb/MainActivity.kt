package com.example.grpcclientserverdb


import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.grpcclientserverdb.ui.theme.GrpcClientServerDbTheme
import com.example.myapplication.Items
import com.example.myapplication.toMap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


import myproto.KdsRpc
import myproto.KdsServer
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import myproto.TriggerResponse
import java.net.BindException
import kotlin.random.Random

/*import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject*/
class MainActivity : ComponentActivity(), TriggerResponse {
    //    private val kdsClient = KdsClient()
//    private val default: SyncResponse = kdsClient.defaultResponse
    // private val uri by lazy { Uri.parse("http://10.0.2.2:50051/") }
    private val uri by lazy { Uri.parse("http://0.0.0.0:50051/") }

    //    private val uri by lazy { Uri.parse("http://localhost:50051/") }
    private val service by lazy { KdsRpc(uri, this) }
    private val server = KdsServer()

    //    private val personCollectionRef = Firebase.firestore.collection("persons")
    private val database = Firebase.database
    private val itemsRef = database.getReference("Items")

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch {
            try {
                server.start()
            } catch (e: BindException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        enableEdgeToEdge()
        setContent {
            GrpcClientServerDbTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    val viewModel = ViewModelProvider(this).get(ItemsViewModel::class.java)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Greeter(service)
                            ItemListScreen(viewModel)
                        }
                    }
                }
            }
        }
       /* itemsRef.removeValue().addOnCompleteListener { taskId ->
            if (taskId.isSuccessful) {
                Log.d("Firebase", "Item data deleted successfully")
            } else {
                Log.e("Firebase", "Failed to delete Item data")
            }
        }*/
    }

    override fun success(response: String) {
        val item = Items(Random.nextInt(1,100).toString(), response.split("-")[0], response.split("-")[1])
        val userValues = item.toMap()
        itemsRef.child(item.id!!).setValue(userValues)
            .addOnSuccessListener {
                Log.d("Firebase", "User data saved successfully")
            }
            .addOnFailureListener {
                Log.e("Firebase", "Failed to save user data")
            }
    }

    override fun error() {
        Log.d("DB", "Failed to read value.")
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Greeter(kdsRpc: KdsRpc) {
    val response = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    scope.launch {
        kdsRpc.responseFlow.collect { it ->
            response.value = it
        }
    }
    val stringBuilder = StringBuilder()


    val itemName = remember { mutableStateOf("") }
    val itemQnty = remember { mutableStateOf("") }
    val itemPrice = remember { mutableStateOf("") }
//    val response1 = remember { mutableStateOf<SyncResponse>(default)}
    Column {
        OutlinedTextField(value = itemName.value,
            onValueChange = {
                itemName.value = it
                stringBuilder.append(it)
            },
            label = { Text("Item Name") }
        )
        OutlinedTextField(value = itemQnty.value,
            onValueChange = {
                itemQnty.value = it
                stringBuilder.append("-$it")
            },
            label = { Text("Quantity") }
        )
        OutlinedTextField(value = itemPrice.value,
            onValueChange = {
                itemPrice.value = it
                stringBuilder.append("-$it")
            },
            label = { Text("Price") }
        )

        Button(onClick = {
            scope.launch { kdsRpc.sync(itemName.value, itemQnty.value, itemPrice.value) }
        }) {
            Text("Click to send request!")
        }
      //  Text("Response is ${response.value}")

    }

}


