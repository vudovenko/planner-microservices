package ru.vudovenko.micro.planner.todo.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;

@Component
public class FeignExceptionHandler implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {

        return switch (response.status()) {
            case 406 -> new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, readMessage(response));
            default -> null;
        };

    }

    private String readMessage(Response response) {

        StringWriter writer = new StringWriter();

        try (Reader reader = response.body().asReader(Charset.defaultCharset())) {
            reader.transferTo(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }
}
