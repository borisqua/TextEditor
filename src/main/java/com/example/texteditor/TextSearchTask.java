package com.example.texteditor;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TextSearchTask extends SwingWorker<List<SearchResult>, SearchResult> {

    private static final Logger LOGGER = LogManager.getLogger(TextSearchTask.class);

    private final JTextArea textArea;
    private final String sourceText;
    private final String target;
    private final int externalStartIndex;
    private final Matcher matcher;
    private final boolean useRegExp;
    private final List<SearchResult> searchResults;
    private final SearchResult selectedSearchResult;

    private final int targetSize;
    private boolean interimPublished = false;

    public TextSearchTask(
            JTextArea textArea, String target, int externalStartIndex,
            List<SearchResult> searchResults, SearchResult selectedSearchResult,
            boolean useRegExp) {

        super();

        this.textArea = textArea;
        this.sourceText = textArea.getText();
        this.target = target;
        this.targetSize = target.length();
        this.externalStartIndex = externalStartIndex;
        this.useRegExp = useRegExp;
        this.searchResults = searchResults;
        this.matcher = Pattern.compile(target).matcher(sourceText);
        this.selectedSearchResult = selectedSearchResult;

        LOGGER.debug("SwingWorker created in thread name: {}", Thread.currentThread().getName());

    }

    /**
     * Set the caret of the textArea in the position right after a target string found, and select the target string
     *
     * @param textArea             - The JTextArea component where to search had done
     * @param targetPositionInText - the position of the target string in the text
     * @param targetSize           - target string size
     */
    public static void showSearchResult(JTextArea textArea, int targetPositionInText, int targetSize) {
        textArea.setCaretPosition(targetPositionInText + targetSize);
        textArea.select(targetPositionInText, targetPositionInText + targetSize);
        textArea.grabFocus();
    }

    private int search(List<SearchResult> searchResults, Integer nextStartIndex, int size) {//find next
        SearchResult searchResult = new SearchResult(searchResults.size(), nextStartIndex, size);
        if (!interimPublished && nextStartIndex > externalStartIndex) { //publish the only first occurrence after the current caret position
            //don't wait until all occurrences will found
            publish(searchResult); // publish in EventQueue the first occurrence found after the current caret position
            interimPublished = true; // publish only the first occurrence
        }
        searchResults.add(searchResult);
        return nextStartIndex + size;
    }

    @Override
    protected List<SearchResult> doInBackground() {

        LOGGER.debug("SwingWorker background work thread name: {}", Thread.currentThread().getName());

        int startIndex = 0;
        List<SearchResult> searchResults = new ArrayList<>();
        if (useRegExp) {
            while (matcher.find(startIndex)) {
                startIndex = search(searchResults, matcher.start(), matcher.group(0).length());
            }
        } else {
            while ((startIndex = sourceText.indexOf(target, startIndex)) != -1) {
                startIndex = search(searchResults, startIndex, targetSize);
            }
        }

        if (!interimPublished && !searchResults.isEmpty()) {
            //Publish in EventQueue the first occurrence from the beginning
            // if it hadn't found after the current caret position yet.
            publish(searchResults.getFirst());
        }

        return searchResults;

    }

    @Override
    protected void process(List<SearchResult> searchResults) { //process the interim results in the event dispatch thread
        SearchResult lastPublished = searchResults.getLast();
        selectedSearchResult.setIndexInSearchResults(lastPublished.getIndexInSearchResults());
        selectedSearchResult.setCaretPosition(lastPublished.getCaretPosition());
        selectedSearchResult.setSize(lastPublished.getSize());
        showSearchResult(textArea, selectedSearchResult.getCaretPosition(), selectedSearchResult.getSize());
    }

    @Override
    protected void done() { //process the final results of the basics.swing worker background work
        try {
            searchResults.clear();//reset the previous search results
            List<SearchResult> freshSearchResults = get();
            if (!freshSearchResults.isEmpty()) {
                searchResults.addAll(freshSearchResults);//set the new search results if they are there
            }
        } catch (InterruptedException ignored) {
        } catch (ExecutionException e) {
            LOGGER.error("Error during search", e);
        }
    }

}
