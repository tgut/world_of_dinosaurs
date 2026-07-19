package com.example.world_of_dinosaurs_extented.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.world_of_dinosaurs_extented.R
import com.example.world_of_dinosaurs_extented.data.map.MapProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsServiceScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val savedApiKey by viewModel.visionApiKey.collectAsStateWithLifecycle(initialValue = "")
    val mapProviderKey by viewModel.mapProvider.collectAsStateWithLifecycle(initialValue = MapProvider.AUTO.key)
    val visionProviderKey by viewModel.visionProvider.collectAsStateWithLifecycle(initialValue = "auto")
    val tencentSecretId by viewModel.tencentSecretId.collectAsStateWithLifecycle(initialValue = "")
    val tencentSecretKey by viewModel.tencentSecretKey.collectAsStateWithLifecycle(initialValue = "")
    val currentMapProvider = MapProvider.fromKey(mapProviderKey)

    var apiKeyInput by remember { mutableStateOf("") }
    var tencentSecretIdInput by remember { mutableStateOf("") }
    var tencentSecretKeyInput by remember { mutableStateOf("") }
    var tencentSecretKeyVisible by remember { mutableStateOf(false) }
    var mapProviderDropdownExpanded by remember { mutableStateOf(false) }
    var visionProviderDropdownExpanded by remember { mutableStateOf(false) }
    var apiKeyVisible by remember { mutableStateOf(false) }
    var showVisionKeyGuide by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(savedApiKey) {
        if (apiKeyInput.isEmpty() && savedApiKey.isNotEmpty()) apiKeyInput = savedApiKey
    }
    LaunchedEffect(tencentSecretId) {
        if (tencentSecretIdInput.isEmpty() && tencentSecretId.isNotEmpty()) tencentSecretIdInput = tencentSecretId
    }
    LaunchedEffect(tencentSecretKey) {
        if (tencentSecretKeyInput.isEmpty() && tencentSecretKey.isNotEmpty()) tencentSecretKeyInput = tencentSecretKey
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Services") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Map Provider", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Select a map service provider (Auto detects based on network)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = mapProviderDropdownExpanded,
                onExpandedChange = { mapProviderDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = currentMapProvider.displayName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mapProviderDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = mapProviderDropdownExpanded,
                    onDismissRequest = { mapProviderDropdownExpanded = false }
                ) {
                    MapProvider.entries.forEach { provider ->
                        DropdownMenuItem(
                            text = { Text(provider.displayName) },
                            onClick = {
                                viewModel.setMapProvider(provider.key)
                                mapProviderDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Vision Provider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Vision Service", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { showVisionKeyGuide = true }) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Get Key", style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Select image recognition service",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = visionProviderDropdownExpanded,
                onExpandedChange = { visionProviderDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = when (visionProviderKey) {
                        "google" -> "Google Vision"
                        "tencent" -> "Tencent Cloud Vision"
                        else -> "Auto Detect"
                    },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = visionProviderDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = visionProviderDropdownExpanded,
                    onDismissRequest = { visionProviderDropdownExpanded = false }
                ) {
                    listOf("auto" to "Auto Detect", "google" to "Google Vision", "tencent" to "Tencent Cloud Vision")
                        .forEach { (key, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    viewModel.setVisionProvider(key)
                                    visionProviderDropdownExpanded = false
                                }
                            )
                        }
                }
            }

            if (visionProviderKey == "google") {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Google Vision API Key", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Get from Google Cloud Console",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    label = { Text("API Key") },
                    singleLine = true,
                    visualTransformation = if (apiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { apiKeyVisible = !apiKeyVisible }) {
                            Icon(
                                if (apiKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { viewModel.setVisionApiKey(apiKeyInput); focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.setVisionApiKey(apiKeyInput); focusManager.clearFocus() },
                    enabled = apiKeyInput.trim() != savedApiKey) {
                    Text("Save API Key")
                }
            }

            if (visionProviderKey == "tencent") {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Tencent Cloud Credentials", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Get from Tencent Cloud Console > Access Management > API Key Management",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = tencentSecretIdInput,
                    onValueChange = { tencentSecretIdInput = it },
                    label = { Text("SecretId") },
                    placeholder = { Text("AKIDxxxxxxxxxxxxxxxx") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = tencentSecretKeyInput,
                    onValueChange = { tencentSecretKeyInput = it },
                    label = { Text("SecretKey") },
                    placeholder = { Text("xxxxxxxxxxxxxxxx") },
                    singleLine = true,
                    visualTransformation = if (tencentSecretKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { tencentSecretKeyVisible = !tencentSecretKeyVisible }) {
                            Icon(
                                if (tencentSecretKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        viewModel.setTencentSecretId(tencentSecretIdInput)
                        viewModel.setTencentSecretKey(tencentSecretKeyInput)
                        focusManager.clearFocus()
                    }),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.setTencentSecretId(tencentSecretIdInput)
                        viewModel.setTencentSecretKey(tencentSecretKeyInput)
                        focusManager.clearFocus()
                    },
                    enabled = tencentSecretIdInput.trim() != tencentSecretId ||
                        tencentSecretKeyInput.trim() != tencentSecretKey
                ) {
                    Text("Save Credentials")
                }
            }
        }
    }

    if (showVisionKeyGuide) {
        AlertDialog(
            onDismissRequest = { showVisionKeyGuide = false },
            title = { Text("Get API Key") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Google Vision", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("1. Go to console.cloud.google.com\n2. Create or select a project\n3. Enable Vision API\n4. Create API Key\n5. Copy and paste it here", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tencent Cloud Vision", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("1. Go to console.cloud.tencent.com\n2. Search for Image Recognition and enable\n3. Go to Access Management > API Key Management\n4. Create SecretId and SecretKey\n5. Paste them here", style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = { TextButton(onClick = { showVisionKeyGuide = false }) { Text("Close") } }
        )
    }
}
