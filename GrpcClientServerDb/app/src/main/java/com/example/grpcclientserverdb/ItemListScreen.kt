package com.example.grpcclientserverdb


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.myapplication.Items

@Composable
fun ItemListScreen(viewModel: ItemsViewModel) {
    val items by viewModel.items.observeAsState(emptyList())

    LazyColumn {
        items(items) { item ->
            ItemRow(item)
        }
    }
}

@Composable
fun ItemRow(item: Items) {
    Row {
        Text(text = item.itemName ?: "", style = MaterialTheme.typography.bodyLarge)
        Text(text = null ?: "-", style = MaterialTheme.typography.bodyLarge)
        Text(text = item.itemQnty ?: "", style = MaterialTheme.typography.bodyLarge)
    }
}

