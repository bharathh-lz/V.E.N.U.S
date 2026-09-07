package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.VenusViewModel
import com.example.ui.theme.VenusBgDark
import com.example.ui.theme.VenusCardBorder
import com.example.ui.theme.VenusCyan
import com.example.ui.theme.VenusCyanGlow
import com.example.ui.theme.VenusLavender
import com.example.ui.theme.VenusLavenderGlow
import com.example.ui.theme.VenusMidnightPurple
import com.example.ui.theme.VenusPurple
import com.example.ui.theme.VenusSurfaceDark
import com.example.ui.theme.VenusSurfaceVariant
import com.example.ui.theme.VenusTextMuted
import com.example.ui.theme.VenusTextPrimary
import com.example.ui.theme.VenusTextSecondary
import com.example.ui.theme.VenusViolet

enum class StudyTab(val label: String, val icon: ImageVector) {
    EXPLAIN("Concept Explainer", Icons.Default.Lightbulb),
    QUIZ("Interactive Quiz", Icons.Default.Quiz),
    SCHEDULE("Study Timetable", Icons.Default.CalendarMonth),
    CODE_TUTOR("Coding Tutor", Icons.Default.Code)
}

@Composable
fun StudyAssistantScreen(
    viewModel: VenusViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(StudyTab.EXPLAIN) }

    val conceptResult by viewModel.studyConceptResult.collectAsState()
    val isExplaining by viewModel.isExplainingStudy.collectAsState()

    val quizResult by viewModel.studyQuizResult.collectAsState()
    val isGeneratingQuiz by viewModel.isGeneratingStudyQuiz.collectAsState()

    val scheduleResult by viewModel.studyScheduleResult.collectAsState()
    val isGeneratingSchedule by viewModel.isGeneratingStudySchedule.collectAsState()

    var conceptQuery by remember { mutableStateOf("Quantum Entanglement") }
    var explanationStyle by remember { mutableStateOf("Feynman Technique (Simple Analogies)") }

    var quizTopic by remember { mutableStateOf("Data Structures & Algorithms") }
    var quizDifficulty by remember { mutableStateOf("Intermediate") }

    var subjectsList by remember { mutableStateOf("Mobile Dev, Operating Systems, Machine Learning") }
    var dailyStudyHours by remember { mutableStateOf("3.5 Hours") }
    var targetGoal by remember { mutableStateOf("A+ Semester Exams & Technical Interview Prep") }

    var codingQuery by remember { mutableStateOf("Explain how Dijkstra's algorithm works with step-by-step logic") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VenusBgDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = VenusSurfaceDark,
                border = BorderStroke(1.dp, VenusCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = VenusViolet.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, VenusViolet),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = "Study Assistant",
                                        tint = VenusCyan,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "VENUS STUDY ASSISTANT",
                                    color = VenusTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Neural Tutoring • Quizzes • Timetables • Deep Concepts",
                                    color = VenusCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                viewModel.speechManager.speak("Study Assistant mode active. Select a concept to explain, take an AI quiz, or generate an exam study schedule.")
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Listen Overview",
                                tint = VenusLavender
                            )
                        }
                    }
                }
            }
        }

        // Mode Switcher Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StudyTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) VenusPurple else VenusSurfaceDark,
                        border = BorderStroke(1.dp, if (isSelected) VenusCyan else VenusCardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { currentTab = tab }
                            .testTag("study_tab_${tab.name.lowercase()}")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = if (isSelected) VenusCyan else VenusTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tab.label,
                                color = if (isSelected) VenusTextPrimary else VenusTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Tab Content
        when (currentTab) {
            StudyTab.EXPLAIN -> {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = VenusSurfaceDark,
                        border = BorderStroke(1.dp, VenusCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🧠 Concept Explainer",
                                color = VenusTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Break down complex topics using analogies and intuitive breakdowns.",
                                color = VenusTextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            OutlinedTextField(
                                value = conceptQuery,
                                onValueChange = { conceptQuery = it },
                                label = { Text("Enter Topic / Concept", color = VenusTextMuted) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("study_concept_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VenusCyan,
                                    unfocusedBorderColor = VenusCardBorder,
                                    focusedTextColor = VenusTextPrimary,
                                    unfocusedTextColor = VenusTextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Explanation Style:", color = VenusTextSecondary, fontSize = 11.sp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "Feynman Technique (Simple)",
                                    "ELI5 (Like I'm 5)",
                                    "Academic Deep Dive"
                                ).forEach { style ->
                                    val isPicked = explanationStyle.startsWith(style.take(6))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isPicked) VenusMidnightPurple else VenusSurfaceVariant,
                                        border = BorderStroke(1.dp, if (isPicked) VenusLavender else Color.Transparent),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { explanationStyle = style }
                                    ) {
                                        Text(
                                            text = style.substringBefore(" ("),
                                            color = if (isPicked) VenusCyan else VenusTextSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = if (isPicked) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.explainStudyConcept(conceptQuery, explanationStyle)
                                },
                                enabled = !isExplaining && conceptQuery.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("explain_concept_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = VenusPurple),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (isExplaining) {
                                    CircularProgressIndicator(
                                        color = VenusCyan,
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Synthesizing Explanation...")
                                } else {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = VenusCyan)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Explain with VENUS", fontWeight = FontWeight.Bold)
                                }
                            }

                            if (!conceptResult.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = VenusMidnightPurple,
                                    border = BorderStroke(1.dp, VenusLavenderGlow),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "CONCEPT BREAKDOWN",
                                                color = VenusCyan,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp
                                            )
                                            IconButton(
                                                onClick = {
                                                    conceptResult?.let { viewModel.speechManager.speak(it) }
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.VolumeUp,
                                                    contentDescription = "Speak explanation",
                                                    tint = VenusCyan
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = conceptResult ?: "",
                                            color = VenusTextPrimary,
                                            fontSize = 13.sp,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            StudyTab.QUIZ -> {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = VenusSurfaceDark,
                        border = BorderStroke(1.dp, VenusCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📝 Interactive AI Quiz",
                                color = VenusTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Test your mastery with AI-generated multiple-choice questions & immediate scoring.",
                                color = VenusTextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            OutlinedTextField(
                                value = quizTopic,
                                onValueChange = { quizTopic = it },
                                label = { Text("Subject or Topic", color = VenusTextMuted) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("quiz_topic_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VenusCyan,
                                    unfocusedBorderColor = VenusCardBorder,
                                    focusedTextColor = VenusTextPrimary,
                                    unfocusedTextColor = VenusTextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Difficulty Level:", color = VenusTextSecondary, fontSize = 11.sp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Beginner", "Intermediate", "Advanced / Mastery").forEach { diff ->
                                    val isSelected = quizDifficulty.startsWith(diff.take(4))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) VenusMidnightPurple else VenusSurfaceVariant,
                                        border = BorderStroke(1.dp, if (isSelected) VenusCyan else Color.Transparent),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { quizDifficulty = diff }
                                    ) {
                                        Text(
                                            text = diff.substringBefore(" /"),
                                            color = if (isSelected) VenusCyan else VenusTextSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.generateStudyQuiz(quizTopic, 3, quizDifficulty)
                                },
                                enabled = !isGeneratingQuiz && quizTopic.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("generate_quiz_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = VenusPurple),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (isGeneratingQuiz) {
                                    CircularProgressIndicator(
                                        color = VenusCyan,
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generating Quiz...")
                                } else {
                                    Icon(imageVector = Icons.Default.Quiz, contentDescription = null, tint = VenusCyan)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generate Quiz Questions", fontWeight = FontWeight.Bold)
                                }
                            }

                            if (!quizResult.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = VenusMidnightPurple,
                                    border = BorderStroke(1.dp, VenusCyanGlow),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "QUIZ CHALLENGE",
                                                color = VenusCyan,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp
                                            )
                                            IconButton(
                                                onClick = {
                                                    quizResult?.let { viewModel.speechManager.speak(it) }
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.VolumeUp,
                                                    contentDescription = "Read quiz aloud",
                                                    tint = VenusCyan
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = quizResult ?: "",
                                            color = VenusTextPrimary,
                                            fontSize = 13.sp,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            StudyTab.SCHEDULE -> {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = VenusSurfaceDark,
                        border = BorderStroke(1.dp, VenusCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📅 Smart Study Timetable",
                                color = VenusTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Builds structured Pomodoro intervals, active recall slots, and balanced schedules.",
                                color = VenusTextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            OutlinedTextField(
                                value = subjectsList,
                                onValueChange = { subjectsList = it },
                                label = { Text("Subjects / Exam Modules", color = VenusTextMuted) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VenusCyan,
                                    unfocusedBorderColor = VenusCardBorder,
                                    focusedTextColor = VenusTextPrimary,
                                    unfocusedTextColor = VenusTextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = dailyStudyHours,
                                    onValueChange = { dailyStudyHours = it },
                                    label = { Text("Daily Hours", color = VenusTextMuted) },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = VenusCyan,
                                        unfocusedBorderColor = VenusCardBorder,
                                        focusedTextColor = VenusTextPrimary,
                                        unfocusedTextColor = VenusTextPrimary
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                OutlinedTextField(
                                    value = targetGoal,
                                    onValueChange = { targetGoal = it },
                                    label = { Text("Target Goal", color = VenusTextMuted) },
                                    modifier = Modifier.weight(1.5f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = VenusCyan,
                                        unfocusedBorderColor = VenusCardBorder,
                                        focusedTextColor = VenusTextPrimary,
                                        unfocusedTextColor = VenusTextPrimary
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.generateStudySchedule(subjectsList, dailyStudyHours, targetGoal)
                                },
                                enabled = !isGeneratingSchedule && subjectsList.isNotBlank(),
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = VenusPurple),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (isGeneratingSchedule) {
                                    CircularProgressIndicator(
                                        color = VenusCyan,
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generating Timetable...")
                                } else {
                                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = VenusCyan)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generate Study Timetable", fontWeight = FontWeight.Bold)
                                }
                            }

                            if (!scheduleResult.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = VenusMidnightPurple,
                                    border = BorderStroke(1.dp, VenusLavender),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "OPTIMIZED TIMETABLE",
                                            color = VenusCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = scheduleResult ?: "",
                                            color = VenusTextPrimary,
                                            fontSize = 13.sp,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            StudyTab.CODE_TUTOR -> {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = VenusSurfaceDark,
                        border = BorderStroke(1.dp, VenusCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "💻 Coding & Algorithmic Tutor",
                                color = VenusTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ask how any algorithm works, debug syntax, or learn new software patterns.",
                                color = VenusTextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            OutlinedTextField(
                                value = codingQuery,
                                onValueChange = { codingQuery = it },
                                label = { Text("Code / Algorithm Question", color = VenusTextMuted) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VenusCyan,
                                    unfocusedBorderColor = VenusCardBorder,
                                    focusedTextColor = VenusTextPrimary,
                                    unfocusedTextColor = VenusTextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.explainStudyConcept(codingQuery, "Technical Coding Breakdown & Code Example")
                                },
                                enabled = !isExplaining && codingQuery.isNotBlank(),
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = VenusPurple),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = VenusCyan)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Explain Code & Logic", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
