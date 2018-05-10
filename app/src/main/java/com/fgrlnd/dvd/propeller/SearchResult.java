package com.fgrlnd.dvd.propeller;

/**
 * Created by david on 3/25/18.
 */

public class SearchResult {
    public App app;
    public double editDistance;

    public SearchResult(App app, double editDistance) {
        this.app = app;
        this.editDistance = editDistance;
    }
}
