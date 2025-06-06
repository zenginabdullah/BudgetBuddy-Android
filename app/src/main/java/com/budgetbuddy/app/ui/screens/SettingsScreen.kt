package com.budgetbuddy.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.budgetbuddy.app.data.PreferencesManager
import androidx.compose.material.icons.filled.AttachMoney


@Composable
fun SettingsScreen(
    currency: String,
    isDark: Boolean,
    notificationsEnabled: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onNotificationsToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val currencyOptions = listOf("₺", "$", "€")
    var currencyMenuExpanded by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 16.dp)
    ) {
        // Başlık
        Text(
            text = "Ayarlar",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        // Ayarlar Kartı
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Para Birimi Ayarı
                SettingsItem(
                    title = "Para Birimi",
                    icon = Icons.Default.Language,
                    content = {
                        Box {
                            Button(
                                onClick = { currencyMenuExpanded = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = currency,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            DropdownMenu(
                                expanded = currencyMenuExpanded,
                                onDismissRequest = { currencyMenuExpanded = false },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                currencyOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        },
                                        onClick = {
                                            onCurrencyChange(option)
                                            currencyMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                // Koyu Tema Ayarı
                SettingsItem(
                    title = "Koyu Tema",
                    icon = Icons.Default.DarkMode,
                    content = {
                        Switch(
                            checked = isDark,
                            onCheckedChange = { onThemeToggle(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                )

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                // Bildirimler Ayarı
                SettingsItem(
                    title = "Bildirimler",
                    icon = Icons.Default.Notifications,
                    content = {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { onNotificationsToggle(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                )

                SettingsItem(
                    title = "Harcama Limiti",
                    icon = Icons.Default.AttachMoney,
                    content = {
                        var limitText by remember { mutableStateOf(PreferencesManager(context).getDailyLimit().toString()) }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.widthIn(max = 250.dp) // Fazla taşmasın
                        ) {
                            OutlinedTextField(
                                value = limitText,
                                onValueChange = { limitText = it },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                label = { Text("₺ Limit") },
                                textStyle = MaterialTheme.typography.bodyMedium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            Button(
                                onClick = {
                                    val parsed = limitText.toDoubleOrNull() ?: 0.0
                                    PreferencesManager(context).setDailyLimit(parsed)
                                    Toast.makeText(context, "Günlük limit güncellendi", Toast.LENGTH_SHORT).show()
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("✓", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Uygulama Bilgileri Kartı
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Text(
                        text = "Uygulama Bilgileri",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Text(
                    text = "BudgetBuddy Bütçe Takip Uygulaması v1.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Alt bilgi
        Text(
            text = "© 2025 BudgetBuddy. Tüm hakları saklıdır.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }

        content()
    }
}

@Composable
fun InfoItem(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}