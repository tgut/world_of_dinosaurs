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
                title = { Text(stringResource(R.string.settings_services_title)) },
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
            Text(stringResource(R.string.map_provider), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                stringResource(R.string.map_provider_desc),
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
                Text(stringResource(R.string.vision_service), style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { showVisionKeyGuide = true }) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.get_key), style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                stringResource(R.string.vision_service_desc),
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
                        else -> stringResource(R.string.auto_detect)
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
                    listOf("auto" to stringResource(R.string.auto_detect), "google" to "Google Vision", "tencent" to "Tencent Cloud Vision")
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
                Text(stringResource(R.string.google_vision_api_key), style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    stringResource(R.string.google_vision_console),
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
                    Text(stringResource(R.string.save_api_key))
                }
            }

            if (visionProviderKey == "tencent") {
                Spacer(modifier = Modifier.height(12.dp))
                Text(stringResource(R.string.tencent_cloud_credentials), style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    stringResource(R.string.tencent_cloud_console_hint),
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
                    Text(stringResource(R.string.save_credentials))
                }
            }
        }
    }

    if (showVisionKeyGuide) {
        AlertDialog(
            onDismissRequest = { showVisionKeyGuide = false },
            title = { Text(stringResource(R.string.vision_key_guide_title)) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(stringResource(R.string.vision_key_guide_google_title), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(stringResource(R.string.vision_key_guide_google_steps), style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.vision_key_guide_tencent_title), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(stringResource(R.string.vision_key_guide_tencent_steps), style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = { TextButton(onClick = { showVisionKeyGuide = false }) { Text(stringResource(R.string.vision_key_guide_close)) } }
        )
    }
}
