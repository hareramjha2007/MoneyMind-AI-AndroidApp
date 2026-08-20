package com.finly.feature.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finly.core.domain.model.CoachSender
import com.finly.core.ui.theme.AccentPurple
import com.finly.core.ui.theme.CardNavy
import com.finly.core.ui.theme.DeepNavy
import com.finly.core.ui.theme.PrimaryIndigo
import com.finly.core.ui.theme.TextMutedDark
import com.finly.core.ui.theme.TextSecondaryDark

import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.IconButton

@Composable
fun CoachScreen(
    onNavigateToProfile: () -> Unit = {},
    viewModel: CoachViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size, state.messages.lastOrNull()?.text) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateToProfile,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = "Profile Vault",
                    tint = PrimaryIndigo,
                    modifier = Modifier.size(34.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = AccentPurple
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "MoneyMind AI Coach",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "On-device anonymized metrics analysis",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark
                )
            }
        }

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(state.messages) { msg ->
                val isUser = msg.sender == CoachSender.USER
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 20.dp,
                                    topEnd = 20.dp,
                                    bottomStart = if (isUser) 20.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 20.dp
                                )
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) PrimaryIndigo else CardNavy
                        )
                    ) {
                        Text(
                            text = msg.text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dynamic Suggested Prompt Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.suggestedChips) { chip ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardNavy)
                        .clickable { viewModel.sendMessage(chip) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = chip,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Input Field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val onSendClick = {
                if (inputText.isNotBlank()) {
                    viewModel.sendMessage(inputText)
                    inputText = ""
                }
            }

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask your financial coach...", color = TextMutedDark) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Send
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onSend = { onSendClick() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardNavy,
                    unfocusedContainerColor = CardNavy,
                    focusedBorderColor = PrimaryIndigo,
                    unfocusedBorderColor = Color(0xFF2E365C),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(10.dp))

            IconButton(
                onClick = { onSendClick() },
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(PrimaryIndigo)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}
