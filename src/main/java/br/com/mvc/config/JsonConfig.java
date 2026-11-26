package br.com.mvc.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Configuracao unica do Gson usada pela API.
 *
 * O Gson nao consegue ler/escrever LocalDateTime sozinho no Java 17+
 * (os campos internos da classe sao inacessiveis), entao registramos um
 * adaptador que trabalha com texto no formato 2026-11-23T10:15:30.
 */
public final class JsonConfig {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .disableHtmlEscaping()
            .create();

    private JsonConfig() {
    }

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

        private static final DateTimeFormatter FORMATO =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        @Override
        public void write(JsonWriter out, LocalDateTime valor) throws IOException {
            if (valor == null) {
                out.nullValue();
            } else {
                out.value(valor.format(FORMATO));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDateTime.parse(in.nextString());
        }
    }
}
