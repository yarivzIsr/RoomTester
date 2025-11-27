package com.test.roomtester

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.roomtester.data.TestTransaction
import com.test.roomtester.ui.theme.RoomTesterTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val application = application as RoomTesterApplication
        val viewModel = MainViewModel(application.repository, applicationContext)

        setContent {
            RoomTesterTheme {
                TransactionTesterScreen(viewModel)
            }
        }
    }
}

@Composable
fun TransactionTesterScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar messages
    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Room Database Tester",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Transaction Management Buttons
            Button(
                onClick = { viewModel.addTransaction() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Transaction")
            }

            Button(
                onClick = { viewModel.showClearDialog() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear Table")
            }

            Button(
                onClick = { viewModel.toggleRecordsVisibility() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.showRecords) "Hide Records" else "Show Records")
            }

            // Power Management Buttons
            Text(
                text = "Persistence Testing",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Button(
                onClick = { viewModel.forceCloseApp() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Force Close App (Kill Process)")
            }

            Button(
                onClick = { viewModel.restartDevice() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Restart Device (Root/System)")
            }

            Button(
                onClick = { viewModel.shutdownDevice() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Shut Down Device (Root/System)")
            }

            Button(
                onClick = { viewModel.showPullPlugDialog() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pull the Plug Instructions")
            }

            // Records Display
            if (uiState.showRecords) {
                Text(
                    text = "Records (${uiState.transactions.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (uiState.transactions.isEmpty()) {
                        item {
                            Text(
                                text = "No records yet. Add a transaction to get started!",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    } else {
                        items(uiState.transactions) { transaction ->
                            TransactionItem(transaction)
                        }
                    }
                }
            }
        }

        // Dialogs
        if (uiState.showClearDialog) {
            ConfirmationDialog(
                title = "Clear All Records?",
                message = "This will delete all transactions from the database. This action cannot be undone.",
                onConfirm = {
                    viewModel.clearTable()
                },
                onDismiss = {
                    viewModel.dismissClearDialog()
                }
            )
        }

        if (uiState.showPullPlugDialog) {
            PullPlugDialog(
                onDismiss = { viewModel.dismissPullPlugDialog() }
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: TestTransaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Transaction #${transaction.transactionEnumerator}",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "ID: ${transaction.id}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = formatDateTime(transaction.dateTime),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PullPlugDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pull the Plug Test") },
        text = {
            Column {
                Text("To test database persistence after unexpected power loss:")
                Text("\n1. Make sure you have added some transactions")
                Text("2. Now physically disconnect the power to the device")
                Text("3. After power is restored, restart the app")
                Text("4. Verify that all transactions are still present")
                Text("\n⚠️ Warning: This simulates a crash scenario. Ensure no important operations are running.")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Understood")
            }
        }
    )
}

fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    return sdf.format(Date(timestamp))
}