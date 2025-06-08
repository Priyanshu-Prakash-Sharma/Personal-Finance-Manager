package com.example.financemanager

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EntryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: Entry)

    @Query("SELECT * FROM financial_entries ORDER BY id DESC")
    fun getAllEntries(): LiveData<List<Entry>>

    @Delete // Add this annotation for the delete operation
    suspend fun delete(entry: Entry) // Method to delete a specific entry

    // Optional: If you want to get current totals directly from the database
    // You'll need to decide if you want to calculate this in the ViewModel or here.
    // For simplicity with LiveData, often calculations are done on the observed list.
    // @Query("SELECT SUM(amount) FROM financial_entries WHERE type = :entryType")
    // fun getTotalAmountByType(entryType: String): LiveData<Double?> // Nullable if no entries of that type

    // Example of a delete all function (use with caution)
    // @Query("DELETE FROM financial_entries")
    // suspend fun deleteAll()
}