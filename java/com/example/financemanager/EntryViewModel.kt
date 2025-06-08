package com.example.financemanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EntryViewModel(private val entryDao: EntryDao) : ViewModel() {

    // LiveData holding the list of all entries from the database
    val allEntries: LiveData<List<Entry>> = entryDao.getAllEntries()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(entry: Entry) = viewModelScope.launch(Dispatchers.IO) { // Good to specify Dispatchers.IO
        entryDao.insert(entry)
    }

    /**
     * Launching a new coroutine to delete an entry in a non-blocking way
     */
    fun deleteEntry(entry: Entry) = viewModelScope.launch(Dispatchers.IO) { // Specify Dispatchers.IO
        entryDao.delete(entry)
    }

    // You could add methods here to get total income/expense if you
    // choose to calculate them in the ViewModel based on allEntries.
    // For example, using Transformations.map on allEntries.
    // Or, you could have specific LiveData from the DAO for these totals.
}

/**
 * Factory class to create an instance of EntryViewModel with a constructor parameter (entryDao).
 * ViewModels with constructor parameters need a custom factory.
 */
class EntryViewModelFactory(private val entryDao: EntryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EntryViewModel(entryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}