package io.dockstore;

public class ZenodoClientTest {

//    private ApiClient client;
//    private PreviewApi previewApi;
//
//    @BeforeEach
//    void setup() {
//        client = new ApiClient();
//        client.setBasePath("https://sandbox.zenodo.org/api");
//        previewApi = new io.swagger.zenodo.client.api.PreviewApi(client);
//    }
//
//    @Test
//    @Disabled("Test disabled as the communities API is failing on sandbox")
//    public void testZenodoClient() throws ApiException {
//        HashMap<String, Object> o = (HashMap<String, Object>)previewApi.listCommunities();
//        assertTrue(o != null && !o.keySet().isEmpty(), "not able to list communities as basic test");
//    }
//
//    @Test
//    void testConceptDoi() {
//        final SearchResult searchResult = previewApi.listRecords(null, "bestmatch", 1, 100);
//        assertNotNull(searchResult.getHits().getHits().get(0).getConceptdoi());
//        assertNotNull(searchResult.getHits().getHits().get(0).getCreated());
//        assertNotNull(searchResult.getHits().getHits().get(0).getModified());
//    }
}