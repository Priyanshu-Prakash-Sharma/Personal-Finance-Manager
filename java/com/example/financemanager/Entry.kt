package com.example.financemanager

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_entries")
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L, // <--- CHANGE Int to Long, and 0 to 0L
    val amount: Double,
    val type: String,
    val description: String,
    val category: String
)