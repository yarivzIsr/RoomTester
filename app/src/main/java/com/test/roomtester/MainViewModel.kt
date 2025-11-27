package com.test.roomtester

import android.content.Context
import android.os.PowerManager
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.roomtester.data.TestTransaction
import com.test.roomtester.data.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val transactions: List<TestTransaction> = emptyList(),
    val showRecords: Boolean = true,
    val showClearDialog: Boolean = false,
    val showPullPlugDialog: Boolean = false,
    val message: String? = null
)

class MainViewModel(private val repository: TransactionRepository, private val context: Context) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allTransactions
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                )
                .collect { transactions ->
                    _uiState.value = _uiState.value.copy(transactions = transactions)
                }
        }
    }

    fun addTransaction() {
        viewModelScope.launch {
            try {
                val maxEnumerator = repository.getMaxEnumerator() ?: 0L
                val newEnumerator = maxEnumerator + 1
                val currentTime = System.currentTimeMillis()

                val transaction = TestTransaction(
                    transactionEnumerator = newEnumerator,
                    dateTime = currentTime
                )

                repository.insert(transaction)
                showMessage("Transaction #$newEnumerator added")
            } catch (e: Exception) {
                showMessage("Error adding transaction: ${e.message}")
            }
        }
    }

    fun showClearDialog() {
        _uiState.value = _uiState.value.copy(showClearDialog = true)
    }

    fun dismissClearDialog() {
        _uiState.value = _uiState.value.copy(showClearDialog = false)
    }

    fun clearTable() {
        viewModelScope.launch {
            try {
                repository.deleteAll()
                _uiState.value = _uiState.value.copy(showClearDialog = false)
                showMessage("All records cleared")
            } catch (e: Exception) {
                showMessage("Error clearing table: ${e.message}")
            }
        }
    }

    fun toggleRecordsVisibility() {
        _uiState.value = _uiState.value.copy(showRecords = !_uiState.value.showRecords)
    }

    fun forceCloseApp() {
        // This will kill the app process, simulating a crash or force stop
        // When the app is restarted, the database should still contain all data
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    fun restartDevice() {
        viewModelScope.launch {
            var success = false

            // Try method 1: Root command
            try {
//                val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "reboot"))
                val process = Runtime.getRuntime().exec("reboot")
                process.waitFor()
                success = true
                return@launch
            } catch (e: Exception) {
                // Root not available or command failed
            }

            // Try method 2: PowerManager (requires system permissions)
            if (!success) {
                try {
                    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                    powerManager.reboot(null)
                    success = true
                    return@launch
                } catch (e: SecurityException) {
                    // Expected on non-system apps
                } catch (e: Exception) {
                    // Other errors
                }
            }

            // If all methods failed, show helpful message
            if (!success) {
                showMessage("Reboot failed: Requires root access or system app signature")
                Toast.makeText(
                    context,
                    "Unable to reboot device.\n\n" +
                    "Options:\n" +
                    "1. Root your device and grant root access\n" +
                    "2. Manually reboot to test persistence\n" +
                    "3. Use 'Pull the Plug' option instead",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun shutdownDevice() {
        viewModelScope.launch {
            var success = false

            // Try method 1: Root command
            try {
                val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "reboot -p"))
                process.waitFor()
                success = true
                return@launch
            } catch (e: Exception) {
                // Root not available or command failed
            }

            // Try method 2: PowerManager shutdown (requires system permissions)
            if (!success) {
                try {
                    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                    val shutdownMethod = powerManager.javaClass.getMethod(
                        "shutdown",
                        Boolean::class.java,
                        String::class.java,
                        Boolean::class.java
                    )
                    shutdownMethod.invoke(powerManager, false, null, false)
                    success = true
                    return@launch
                } catch (e: SecurityException) {
                    // Expected on non-system apps
                } catch (e: Exception) {
                    // Other errors
                }
            }

            // If all methods failed, show helpful message
            if (!success) {
                showMessage("Shutdown failed: Requires root access or system app signature")
                Toast.makeText(
                    context,
                    "Unable to shutdown device.\n\n" +
                    "Options:\n" +
                    "1. Root your device and grant root access\n" +
                    "2. Manually power off to test persistence\n" +
                    "3. Use 'Pull the Plug' option instead",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun showPullPlugDialog() {
        _uiState.value = _uiState.value.copy(showPullPlugDialog = true)
    }

    fun dismissPullPlugDialog() {
        _uiState.value = _uiState.value.copy(showPullPlugDialog = false)
    }

    private fun showMessage(message: String) {
        _uiState.value = _uiState.value.copy(message = message)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

