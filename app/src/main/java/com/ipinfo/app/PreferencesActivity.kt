package com.ipinfo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val android.content.Context.dataStore by preferencesDataStore("user_preferences")

object PreferenceKeys {
    val DARK_THEME = booleanPreferencesKey("dark_theme")
    val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
    val VISUAL_STYLE = intPreferencesKey("visual_style")
    val TEXT_SIZE = floatPreferencesKey("text_size")
    val LAST_MODIFIED = longPreferencesKey("last_modified")
}

class PreferencesActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold { padding ->
                    PreferencesContent(
                        onBackToMenu = { finish() },
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreferencesContent(
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val isDarkThemeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.DARK_THEME] ?: false
    }
    val selectedLanguageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.SELECTED_LANGUAGE] ?: "Română"
    }
    val visualStyleFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.VISUAL_STYLE] ?: 0
    }
    val textSizeFlow: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.TEXT_SIZE] ?: 1.0f
    }
    val lastModifiedFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.LAST_MODIFIED] ?: System.currentTimeMillis()
    }

    val isDarkTheme by isDarkThemeFlow.collectAsState(initial = false)
    val selectedLanguage by selectedLanguageFlow.collectAsState(initial = "Română")
    val visualStyle by visualStyleFlow.collectAsState(initial = 0)
    val textSize by textSizeFlow.collectAsState(initial = 1.0f)
    val lastModified by lastModifiedFlow.collectAsState(initial = System.currentTimeMillis())

    val languages = listOf("Română", "English", "Français", "Español")
    val styles = listOf("Default", "Modern", "Classic", "Minimalist")
    var expandedLanguage by remember { mutableStateOf(false) }
    var expandedStyle by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Logo aplicație",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Setări",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Card {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Temă întunecată")
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = {
                        coroutineScope.launch {
                            context.dataStore.edit { preferences ->
                                preferences[PreferenceKeys.DARK_THEME] = it
                                preferences[PreferenceKeys.LAST_MODIFIED] = System.currentTimeMillis()
                            }
                        }
                    }
                )
            }
        }

        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Limbă")
                Box {
                    OutlinedTextField(
                        value = selectedLanguage,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { expandedLanguage = !expandedLanguage }) {
                                Icon(
                                    painter = painterResource(android.R.drawable.arrow_down_float),
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = expandedLanguage,
                        onDismissRequest = { expandedLanguage = false }
                    ) {
                        languages.forEach { language ->
                            DropdownMenuItem(
                                text = { Text(language) },
                                onClick = {
                                    coroutineScope.launch {
                                        context.dataStore.edit { preferences ->
                                            preferences[PreferenceKeys.SELECTED_LANGUAGE] = language
                                            preferences[PreferenceKeys.LAST_MODIFIED] = System.currentTimeMillis()
                                        }
                                    }
                                    expandedLanguage = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Stil vizual")
                Box {
                    OutlinedTextField(
                        value = styles[visualStyle],
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { expandedStyle = !expandedStyle }) {
                                Icon(
                                    painter = painterResource(android.R.drawable.arrow_down_float),
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = expandedStyle,
                        onDismissRequest = { expandedStyle = false }
                    ) {
                        styles.forEachIndexed { index, style ->
                            DropdownMenuItem(
                                text = { Text(style) },
                                onClick = {
                                    coroutineScope.launch {
                                        context.dataStore.edit { preferences ->
                                            preferences[PreferenceKeys.VISUAL_STYLE] = index
                                            preferences[PreferenceKeys.LAST_MODIFIED] = System.currentTimeMillis()
                                        }
                                    }
                                    expandedStyle = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Dimensiune text")
                Slider(
                    value = textSize,
                    onValueChange = { newSize ->
                        coroutineScope.launch {
                            context.dataStore.edit { preferences ->
                                preferences[PreferenceKeys.TEXT_SIZE] = newSize
                                preferences[PreferenceKeys.LAST_MODIFIED] = System.currentTimeMillis()
                            }
                        }
                    },
                    valueRange = 0.5f..2.0f,
                    steps = 2
                )
                Text("Scală: ${String.format("%.1f", textSize)}x")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Ultima modificare: ${java.util.Date(lastModified)}",
            style = MaterialTheme.typography.bodySmall
        )

        Button(
            onClick = onBackToMenu,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Înapoi")
        }
    }
}