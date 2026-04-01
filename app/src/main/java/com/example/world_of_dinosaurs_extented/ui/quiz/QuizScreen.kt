package com.example.world_of_dinosaurs_extented.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.ads.rewarded.RewardedAd
import com.example.world_of_dinosaurs_extented.R
import com.example.world_of_dinosaurs_extented.ui.common.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 激励视频广告状态
    var loadedAd by remember { mutableStateOf<RewardedAd?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.quiz)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingIndicator(modifier = Modifier.padding(padding))
            uiState.isComplete -> QuizResultView(
                uiState = uiState,
                onPlayAgain = viewModel::restart,
                onBack = onNavigateBack,
                modifier = Modifier.padding(padding)
            )
            uiState.currentQuestion != null -> QuizQuestionView(
                uiState = uiState,
                onAnswerSelected = viewModel::selectAnswer,
                onWatchAdToUnlock = {
                    // 如果已有预加载广告则直接播放，否则先加载
                    val ad = loadedAd
                    if (ad != null) {
                        loadedAd = null
                        viewModel.adManager.showRewardedAd(
                            activity = context as androidx.activity.ComponentActivity,
                            ad = ad,
                            onRewarded = { viewModel.unlockAnalysis() },
                            onDismissed = {}
                        )
                    } else {
                        viewModel.requestRewardedAd(
                            onLoaded = { newAd ->
                                viewModel.adManager.showRewardedAd(
                                    activity = context as androidx.activity.ComponentActivity,
                                    ad = newAd,
                                    onRewarded = { viewModel.unlockAnalysis() },
                                    onDismissed = {}
                                )
                            },
                            onFailed = {}
                        )
                    }
                },
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun QuizQuestionView(
    uiState: QuizUiState,
    onAnswerSelected: (Int) -> Unit,
    onWatchAdToUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val question = uiState.currentQuestion ?: return
    val language = uiState.language

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress
        LinearProgressIndicator(
            progress = { uiState.progress },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.question_of, uiState.currentIndex + 1, uiState.questions.size),
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Question
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = question.getLocalizedQuestion(language),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Options
        question.getLocalizedOptions(language).forEachIndexed { index, option ->
            val isSelected = uiState.selectedAnswer == index
            val isCorrect = index == question.correctIndex
            val containerColor = when {
                !uiState.isAnswered -> MaterialTheme.colorScheme.surface
                isCorrect -> MaterialTheme.colorScheme.primaryContainer
                isSelected -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surface
            }

            OutlinedCard(
                onClick = { onAnswerSelected(index) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = containerColor)
            ) {
                Text(
                    text = option,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // 答题后显示答案解析区域
        if (uiState.isAnswered) {
            Spacer(modifier = Modifier.height(16.dp))
            val isCorrect = uiState.selectedAnswer == question.correctIndex
            Text(
                text = if (isCorrect) stringResource(R.string.correct) else stringResource(R.string.incorrect),
                style = MaterialTheme.typography.titleMedium,
                color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.analysisUnlocked) {
                // 解析已解锁 — 直接显示
                Text(
                    text = question.getLocalizedExplanation(language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                // 未解锁 — 显示"看广告解锁"按钮
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.unlock_analysis_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (uiState.isLoadingAd) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Button(onClick = onWatchAdToUnlock) {
                                Icon(
                                    Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.watch_ad_unlock))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizResultView(
    uiState: QuizUiState,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val result = uiState.result ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.quiz_complete),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = result.grade,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.score_format, result.correctAnswers, result.totalQuestions),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "${result.percentage.toInt()}%",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onPlayAgain, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.play_again))
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.back))
        }
    }
}
