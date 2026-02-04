package com.example.texteditor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TextEditorResourcesTest {

    @Test
    void iconResourcesExistOnClasspath() {
        ClassLoader classLoader = TextEditor.class.getClassLoader();
        List<String> icons = List.of(
                "openfile.png",
                "savefile.png",
                "search.png",
                "prevmatch.png",
                "nextmatch.png"
        );

        for (String icon : icons) {
            assertNotNull(classLoader.getResource("pics/icons/" + icon), "Missing icon: " + icon);
        }
    }
}
