package com.example.texteditor;

import org.junit.jupiter.api.Test;

import javax.swing.JTextArea;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextSearchTaskTest {

    @Test
    void showSearchResultMovesCaretAndSelection() {
        JTextArea textArea = new JTextArea("hello world");

        TextSearchTask.showSearchResult(textArea, 6, 5);

        assertEquals(11, textArea.getCaretPosition());
        assertEquals(6, textArea.getSelectionStart());
        assertEquals(11, textArea.getSelectionEnd());
    }

    @Test
    void doInBackgroundFindsPlainMatches() throws Exception {
        JTextArea textArea = new JTextArea("abc x abc xx abc");
        List<SearchResult> existingResults = new ArrayList<>();
        SearchResult selected = new SearchResult();

        TextSearchTask task = new TextSearchTask(textArea, "abc", 0, existingResults, selected, false);
        suppressInterimPublish(task);

        List<SearchResult> results = task.doInBackground();

        assertEquals(3, results.size());
        assertEquals(0, results.get(0).getCaretPosition());
        assertEquals(6, results.get(1).getCaretPosition());
        assertEquals(13, results.get(2).getCaretPosition());
    }

    @Test
    void doInBackgroundFindsRegexMatches() throws Exception {
        JTextArea textArea = new JTextArea("a1 a2 a3");
        List<SearchResult> existingResults = new ArrayList<>();
        SearchResult selected = new SearchResult();

        TextSearchTask task = new TextSearchTask(textArea, "a\\d", 0, existingResults, selected, true);
        suppressInterimPublish(task);

        List<SearchResult> results = task.doInBackground();

        assertEquals(3, results.size());
        assertEquals(0, results.get(0).getCaretPosition());
        assertEquals(3, results.get(1).getCaretPosition());
        assertEquals(6, results.get(2).getCaretPosition());
    }

    private static void suppressInterimPublish(TextSearchTask task) throws Exception {
        Field interimPublished = TextSearchTask.class.getDeclaredField("interimPublished");
        interimPublished.setAccessible(true);
        interimPublished.setBoolean(task, true);
    }
}
