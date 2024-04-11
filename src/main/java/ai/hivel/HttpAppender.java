package ai.hivel;

import java.io.IOException;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import lombok.Setter;


@Setter
public class HttpAppender extends AppenderBase<ILoggingEvent> {

    private String url;
    private String key;
    private ObjectMapper objectMapper = null;
    private OkHttpClient client = null;

    @Override
    protected void append(ILoggingEvent eventObject) {
        try {
            sendLog(eventObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendLog(ILoggingEvent event) throws IOException {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        if (objectMapper == null)
            objectMapper = new ObjectMapper();
        if (client == null)
            client = new OkHttpClient();
        String jsonBody = objectMapper.writeValueAsString(event);

        RequestBody body = RequestBody.create(mediaType, jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("DD-API-KEY", key)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
