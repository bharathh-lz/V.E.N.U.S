package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.VenusViewModel
import com.example.ui.theme.VenusCardBorder
import com.example.ui.theme.VenusCyan
import com.example.ui.theme.VenusCyanGlow
import com.example.ui.theme.VenusLavender
import com.example.ui.theme.VenusLavenderGlow
import com.example.ui.theme.VenusMidnightPurple
import com.example.ui.theme.VenusSurfaceDark
import com.example.ui.theme.VenusSurfaceVariant
import com.example.ui.theme.VenusTextMuted
import com.example.ui.theme.VenusTextPrimary
import com.example.ui.theme.VenusTextSecondary

@Composable
fun GamesScreen(
    viewModel: VenusViewModel,
    modifier: Modifier = Modifier
) {
    var selectedGameTab by remember { mutableStateOf("TRIVIA") }

    val triviaQuestion by viewModel.triviaQuestion.collectAsState()
    val triviaOptions by viewModel.triviaOptions.collectAsState()
    val triviaCorrectIndex by viewModel.triviaCorrectIndex.collectAsState()
    val triviaSelected by viewModel.triviaSelected.collectAsState()
    val triviaScore by viewModel.triviaScore.collectAsState()

    val tictactoeBoard by viewModel.tictactoeBoard.collectAsState()
    val tictactoeStatus by viewModel.tictactoeStatus.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hero Header
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = VenusLavenderGlow.copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = "Games",
                                tint = VenusLavender,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "V.E.N.U.S MINI-GAMES SUITE",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = VenusLavender
                        )
                        Text(
                            text = "Interactive Cyber Trivia • Tic-Tac-Toe vs AI",
                            fontSize = 10.sp,
                            color = VenusTextMuted
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Challenge V.E.N.U.S in real-time gaming modules designed for brain training, science trivia, and AI strategy.",
                    fontSize = 12.sp,
                    color = VenusTextSecondary,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Game Selector Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                onClick = { selectedGameTab = "TRIVIA" },
                shape = RoundedCornerShape(20.dp),
                color = if (selectedGameTab == "TRIVIA") VenusCyan else VenusSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (selectedGameTab == "TRIVIA") VenusCyan else VenusCardBorder
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_game_trivia")
            ) {
                Text(
                    text = "🧠 Cyber Trivia",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedGameTab == "TRIVIA") VenusMidnightPurple else VenusTextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Surface(
                onClick = { selectedGameTab = "TICTACTOE" },
                shape = RoundedCornerShape(20.dp),
                color = if (selectedGameTab == "TICTACTOE") VenusLavenderGlow else VenusSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (selectedGameTab == "TICTACTOE") VenusLavender else VenusCardBorder
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_game_tictactoe")
            ) {
                Text(
                    text = "❌⭕ Tic-Tac-Toe",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedGameTab == "TICTACTOE") VenusMidnightPurple else VenusTextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedGameTab == "TRIVIA") {
            // Trivia Game Card
            Card(
                colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, VenusCyan),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SCIENCE & CYBER TRIVIA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = VenusCyan
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = VenusSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder)
                        ) {
                            Text(
                                text = "Score: $triviaScore",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VenusLavender,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = triviaQuestion,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = VenusTextPrimary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Options
                    triviaOptions.forEachIndexed { index, option ->
                        val isChosen = triviaSelected == index
                        val isCorrect = index == triviaCorrectIndex
                        val hasAnswered = triviaSelected != null

                        val containerColor = when {
                            !hasAnswered -> VenusSurfaceVariant
                            isCorrect -> VenusCyan.copy(alpha = 0.3f)
                            isChosen -> VenusMidnightPurple
                            else -> VenusSurfaceVariant.copy(alpha = 0.5f)
                        }

                        val borderColor = when {
                            !hasAnswered -> VenusCardBorder
                            isCorrect -> VenusCyan
                            isChosen -> VenusLavender
                            else -> VenusCardBorder
                        }

                        Surface(
                            onClick = { viewModel.answerTrivia(index) },
                            enabled = !hasAnswered,
                            shape = RoundedCornerShape(12.dp),
                            color = containerColor,
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${('A' + index)}. ",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = VenusLavender
                                )
                                Text(
                                    text = option,
                                    fontSize = 12.sp,
                                    color = VenusTextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (triviaSelected != null) {
                        Button(
                            onClick = { viewModel.nextTrivia() },
                            colors = ButtonDefaults.buttonColors(containerColor = VenusCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Next", tint = VenusMidnightPurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Next Question", color = VenusMidnightPurple, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Tic-Tac-Toe Game Card
            Card(
                colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, VenusLavender),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "V.E.N.U.S AI TIC-TAC-TOE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = VenusLavender
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = tictactoeStatus,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = VenusCyan
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3x3 Grid
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .aspectRatio(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (row in 0 until 3) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (col in 0 until 3) {
                                    val index = row * 3 + col
                                    val cell = tictactoeBoard.getOrElse(index) { "" }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(VenusSurfaceVariant)
                                            .border(1.dp, VenusCardBorder, RoundedCornerShape(10.dp))
                                            .clickable { viewModel.playTicTacToeMove(index) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cell,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (cell == "X") VenusCyan else VenusLavenderGlow
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.resetTicTacToe() },
                        colors = ButtonDefaults.buttonColors(containerColor = VenusSurfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", tint = VenusLavender, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset Board", color = VenusLavender, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
