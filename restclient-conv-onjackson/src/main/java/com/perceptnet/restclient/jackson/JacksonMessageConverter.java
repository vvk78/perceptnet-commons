package com.perceptnet.restclient.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perceptnet.restclient.MessageConverter;

import java.io.IOException;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 25.12.2017
 */
public class JacksonMessageConverter implements MessageConverter {

    private ObjectMapper mapper;

    public JacksonMessageConverter() {
        mapper = new ObjectMapper();
        //mapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
    }

    @Override
    public <T> T parse(Class<T> expectedType, String str) {
        try {
            return (T) mapper.reader(expectedType).readValue(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String format(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
