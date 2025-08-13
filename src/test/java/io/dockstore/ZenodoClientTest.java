package io.dockstore;

import io.openapi.invenio.ApiClient;
import io.openapi.invenio.ApiException;
import io.openapi.invenio.api.RecordsApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ZenodoClientTest {

    private RecordsApi recordsApi;

    @BeforeEach
    void setup() {
        ApiClient client = new ApiClient();
        client.setBasePath("https://inveniordm.web.cern.ch");
        recordsApi = new RecordsApi(client);
    }

    @Test
    public void testRecordsSearch() throws ApiException {
        // uh oh, their openapi is incomplete and has no objects
        recordsApi.searchRecords(null, "bestmatch", 10, null, true);
    }

    @Test
    void testConceptDoi() {

    }
}