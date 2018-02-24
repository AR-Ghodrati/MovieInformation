package com.ar.movieinformation.OMDB_by_another;

import com.ar.movieinformation.OMDB_by_another.Model.DetailedListing;
import com.ar.movieinformation.OMDB_by_another.Model.SearchResponse;

import java.io.IOException;


/**
 * Created by Alireza Ghodrati on 20/02/2018.
 */

public class OmdbAPI {

        private Omdb omdb;


        public OmdbAPI(Omdb omdb) {
            this.omdb = omdb;
        }


        public SearchResponse search(Query query) throws IOException, OmdbException {
            if (query.getQueryMap().get("t").isEmpty()) {
                throw new OmdbException("Query must contain title parameter for search.");
            } else {
                query.getQueryMap().put("s", query.getQueryMap().get("t"));
                return omdb.search(query.getQueryMap()).execute().body();
            }
        }


        public DetailedListing getDetailedListing(Query query) throws IOException {
            if (query.getQueryMap().containsKey("s")) {
                query.getQueryMap().replace("s", "");
            }
            return omdb.getDetailedListing(query.getQueryMap()).execute().body();
        }
}
