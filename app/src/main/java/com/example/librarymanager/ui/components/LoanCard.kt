package com.example.librarymanager.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.librarymanager.domain.model.Loan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LoanCard(
    loan: Loan,
    onReturnClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dueDate = dateFormat.format(Date(loan.dueDate))
    val isOverdue = loan.dueDate < System.currentTimeMillis() && !loan.isReturned

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = loan.clientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = loan.bookTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (loan.isReturned) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Returned",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = "Due: $dueDate",
                style = MaterialTheme.typography.bodySmall,
                color = if (isOverdue) MaterialTheme.colorScheme.onErrorContainer
                else MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!loan.isReturned) {
                Button(
                    onClick = { onReturnClick(loan.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mark as Returned")
                }
            }
        }
    }
}
