package com.ar.movieinformation;

import com.ar.movieinformation.OMDB_by_another.Model.DetailedListing;
import com.ar.movieinformation.OMDB_by_another.Model.SearchResponse;
import com.ar.movieinformation.OMDB_by_another.OmdbAPI;
import com.ar.movieinformation.OMDB_by_another.OmdbAPIFactory;
import com.ar.movieinformation.OMDB_by_another.OmdbException;
import com.ar.movieinformation.OMDB_by_another.Query;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alireza Ghodrati on 20/02/2018.
 */

public class APITEST {
    private final static String title = "Fight Club";
    private final static String imdbID = "tt0137523";
    private OmdbAPI omdbAPI;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        omdbAPI = OmdbAPIFactory.getOmdbAPI();
    }

    @Test
    public void searchWithTitle() throws Exception {
        Query query = new Query.Builder(title).build();
        SearchResponse searchResponse = omdbAPI.search(query);

        assertTrue(Boolean.valueOf(searchResponse.getResponse()));
        assertTrue(Integer.valueOf(searchResponse.getTotalResults()) > 0);
    }

    @Test
    public void searchWithoutTitle() throws Exception {
        Query query = new Query.Builder(imdbID).build();
        exception.expect(OmdbException.class);
        exception.expectMessage("Query must contain title parameter for search.");
        omdbAPI.search(query);
    }

    @Test
    public void getDetailedListingWithTitle() throws Exception {
        Query query = new Query.Builder(title).build();
        DetailedListing movie = omdbAPI.getDetailedListing(query);

        assertEquals(title, movie.getTitle());
    }

    @Test
    public void getDetailedListingWithID() throws Exception {
        Query query = new Query.Builder(imdbID).build();
        DetailedListing movie = omdbAPI.getDetailedListing(query);

        assertEquals(imdbID, movie.getImdbID());
    }
}
