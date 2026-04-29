package org.example.dictionary

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*

@OptIn(FlowPreview::class)
object Display {

    private val queries = Channel<String>()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val repository = Repository

    private val enterWordLabel = JLabel("Enter word: ")
    private val searchField = JTextField(20).apply {
        addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                loadDefinitions()
            }
        })
    }
    private val searchButton = JButton("Search").apply {
        addActionListener {
            loadDefinitions()
        }
    }
    private val resultArea = JTextArea(25, 50).apply {
        isEditable = false
        lineWrap = true
        wrapStyleWord = true
    }
    private val topPanel = JPanel().apply {
        add(enterWordLabel)
        add(searchField)
        add(searchButton)
    }
    private val mainFrame = JFrame("Dictionary App").apply {
        layout = BorderLayout()
        add(topPanel, BorderLayout.NORTH)
        add(JScrollPane(resultArea), BorderLayout.CENTER)
        pack()
    }

    private fun loadDefinitions() {
        scope.launch {
            queries.send(searchField.text.trim())
        }
    }

    fun show() {
        mainFrame.isVisible = true
    }

    init {
        queries.consumeAsFlow()
            .onEach {
                searchButton.isEnabled = false
                resultArea.text = "Loading..."
            }.debounce(500)
            .map {
                repository.loadDefinition(it)
            }.map {
                it.joinToString("\n\n").ifEmpty { "Not found" }
            }.onEach {
                resultArea.text = it
                searchButton.isEnabled = true
            }.launchIn(scope)
    }
}

fun main() {
    Display.show()
}