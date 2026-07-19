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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.world_of_dinosaurs_extented.R
import com.example.world_of_dinosaurs_extented.data.ChatProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val chatProviderKey by viewModel.chatProvider.collectAsStateWithLifecycle(initialValue = ChatProvider.DEEPSEEK.key)
    val savedChatApiKey by viewModel.chatApiKey.collectAsStateWithLifecycle(initialValue = "")
    val savedChatBaseUrl by viewModel.chatBaseUrl.collectAsStateWithLifecycle(initialValue = "")
    val savedChatModel by viewModel.chatModel.collectAsStateWithLifecycle(initialValue = "")
    val currentChatProvider = ChatProvider.fromKey(chatProviderKey)

    var chatApiKeyInput by remember { mutableStateOf("") }
    var chatApiKeyVisible by remember { mutableStateOf(false) }
    var chatBaseUrlInput by remember { mutableStateOf("") }
    var chatModelInput by remember { mutableStateOf("") }
    var providerDropdownExpanded by remember { mutableStateOf(false) }
    var showChatKeyGuide by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(savedChatApiKey) {
        if (chatApiKeyInput.isEmpty() && savedChatApiKey.isNotEmpty()) {
            chatApiKeyInput = savedChatApiKey
        }
    }
    LaunchedEffect(savedChatBaseUrl) {
        if (chatBaseUrlInput.isEmpty() && savedChatBaseUrl.isNotEmpty()) {
            chatBaseUrlInput = savedChatBaseUrl
        }
    }
    LaunchedEffect(savedChatModel) {
        if (chatModelInput.isEmpty() && savedChatModel.isNotEmpty()) {
            chatModelInput = savedChatModel
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.chat_provider)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    TextButton(onClick = { showChatKeyGuide = true }) {
                        Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.how_to_get_key), style = MaterialTheme.typography.labelSmall)
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
            Text(
                text = stringResource(R.string.chat_provider_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = providerDropdownExpanded,
                onExpandedChange = { providerDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = currentChatProvider.displayName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = providerDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = providerDropdownExpanded,
                    onDismissRequest = { providerDropdownExpanded = false }
                ) {
                    ChatProvider.entries.forEach { provider ->
                        DropdownMenuItem(
                            text = { Text(provider.displayName) },
                            onClick = {
                                viewModel.setChatProvider(provider.key)
                                providerDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = chatApiKeyInput,
                onValueChange = { chatApiKeyInput = it },
                label = { Text(stringResource(R.string.chat_api_key_title)) },
                placeholder = { Text(stringResource(R.string.chat_api_key_hint)) },
                singleLine = true,
                visualTransformation = if (chatApiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { chatApiKeyVisible = !chatApiKeyVisible }) {
                        Icon(
                            if (chatApiKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    viewModel.setChatApiKey(chatApiKeyInput)
                    focusManager.clearFocus()
                }),
                modifier = Modifier.fillMaxWidth()
            )

            if (currentChatProvider == ChatProvider.CUSTOM) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = chatBaseUrlInput,
                    onValueChange = { chatBaseUrlInput = it },
                    label = { Text(stringResource(R.string.chat_custom_url)) },
                    placeholder = { Text("https://api.example.com/") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = chatModelInput,
                    onValueChange = { chatModelInput = it },
                    label = { Text(stringResource(R.string.chat_custom_model)) },
                    placeholder = { Text("gpt-3.5-turbo") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        viewModel.setChatBaseUrl(chatBaseUrlInput)
                        viewModel.setChatModel(chatModelInput)
                        focusManager.clearFocus()
                    }),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.setChatApiKey(chatApiKeyInput)
                    if (currentChatProvider == ChatProvider.CUSTOM) {
                        viewModel.setChatBaseUrl(chatBaseUrlInput)
                        viewModel.setChatModel(chatModelInput)
                    }
                    focusManager.clearFocus()
                },
                enabled = chatApiKeyInput.trim() != savedChatApiKey ||
                    (currentChatProvider == ChatProvider.CUSTOM &&
                        (chatBaseUrlInput.trim() != savedChatBaseUrl || chatModelInput.trim() != savedChatModel))
            ) {
                Text(stringResource(R.string.save_api_key))
            }
        }
    }

    if (showChatKeyGuide) {
        val guideText = when (currentChatProvider) {
            ChatProvider.DEEPSEEK -> stringResource(R.string.chat_key_guide_deepseek)
            ChatProvider.QWEN -> stringResource(R.string.chat_key_guide_qwen)
            ChatProvider.GEMINI -> stringResource(R.string.chat_key_guide_gemini)
            ChatProvider.CUSTOM -> stringResource(R.string.chat_key_guide_deepseek)
        }
        val guideUrl = when (currentChatProvider) {
            ChatProvider.DEEPSEEK -> stringResource(R.string.chat_key_guide_deepseek_url)
            ChatProvider.QWEN -> stringResource(R.string.chat_key_guide_qwen_url)
            ChatProvider.GEMINI -> stringResource(R.string.chat_key_guide_gemini_url)
            ChatProvider.CUSTOM -> ""
        }
        AlertDialog(
            onDismissRequest = { showChatKeyGuide = false },
            title = {
                Text(
                    stringResource(R.string.chat_key_guide_title) +
                        " (${currentChatProvider.displayName})"
                )
            },
            text = { Text(text = guideText, style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                if (guideUrl.isNotEmpty()) {
                    TextButton(onClick = { uriHandler.openUri(guideUrl) }) {
                        Text(stringResource(R.string.open_link))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showChatKeyGuide = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }
}
