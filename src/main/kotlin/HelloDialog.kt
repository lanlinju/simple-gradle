package com.example

import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingUtilities

fun main() {
    // 确保 UI 在事件调度线程中创建
    SwingUtilities.invokeLater(Runnable {
        // 创建窗口
        val frame = JFrame("Hello Window")
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

        // 添加标签
        val label = JLabel("Hello, World!", JLabel.CENTER)
        frame.contentPane.add(label)

        // 设置大小并显示
        frame.setSize(300, 200)
        frame.setLocationRelativeTo(null) // 居中显示
        frame.isVisible = true
    })
}