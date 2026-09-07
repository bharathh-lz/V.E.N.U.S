package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.UserVaultItem
import com.example.ui.VenusViewModel
import com.example.ui.theme.VenusBgDark
import com.example.ui.theme.VenusCardBorder
import com.example.ui.theme.VenusCyan
import com.example.ui.theme.VenusCyanGlow
import com.example.ui.theme.VenusSurfaceDark
import com.example.ui.theme.VenusSurfaceVariant
import com.example.ui.theme.VenusTextMuted
import com.example.ui.theme.VenusTextPrimary
import com.example.ui.theme.VenusTextSecondary
import com.example.ui.theme.VenusViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataFeedScreen(
    viewModel: VenusViewModel,
    userDataItems: List<UserVaultItem>,
    onNavigateToAssistant: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val categories = listOf("All", "Identity", "Profession", "Preferences", "Habits", "Custom Rules")

    val filteredItems = if (selectedCategoryIndex == 0) {
        userDataItems
    } else {
        val cat = categories[selectedCategoryIndex]
        userDataItems.filter { it.category.equals(cat, ignoreCase = true) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VenusBgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FEED MY DATA",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = VenusCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Encrypted Local Memory & Context Vault",
                        fontSize = 12.sp,
                        color = VenusTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = VenusSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, VenusCyan.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = VenusCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AES-256",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VenusCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Test Personalization Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, VenusViolet.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Personalize",
                                tint = VenusViolet,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Context-Aware Neural Link",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = VenusTextPrimary
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.testPersonalizedDataBriefing()
                                onNavigateToAssistant()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VenusViolet),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("test_briefing_button")
                        ) {
                            Text(text = "Test Briefing", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "V.E.N.U.S feeds these encrypted data records into its neural engine to deliver bespoke answers, habits recall, and proactive context suggestions.",
                        fontSize = 11.sp,
                        color = VenusTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Categories Filter Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedCategoryIndex,
                containerColor = Color.Transparent,
                contentColor = VenusCyan,
                edgePadding = 0.dp,
                divider = {}
            ) {
                categories.forEachIndexed { index, cat ->
                    Tab(
                        selected = selectedCategoryIndex == index,
                        onClick = { selectedCategoryIndex = index },
                        text = {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedCategoryIndex == index) VenusCyan else VenusTextMuted
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User Memory Vault List
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Empty",
                            tint = VenusTextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No records in this category",
                            fontSize = 13.sp,
                            color = VenusTextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        DataVaultCard(
                            item = item,
                            onDelete = { viewModel.deleteUserDataItem(item) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }

        // Floating Action Button to Add Data
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = VenusCyan,
            contentColor = Color.Black,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_data_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Feed Data")
        }
    }

    if (showAddDialog) {
        AddDataVaultDialog(
            categories = categories.drop(1),
            onDismiss = { showAddDialog = false },
            onConfirm = { cat, label, value ->
                viewModel.addUserDataItem(cat, label, value)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun DataVaultCard(
    item: UserVaultItem,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vault_card_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = VenusSurfaceVariant,
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Text(
                            text = item.category.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = VenusViolet,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = item.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VenusTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.decryptedValue,
                    fontSize = 13.sp,
                    color = VenusCyanGlow,
                    lineHeight = 18.sp
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Item",
                    tint = VenusTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDataVaultDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (category: String, label: String, value: String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "Identity") }
    var label by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VenusSurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "Feed Data",
                    tint = VenusCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Personalized Data",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = VenusTextPrimary
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "This information is encrypted locally using AES-GCM and used solely to tailor V.E.N.U.S's responses to you.",
                    fontSize = 11.sp,
                    color = VenusTextSecondary
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VenusCyan,
                            unfocusedBorderColor = VenusCardBorder,
                            focusedTextColor = VenusTextPrimary,
                            unfocusedTextColor = VenusTextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Data Label (e.g. Favorite Food, Pet Name)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VenusCyan,
                        unfocusedBorderColor = VenusCardBorder,
                        focusedTextColor = VenusTextPrimary,
                        unfocusedTextColor = VenusTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("data_label_input")
                )

                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Details / Preference Value") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VenusCyan,
                        unfocusedBorderColor = VenusCardBorder,
                        focusedTextColor = VenusTextPrimary,
                        unfocusedTextColor = VenusTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("data_value_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (label.isNotBlank() && value.isNotBlank()) {
                        onConfirm(selectedCategory, label, value)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = VenusCyan),
                modifier = Modifier.testTag("confirm_add_data_btn")
            ) {
                Text(text = "Encrypt & Store", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = VenusTextMuted)
            }
        }
    )
}
