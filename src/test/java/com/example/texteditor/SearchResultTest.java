package com.example.texteditor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchResultTest {

    @Test
    void defaultConstructorInitializesToMinusOne() {
        SearchResult result = new SearchResult();

        assertEquals(-1, result.getIndexInSearchResults());
        assertEquals(-1, result.getCaretPosition());
        assertEquals(-1, result.getSize());
    }

    @Test
    void settersUpdateFields() {
        SearchResult result = new SearchResult(1, 2, 3);

        result.setIndexInSearchResults(4);
        result.setCaretPosition(5);
        result.setSize(6);

        assertEquals(4, result.getIndexInSearchResults());
        assertEquals(5, result.getCaretPosition());
        assertEquals(6, result.getSize());
    }
}
