package com.example.texteditor;

import javax.swing.*;

public class ApplicationRunner {

    static void main() {

        System.out.println("basics.collections.Main thread name: " + Thread.currentThread().getName());

        SwingUtilities.invokeLater(TextEditor::new);

    }

}
