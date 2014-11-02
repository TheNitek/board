package de.naeveke.board.sse;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

public class SSEMessageConverter extends AbstractHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static final MediaType SSE_MEDIATYPE = new MediaType("text", "event-stream", DEFAULT_CHARSET);

    private ObjectMapper objectMapper = new ObjectMapper();

    public SSEMessageConverter() {
        super(SSE_MEDIATYPE);
    }

    @Override
    public boolean canRead(Class<?> type1, MediaType mt) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return SSE_MEDIATYPE.includes(mediaType);
    }

    @Override
    protected boolean supports(Class<?> type) {
        // Should never be called
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeInternal(Object object, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        /*HttpHeaders headers = outputMessage.getHeaders();
         headers.setContentType(EVENT_STREAM_MEDIATYPE);*/

        /*StringBuilder sb = new StringBuilder(32);
         if (t instanceof SSEvent) {
         SSEvent event = (SSEvent) t;
         if (StringUtils.hasText(event.getComment())) {
         sb.append(":").append(event.getComment()).append("n");
         }
         if (StringUtils.hasText(event.getId())) {
         sb.append("id:").append(event.getId()).append("n");
         }
         if (StringUtils.hasText(event.getEvent())) {
         sb.append("event").append(event.getEvent()).append("n");
         }
         if (StringUtils.hasText(event.getData())) {
         sb.append("data:").append(event.getData()).append("n");
         }
         if (event.getRetry() != null) {
         sb.append("retry:").append(event.getRetry()).append("n");
         }
         } else {
         sb.append("data:").append(t).append("n");
         }*/
        JsonGenerator jsonGenerator
                = this.objectMapper.getFactory().createGenerator(outputMessage.getBody(), JsonEncoding.UTF8);

        try {
            jsonGenerator.writeRaw("data:");
            this.objectMapper.writeValue(jsonGenerator, object);
            jsonGenerator.writeRaw("\n\n");
        } catch (JsonProcessingException e) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + e.getMessage(), e);
        }

    }

    @Override
    protected Object readInternal(Class<? extends Object> type, HttpInputMessage him) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
