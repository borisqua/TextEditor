package com.example.texteditor;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
class SearchResult {

    private int indexInSearchResults;
    private int caretPosition;
    private int size;

    public SearchResult() {
        this(-1, -1, -1);
    }

    public SearchResult(int indexInSearchResults, int caretPosition, int size) {
        this.indexInSearchResults = indexInSearchResults;
        this.caretPosition = caretPosition;
        this.size = size;
    }

}
