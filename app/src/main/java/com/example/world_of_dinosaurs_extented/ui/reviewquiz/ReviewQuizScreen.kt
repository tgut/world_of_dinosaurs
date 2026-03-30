package com.example.world_of_dinosaurs_extented.ui.reviewquiz

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.world_of_dinosaurs_extented.R
import com.example.world_of_dinosaurs_extented.ui.common.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewQuizScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReviewQuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.review_quiz)) },
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
            uiState.isEmpty -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.need_more_scans),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onNavigateBack) {
                        Text(stringResource(R.string.back))
                    }
                }
            }
            uiState.isComplete -> ReviewQuizResultView(
                uiState = uiState,
                onPlayAgain = viewModel::restart,
                onBack = onNavigateBack,
                modifier = Modifier.padding(padding)
            )
            uiState.currentQuestion != null -> ReviewQuizQuestionView(
                uiState = uiState,
                onAnswerSelected = viewModel::selectAnswer,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun ReviewQuizQuestionView(
    uiState: ReviewQuizUiState,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val question = uiState.currentQuestion ?: return
    val language = uiState.language

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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

        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = question.getLocalizedQuestion(language),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

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

        if (uiState.isAnswered) {
            Spacer(modifier = Modifier.height(16.dp))
            val isCorrect = uiState.selectedAnswer == question.correctIndex
            Text(
                text = if (isCorrect) stringResource(R.string.correct) else stringResource(R.string.incorrect),
                style = MaterialTheme.typography.titleMedium,
                color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = question.getLocalizedExplanation(language),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ReviewQuizResultView(
    uiState: ReviewQuizUiState,
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
