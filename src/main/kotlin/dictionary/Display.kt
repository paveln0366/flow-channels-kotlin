package org.example.dictionary

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*

object Display {

    private lateinit var queries: Flow<String>

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val repository = Repository
    private var loadingJob: Job? = null

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

    }

    fun show() {
        mainFrame.isVisible = true
    }

    init {
        queries
            .onEach {
                searchButton.isEnabled = false
                resultArea.text = "Loading..."
            }.map {
                repository.loadDefinition(it)
            }
            .map {
                it.joinToString("\n\n").ifEmpty { "Not found" }
            }
            .onEach {
                resultArea.text = it
                searchButton.isEnabled = true
            }.launchIn(scope)
    }
}

fun main() {
    Display.show()
}