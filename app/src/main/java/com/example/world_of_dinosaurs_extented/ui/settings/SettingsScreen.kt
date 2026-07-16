package com.example.world_of_dinosaurs_extented.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.Alignment
import com.example.world_of_dinosaurs_extented.R
import com.example.world_of_dinosaurs_extented.data.ChatProvider
import com.example.world_of_dinosaurs_extented.data.map.MapProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLoginClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val language by viewModel.language.collectAsStateWithLifecycle(initialValue = "en")
    val theme by viewModel.theme.collectAsStateWithLifecycle(initialValue = "system")
    val savedApiKey by viewModel.visionApiKey.collectAsStateWithLifecycle(initialValue = "")
    val globeTimeout by viewModel.globeRotateTimeout.collectAsStateWithLifecycle(initialValue = 10)
    val chatProviderKey by viewModel.chatProvider.collectAsStateWithLifecycle(initialValue = ChatProvider.DEEPSEEK.key)
    val savedChatApiKey by viewModel.chatApiKey.collectAsStateWithLifecycle(initialValue = "")
    val savedChatBaseUrl by viewModel.chatBaseUrl.collectAsStateWithLifecycle(initialValue = "")
    val savedChatModel by viewModel.chatModel.collectAsStateWithLifecycle(initialValue = "")
    val ttsSpeed by viewModel.ttsSpeed.collectAsStateWithLifecycle(initialValue = 1.0f)
    val ttsPitch by viewModel.ttsPitch.collectAsStateWithLifecycle(initialValue = 1.0f)
    val mapProviderKey by viewModel.mapProvider.collectAsStateWithLifecycle(initialValue = MapProvider.AUTO.key)
    val visionProviderKey by viewModel.visionProvider.collectAsStateWithLifecycle(initialValue = "auto")
    val tencentSecretId by viewModel.tencentSecretId.collectAsStateWithLifecycle(initialValue = "")
    val tencentSecretKey by viewModel.tencentSecretKey.collectAsStateWithLifecycle(initialValue = "")
    var apiKeyInput by remember { mutableStateOf("") }
    var tencentSecretIdInput by remember { mutableStateOf("") }
    var tencentSecretKeyInput by remember { mutableStateOf("") }
    var tencentSecretKeyVisible by remember { mutableStateOf(false) }
    var mapProviderDropdownExpanded by remember { mutableStateOf(false) }
    var visionProviderDropdownExpanded by remember { mutableStateOf(false) }
    var apiKeyVisible by remember { mutableStateOf(false) }
    var chatApiKeyInput by remember { mutableStateOf("") }
    var chatApiKeyVisible by remember { mutableStateOf(false) }
    var chatBaseUrlInput by remember { mutableStateOf("") }
    var chatModelInput by remember { mutableStateOf("") }
    var providerDropdownExpanded by remember { mutableStateOf(false) }
    var showVisionKeyGuide by remember { mutableStateOf(false) }
    var showChatKeyGuide by remember { mutableStateOf(false) }
    var showDonateDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val uriHandler = LocalUriHandler.current

    val currentChatProvider = ChatProvider.fromKey(chatProviderKey)

    // Sync input field with saved value when it loads
    LaunchedEffect(savedApiKey) {
        if (apiKeyInput.isEmpty() && savedApiKey.isNotEmpty()) {
            apiKeyInput = savedApiKey
        }
    }
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
    LaunchedEffect(tencentSecretId) {
        if (tencentSecretIdInput.isEmpty() && tencentSecretId.isNotEmpty()) {
            tencentSecretIdInput = tencentSecretId
        }
    }
    LaunchedEffect(tencentSecretKey) {
        if (tencentSecretKeyInput.isEmpty() && tencentSecretKey.isNotEmpty()) {
            tencentSecretKeyInput = tencentSecretKey
        }
    }

    val currentMapProvider = MapProvider.fromKey(mapProviderKey)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
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
            // User Profile section
            OutlinedButton(
                onClick = onProfileClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("User Profile")
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Language section
            Text(
                text = stringResource(R.string.language),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = language == "en",
                    onClick = { viewModel.setLanguage("en") },
                    label = { Text(stringResource(R.string.english)) }
                )
                FilterChip(
                    selected = language == "zh",
                    onClick = { viewModel.setLanguage("zh") },
                    label = { Text(stringResource(R.string.chinese)) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Theme section
            Text(
                text = stringResource(R.string.theme),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = theme == "system",
                    onClick = { viewModel.setTheme("system") },
                    label = { Text(stringResource(R.string.system_default)) }
                )
                FilterChip(
                    selected = theme == "light",
                    onClick = { viewModel.setTheme("light") },
                    label = { Text(stringResource(R.string.light)) }
                )
                FilterChip(
                    selected = theme == "dark",
                    onClick = { viewModel.setTheme("dark") },
                    label = { Text(stringResource(R.string.dark)) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AI Chat Provider section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.chat_provider),
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = { showChatKeyGuide = true }) {
                    Icon(
                        Icons.Default.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.how_to_get_key), style = MaterialTheme.typography.labelSmall)
                }
            }
            Text(
                text = stringResource(R.string.chat_provider_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Provider dropdown
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

            Spacer(modifier = Modifier.height(12.dp))

            // Chat API Key
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

            // Custom URL and model (only for CUSTOM provider)
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

            Spacer(modifier = Modifier.height(8.dp))
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

            Spacer(modifier = Modifier.height(24.dp))

            // Map Provider section
            Text(
                text = "地图服务",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "选择地图服务提供商（自动检测会根据网络环境选择）",
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

            // Unified Vision Provider section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "图像识别服务",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = { showVisionKeyGuide = true }) {
                    Icon(
                        Icons.Default.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("获取Key", style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "选择识别服务，自动检测会根据网络环境选择可用的服务",
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
                        "tencent" -> "腾讯云 Vision"
                        else -> "自动检测"
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
                    listOf(
                        "auto" to "自动检测",
                        "google" to "Google Vision",
                        "tencent" to "腾讯云 Vision"
                    ).forEach { (key, name) ->
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

            // Dynamic API key fields based on selected provider
            if (visionProviderKey == "google") {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Google Vision API Key",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "从 Google Cloud Console 获取",
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
                    keyboardActions = KeyboardActions(onDone = {
                        viewModel.setVisionApiKey(apiKeyInput)
                        focusManager.clearFocus()
                    }),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.setVisionApiKey(apiKeyInput)
                        focusManager.clearFocus()
                    },
                    enabled = apiKeyInput.trim() != savedApiKey
                ) {
                    Text("保存 API Key")
                }
            }

            if (visionProviderKey == "tencent") {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "腾讯云 API 凭据",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "从腾讯云控制台 - 访问管理 - API密钥管理获取",
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
                    Text("保存腾讯云凭据")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Globe auto-rotate section
            Text(
                text = stringResource(R.string.globe_rotate_timeout_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.globe_rotate_timeout_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Slider(
                    value = globeTimeout.toFloat(),
                    onValueChange = { viewModel.setGlobeRotateTimeout(it.toInt()) },
                    valueRange = 3f..30f,
                    steps = 8,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.globe_rotate_timeout_value, globeTimeout),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Voice Settings section
            Text(
                text = stringResource(R.string.voice_settings),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Speech speed slider
            Text(
                text = stringResource(R.string.tts_speed),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Slider(
                    value = ttsSpeed,
                    onValueChange = { viewModel.setTtsSpeed(it) },
                    valueRange = 0.5f..2.0f,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.tts_speed_value, ttsSpeed),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Speech pitch slider
            Text(
                text = stringResource(R.string.tts_pitch),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Slider(
                    value = ttsPitch,
                    onValueChange = { viewModel.setTtsPitch(it) },
                    valueRange = 0.5f..2.0f,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.tts_pitch_value, ttsPitch),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About section
            Text(
                text = stringResource(R.string.about),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.app_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.app_version, "1.1"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.developer_name),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.contact_email),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            val icpVerifyUrl = stringResource(R.string.icp_verify_url)
            TextButton(
                onClick = { uriHandler.openUri(icpVerifyUrl) },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.icp_number),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.icp_verify_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Donate button
            OutlinedButton(
                onClick = { showDonateDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.donate))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Feedback button
            val feedbackUrl = stringResource(R.string.feedback_url)
            OutlinedButton(
                onClick = { uriHandler.openUri(feedbackUrl) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Feedback, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.feedback))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Vision API Key Guide Dialog
    if (showVisionKeyGuide) {
        AlertDialog(
            onDismissRequest = { showVisionKeyGuide = false },
            title = { Text("获取 API Key") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Google Vision",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1. 访问 console.cloud.google.com\n" +
                            "2. 创建或选择项目\n" +
                            "3. 启用 Vision API\n" +
                            "4. 创建 API Key（凭据页面）\n" +
                            "5. 复制 API Key 粘贴到设置中",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "腾讯云视觉识别",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1. 访问 console.cloud.tencent.com\n" +
                            "2. 搜索「图像识别」并开通服务\n" +
                            "3. 进入「访问管理」→「API密钥管理」\n" +
                            "4. 创建 SecretId 和 SecretKey\n" +
                            "5. 复制到设置中",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showVisionKeyGuide = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    // Donate Dialog
    if (showDonateDialog) {
        AlertDialog(
            onDismissRequest = { showDonateDialog = false },
            title = { Text(stringResource(R.string.donate)) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.donate_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(R.drawable.qr_wechat_donate),
                        contentDescription = stringResource(R.string.donate_wechat_label),
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.donate_wechat_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDonateDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    // Chat API Key Guide Dialog
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
            text = {
                Text(
                    text = guideText,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
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
