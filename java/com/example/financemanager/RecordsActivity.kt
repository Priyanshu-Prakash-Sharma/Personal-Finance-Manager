package com.example.financemanager

// import androidx.lifecycle.Observer // No longer explicitly needed if using the lambda version
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RecordsActivity : AppCompatActivity() {

    private lateinit var totalIncomeTextView: TextView
    private lateinit var totalExpenseTextView: TextView
    private lateinit var totalTextView: TextView
    private lateinit var entriesRecyclerView: RecyclerView
    private lateinit var addEntryButton: Button
    private lateinit var entryAdapter: EntryAdapter
    private lateinit var mainLayout: View

    private val entryViewModel: EntryViewModel by viewModels {
        // Ensure FinanceManagerApplication and its database property are correctly set up
        EntryViewModelFactory((application as FinanceManagerApplication).database.entryDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)

        totalIncomeTextView = findViewById(R.id.totalIncomeTextView)
        totalExpenseTextView = findViewById(R.id.totalExpenseTextView)
        totalTextView = findViewById(R.id.totalTextView)
        entriesRecyclerView = findViewById(R.id.entriesRecyclerView)
        addEntryButton = findViewById(R.id.addEntryButton)
        mainLayout = findViewById(R.id.mainLayout)

        setupRecyclerView() // Call the new setup method

        addEntryButton.setOnClickListener {
            showAddEntryDialog()
        }

        entryViewModel.allEntries.observe(this) { entries -> // Simplified observer
            entries?.let {
                entryAdapter.submitList(it)
                updateTotals(it)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (content.isLaidOut) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(1000)
                            splashScreen.setKeepOnScreenCondition { false }
                        }
                        true
                    } else {
                        false
                    }
                }
            }
        )
    }

    private fun setupRecyclerView() {
        // Initialize the adapter with the delete callback
        entryAdapter = EntryAdapter { entryToDelete ->
            // This lambda is called when a delete button in the adapter is clicked
            showDeleteConfirmationDialog(entryToDelete)
        }
        entriesRecyclerView.adapter = entryAdapter
        entriesRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun showDeleteConfirmationDialog(entry: Entry) {
        AlertDialog.Builder(this) // Use 'this' for Activity context
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete '${entry.description}'?") // Customize as needed
            .setPositiveButton("Delete") { _, _ ->
                entryViewModel.deleteEntry(entry) // Call ViewModel to delete
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddEntryDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.add_entry_dialog, null)
        dialogBuilder.setView(dialogView)

        val entryTypeRadioGroup = dialogView.findViewById<RadioGroup>(R.id.entryTypeRadioGroup)
        val incomeRadioButton = dialogView.findViewById<RadioButton>(R.id.incomeRadioButton)
        val amountEditText = dialogView.findViewById<EditText>(R.id.amountEditText)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.descriptionEditText)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.categorySpinner)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

        val incomeCategories = listOf("Salary", "Bonus", "Rewards", "Shares")
        val expenseCategories = listOf(
            "Shopping", "Entertainment", "Clothing", "Food", "Rent", "Bills",
            "Miscellaneous", "Investment", "Other"
        )

        fun updateCategorySpinner(categories: List<String>) {
            val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = categoryAdapter
        }

        updateCategorySpinner(incomeCategories)

        entryTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.expenseRadioButton) {
                updateCategorySpinner(expenseCategories)
            } else {
                updateCategorySpinner(incomeCategories)
            }
        }

        val dialog = dialogBuilder.create()

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        saveButton.setOnClickListener {
            val selectedType = if (incomeRadioButton.isChecked) "income" else "expense"
            val amountText = amountEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val selectedCategory = categorySpinner.selectedItem?.toString() ?: ""

            if (amountText.isEmpty()) {
                amountEditText.error = "Amount is required"
                return@setOnClickListener
            }
            if (description.isEmpty()) {
                descriptionEditText.error = "Description is required"
                return@setOnClickListener
            }
            if (description.split("\\s+".toRegex()).size > 10) {
                descriptionEditText.error = "Description should be max 10 words"
                return@setOnClickListener
            }
            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                amountEditText.error = "Invalid amount"
                return@setOnClickListener
            }

            val newEntry = Entry(amount = amount, type = selectedType, description = description, category = selectedCategory)
            entryViewModel.insert(newEntry)

            Toast.makeText(this, "Data added", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun updateTotals(entries: List<Entry>) {
        val totalIncome = entries.filter { it.type == "income" }.sumOf { it.amount }
        val totalExpense = entries.filter { it.type == "expense" }.sumOf { it.amount }
        val total = totalIncome - totalExpense
        updateUI(totalIncome, totalExpense, total)
    }

    private fun updateUI(income: Double, expense: Double, totalVal: Double) {
        totalIncomeTextView.text = String.format("%.2f", income)
        totalExpenseTextView.text = String.format("%.2f", expense)
        totalTextView.text = String.format("%.2f", totalVal)
    }
}