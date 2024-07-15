package com.example.grpcclientserverdb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.Items
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ItemsViewModel : ViewModel() {
    private val _items = MutableLiveData<List<Items>>()
    val items: LiveData<List<Items>> = _items

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val itemsRef: DatabaseReference = database.getReference("Items")

    init {
        fetchItems()
    }

    private fun fetchItems() {
        itemsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val itemList = mutableListOf<Items>()
                    for (snapshot in dataSnapshot.children) {
                        val item = snapshot.getValue(Items::class.java)
                        if (item != null) {
                            itemList.add(item)
                        }
                    _items.value = itemList
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}
