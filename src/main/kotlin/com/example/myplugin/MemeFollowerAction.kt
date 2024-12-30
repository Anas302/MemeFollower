package com.example.myplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import MemeSwingApp

class MemeFollowerAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        // Create and show the meme follower GUI when the action is triggered
        MemeSwingApp().createAndShowGUI()
    }
}