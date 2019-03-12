package com.longfocus.webcorepresence;

import com.google.android.gms.common.util.IOUtils;
import com.longfocus.webcorepresence.dashboard.client.Instance;
import com.longfocus.webcorepresence.dashboard.client.Load;
import com.longfocus.webcorepresence.dashboard.client.Settings;
import com.longfocus.webcorepresence.dashboard.js.Meta;
import com.longfocus.webcorepresence.dashboard.js.Place;
import com.longfocus.webcorepresence.dashboard.js.Register;
import com.longfocus.webcorepresence.dashboard.js.StatusRequest;
import com.longfocus.webcorepresence.dashboard.js.Update;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ParseUtilsTest {

    private InputStream getResource(final String name) {
        return ClassLoader.getSystemClassLoader().getResourceAsStream(name);
    }

    private String getJson(final String fileName) {
        final InputStream stream = getResource(fileName);

        try {
            return new String(IOUtils.toByteArray(stream), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    class TestParse {

        private String str;
        private Object obj;
        private Object[] arr;
    }

    /**
     * Tests {@link ParseUtils#fromJson(String, Class)} for {@code null} json.
     */
    @Test
    public void fromJson_json_null() {
        final TestParse testParse = ParseUtils.fromJson(null, TestParse.class);
        assertNull(testParse);
    }

    /**
     * Tests {@link ParseUtils#fromJson(String, Class)} for empty json.
     */
    @Test
    public void fromJson_json_empty() {
        final TestParse testParse = ParseUtils.fromJson("", TestParse.class);
        assertNull(testParse);
    }

    /**
     * Tests {@link ParseUtils#toJson(Object)} for {@code null} source.
     */
    @Test
    public void toJson_null() {
        final String json = ParseUtils.toJson(null);
        assertNotNull(json);
        assertEquals("null", json);
    }

    /**
     * Tests {@link ParseUtils#toJson(Object)} for empty source.
     */
    @Test
    public void toJson_empty() {
        final String json = ParseUtils.toJson("");
        assertNotNull(json);
        assertEquals("\"\"", json);
    }

    /**
     * Tests {@link ParseUtils#fromJson(String, Class)} for the dashboard client {@link Load}.
     */
    @Test
    public void fromJson_dashboard_load() {
        final Load load = ParseUtils.fromJson(getJson("dashboard/load.json"), Load.class);
        assertNotNull(load);
        assertEquals("Home \\ webCoRE", load.getName());
        assertEquals(1551749743378L, load.getNow());

        final Instance instance = load.getInstance();
        assertNotNull(instance);
        assertEquals(":c31287b90c5f4012b23bd2fc1d16d96c:", instance.getId());
        assertEquals(":dd770930cab74bda90c856add89c74c4:", instance.getLocationId());
        assertEquals("webCoRE", instance.getName());
        assertEquals("https://graph-na04-useast2.api.smartthings.com:443/api/token/cf851b5c-7be3-4fd6-b566-b80e02b9cd6e/smartapps/installations/1df6386d-a720-43a8-a5e0-ce42fae3d912/", instance.getUri());
        assertEquals("1551652687521", instance.getDeviceVersion());
        assertEquals("v0.3.10a.20190223", instance.getCoreVersion());
        assertEquals(true, instance.isEnabled());
        assertEquals("8cd1473a-6627-4422-9f00-e7d7453bf540", instance.getToken());

        final Settings settings = instance.getSettings();
        assertNotNull(settings);

        final Place[] places = settings.getPlaces();
        assertNotNull(places);
        assertEquals(2, places.length);

        final Place placeRushmore = places[0];
        assertEquals(":dcb3ae5326484c3495af3b5a758a14f5:", placeRushmore.getId());
        assertEquals("Rushmore", placeRushmore.getN());
        assertEquals(true, placeRushmore.isH());
        assertEquals(122.51291010685114, placeRushmore.getI(), 0);
        assertEquals(412.01632938099965, placeRushmore.getO(), 0);
        assertEquals(43.87910575938632, placeRushmore.getP()[0], 0);
        assertEquals(-103.45906856562243, placeRushmore.getP()[1], 0);

        final Meta metaRushmore = placeRushmore.getMeta();
        assertEquals(5.4886627, metaRushmore.getD(), 0);
        assertEquals(true, metaRushmore.isP());

        final Place placeLasVegas = places[1];
        assertEquals(":80c38a04cb1242d3b65374dbedd587b3:", placeLasVegas.getId());
        assertEquals("Las Vegas", placeLasVegas.getN());
        assertEquals(false, placeLasVegas.isH());
        assertEquals(54.672307981457244, placeLasVegas.getI(), 0);
        assertEquals(411.19042501108135, placeLasVegas.getO(), 0);
        assertEquals(36.169941182266306, placeLasVegas.getP()[0], 0);
        assertEquals(-115.13983282128906, placeLasVegas.getP()[1], 0);

        final Meta metaLasVegas = placeLasVegas.getMeta();
        assertEquals(2889.4446, metaLasVegas.getD(), 0);
        assertEquals(false, metaLasVegas.isP());
    }

    /**
     * Tests {@link ParseUtils#toJson(Object)} for the dashboard client {@link Load}.
     */
    @Test
    public void toJson_dashboard_load() throws JSONException {
        final Meta metaRushmore = new Meta();
        metaRushmore.setD(5.4886627);
        metaRushmore.setP(true);

        final Place placeRushmore = new Place();
        placeRushmore.setId(":dcb3ae5326484c3495af3b5a758a14f5:");
        placeRushmore.setN("Rushmore");
        placeRushmore.setH(true);
        placeRushmore.setI(122.51291010685114);
        placeRushmore.setO(412.01632938099965);
        placeRushmore.setP(new double[] { 43.87910575938632, -103.45906856562243 });
        placeRushmore.setMeta(metaRushmore);

        final Meta metaLasVegas = new Meta();
        metaLasVegas.setD(2889.4446);
        metaLasVegas.setP(false);

        final Place placeLasVegas = new Place();
        placeLasVegas.setId(":80c38a04cb1242d3b65374dbedd587b3:");
        placeLasVegas.setN("Las Vegas");
        placeLasVegas.setH(false);
        placeLasVegas.setI(54.672307981457244);
        placeLasVegas.setO(411.19042501108135);
        placeLasVegas.setP(new double[] { 36.169941182266306, -115.13983282128906 });
        placeLasVegas.setMeta(metaLasVegas);

        final Settings settings = new Settings();
        settings.setPlaces(new Place[] { placeRushmore, placeLasVegas });

        final Instance instance = new Instance();
        instance.setId(":c31287b90c5f4012b23bd2fc1d16d96c:");
        instance.setLocationId(":dd770930cab74bda90c856add89c74c4:");
        instance.setName("webCoRE");
        instance.setUri("https://graph-na04-useast2.api.smartthings.com:443/api/token/cf851b5c-7be3-4fd6-b566-b80e02b9cd6e/smartapps/installations/1df6386d-a720-43a8-a5e0-ce42fae3d912/");
        instance.setDeviceVersion("1551652687521");
        instance.setCoreVersion("v0.3.10a.20190223");
        instance.setEnabled(true);
        instance.setToken("8cd1473a-6627-4422-9f00-e7d7453bf540");
        instance.setSettings(settings);

        final Load load = new Load();
        load.setName("Home \\ webCoRE");
        load.setNow(1551749743378L);
        load.setInstance(instance);

        final String json = ParseUtils.toJson(load);
        JSONAssert.assertEquals(getJson("dashboard/load.json"), json, true);
    }

    /**
     * Tests {@link ParseUtils#fromJson(String, Class)} for the dashboard interface {@link StatusRequest}.
     */
    @Test
    public void fromJson_dashboard_status() {
        final StatusRequest statusRequest = ParseUtils.fromJson(getJson("dashboard/status.json"), StatusRequest.class);
        assertNotNull(statusRequest);
        assertEquals(":69b86b285d55404aabc8f13f15776c8a:", statusRequest.getI());

    }

    /**
     * Tests {@link ParseUtils#toJson(Object)} for the dashboard interface {@link StatusRequest}.
     */
    @Test
    public void toJson_dashboard_status() throws JSONException {
        final StatusRequest statusRequest = new StatusRequest();
        statusRequest.setI(":69b86b285d55404aabc8f13f15776c8a:");

        final String json = ParseUtils.toJson(statusRequest);
        JSONAssert.assertEquals(getJson("dashboard/status.json"), json, true);
    }

    /**
     * Tests {@link ParseUtils#fromJson(String, Class)} for the dashboard interface {@link Register}.
     */
    @Test
    public void fromJson_dashboard_register() {
        final Register register = ParseUtils.fromJson(getJson("dashboard/register.json"), Register.class);
        assertNotNull(register);
        assertEquals("https://graph-na04-useast2.api.smartthings.com/api/token/cf851b5c-7be3-4fd6-b566-b80e02b9cd6e/smartapps/installations/1df6386d-a720-43a8-a5e0-ce42fae3d912/", register.getE());
        assertEquals(":69b86b285d55404aabc8f13f15776c8a:", register.getI());
        assertEquals(":25744d2cea854037bfe0b0f7e3f31d3d:", register.getD());
    }

    /**
     * Tests {@link ParseUtils#toJson(Object)} for the dashboard interface {@link Register}.
     */
    @Test
    public void toJson_dashboard_register() throws JSONException {
        final Register register = new Register();
        register.setE("https://graph-na04-useast2.api.smartthings.com/api/token/cf851b5c-7be3-4fd6-b566-b80e02b9cd6e/smartapps/installations/1df6386d-a720-43a8-a5e0-ce42fae3d912/");
        register.setI(":69b86b285d55404aabc8f13f15776c8a:");
        register.setD(":25744d2cea854037bfe0b0f7e3f31d3d:");

        final String json = ParseUtils.toJson(register);
        JSONAssert.assertEquals(getJson("dashboard/register.json"), json, true);
    }

    /**
     * Tests {@link ParseUtils#fromJson(String, Class)} for the dashboard interface {@link Update}.
     */
    @Test
    public void fromJson_dashboard_update() {
        final Update update = ParseUtils.fromJson(getJson("dashboard/update.json"), Update.class);
        assertNotNull(update);
        assertEquals(":69b86b285d55404aabc8f13f15776c8a:", update.getI());

        final Place[] places = update.getP();
        assertNotNull(places);
        assertEquals(2, places.length);

        final Place placeRushmore = places[0];
        assertEquals(":dcb3ae5326484c3495af3b5a758a14f5:", placeRushmore.getId());
        assertEquals("Rushmore", placeRushmore.getN());
        assertEquals(true, placeRushmore.isH());
        assertEquals(122.51291010685114, placeRushmore.getI(), 0);
        assertEquals(412.01632938099965, placeRushmore.getO(), 0);
        assertEquals(43.87910575938632, placeRushmore.getP()[0], 0);
        assertEquals(-103.45906856562243, placeRushmore.getP()[1], 0);

        final Meta metaRushmore = placeRushmore.getMeta();
        assertEquals(5.4886627, metaRushmore.getD(), 0);
        assertEquals(true, metaRushmore.isP());

        final Place placeLasVegas = places[1];
        assertEquals(":80c38a04cb1242d3b65374dbedd587b3:", placeLasVegas.getId());
        assertEquals("Las Vegas", placeLasVegas.getN());
        assertEquals(false, placeLasVegas.isH());
        assertEquals(54.672307981457244, placeLasVegas.getI(), 0);
        assertEquals(411.19042501108135, placeLasVegas.getO(), 0);
        assertEquals(36.169941182266306, placeLasVegas.getP()[0], 0);
        assertEquals(-115.13983282128906, placeLasVegas.getP()[1], 0);

        final Meta metaLasVegas = placeLasVegas.getMeta();
        assertEquals(2889.4446, metaLasVegas.getD(), 0);
        assertEquals(false, metaLasVegas.isP());
    }

    /**
     * Tests {@link ParseUtils#toJson(Object)} for the dashboard interface {@link Update}.
     */
    @Test
    public void toJson_dashboard_update() throws JSONException {
        final Meta metaRushmore = new Meta();
        metaRushmore.setD(5.4886627);
        metaRushmore.setP(true);

        final Place placeRushmore = new Place();
        placeRushmore.setId(":dcb3ae5326484c3495af3b5a758a14f5:");
        placeRushmore.setN("Rushmore");
        placeRushmore.setH(true);
        placeRushmore.setI(122.51291010685114);
        placeRushmore.setO(412.01632938099965);
        placeRushmore.setP(new double[] { 43.87910575938632, -103.45906856562243 });
        placeRushmore.setMeta(metaRushmore);

        final Meta metaLasVegas = new Meta();
        metaLasVegas.setD(2889.4446);
        metaLasVegas.setP(false);

        final Place placeLasVegas = new Place();
        placeLasVegas.setId(":80c38a04cb1242d3b65374dbedd587b3:");
        placeLasVegas.setN("Las Vegas");
        placeLasVegas.setH(false);
        placeLasVegas.setI(54.672307981457244);
        placeLasVegas.setO(411.19042501108135);
        placeLasVegas.setP(new double[] { 36.169941182266306, -115.13983282128906 });
        placeLasVegas.setMeta(metaLasVegas);

        final Update update = new Update();
        update.setI(":69b86b285d55404aabc8f13f15776c8a:");
        update.setP(new Place[] { placeRushmore, placeLasVegas });

        final String json = ParseUtils.toJson(update);
        JSONAssert.assertEquals(getJson("dashboard/update.json"), json, true);
    }

    /**
     * Tests {@link ParseUtils#jsonCallback(String, String)}.
     */
    @Test
    public void jsonCallback() throws JSONException {
        final String json = ParseUtils.jsonCallback("longfocus", "longfocus({ \"name\": \"webCoRE Presence\" })");
        JSONAssert.assertEquals("{ \"name\": \"webCoRE Presence\" }", json, true);
    }

    /**
     * Tests {@link ParseUtils#jsonCallback(String, String)} for {@code null} callback.
     */
    @Test(expected = NullPointerException.class)
    public void jsonCallback_callback_null() throws JSONException {
        ParseUtils.jsonCallback(null, "longfocus({ \"name\": \"webCoRE Presence\" })");
    }

    /**
     * Tests {@link ParseUtils#jsonCallback(String, String)} for empty callback.
     */
    @Test
    public void jsonCallback_callback_empty() throws JSONException {
        final String json = ParseUtils.jsonCallback("", "longfocus({ \"name\": \"webCoRE Presence\" })");
        assertEquals("longfocus({ \"name\": \"webCoRE Presence\" })", json);
    }

    /**
     * Tests {@link ParseUtils#jsonCallback(String, String)} for {@code null} response.
     */
    @Test(expected = NullPointerException.class)
    public void jsonCallback_response_null() throws JSONException {
        ParseUtils.jsonCallback("longfocus", null);
    }

    /**
     * Tests {@link ParseUtils#jsonCallback(String, String)} for empty response.
     */
    @Test
    public void jsonCallback_response_empty() throws JSONException {
        final String json = ParseUtils.jsonCallback("longfocus", "");
        JSONAssert.assertEquals("", json, true);
    }

    /**
     * Tests {@link ParseUtils#getData(Response)}.
     */
    @Test
    public void getData() throws IOException, JSONException {
        final Response response = new Response.Builder()
                .request(new Request.Builder()
                        .url("https://longfocus.com")
                        .build())
                .protocol(Protocol.HTTP_2)
                .body(new ResponseBody() {

                    @Override
                    public MediaType contentType() {
                        return MediaType.parse("application/json");
                    }

                    @Override
                    public long contentLength() {
                        return 0;
                    }

                    @Override
                    public BufferedSource source() {
                        final Source source = Okio.source(getResource("dashboard/load.json"));
                        return Okio.buffer(source);
                    }
                })
                .code(200)
                .message("OK")
                .build();

        final String json = ParseUtils.getData(response);
        JSONAssert.assertEquals(getJson("dashboard/load.json"), json, true);
    }

    /**
     * Tests {@link ParseUtils#getData(Response)} for {@code null} response.
     */
    @Test(expected = NullPointerException.class)
    public void getData_null() throws IOException {
        ParseUtils.getData(null);
    }
}
