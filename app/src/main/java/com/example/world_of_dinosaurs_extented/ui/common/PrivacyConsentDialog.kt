package com.example.world_of_dinosaurs_extented.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.world_of_dinosaurs_extented.R

@Composable
fun PrivacyConsentDialog(
    onAccept: () -> Unit,
    onLearnMore: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current
    val privacyPolicyUrl = stringResource(R.string.privacy_policy_url)

    AlertDialog(
        onDismissRequest = { /* Prevent dismissal by clicking outside */ },
        icon = {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = stringResource(R.string.privacy_consent_title),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = stringResource(R.string.privacy_consent_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "• Camera: AR viewing, QR scanning, image recognition\n• Microphone: Voice chat input\n• Network: API calls to external services",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(stringResource(R.string.privacy_consent_accept))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    uriHandler.openUri(privacyPolicyUrl)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(stringResource(R.string.privacy_consent_learn_more))
            }
        },
        modifier = Modifier.widthIn(max = 400.dp)
    )
}
