package ada.apiavaliacaofilmes.servico;

import ada.apiavaliacaofilmes.clientesapi.OmdbClienteApi;
import ada.apiavaliacaofilmes.dominio.AvaliacaoFilme;
import feign.codec.DecodeException;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class OmdbServiceTest {

    OmdbService service;

    OmdbClienteApi clienteApi;

    @BeforeEach
    void setup() throws IllegalAccessException {
        clienteApi = mock(OmdbClienteApi.class);

        service = new OmdbService(clienteApi);

        FieldUtils.writeDeclaredField(service, "apiKey", "minhachave", true);
    }

    @Test
    @DisplayName("getAvaliacaoFilmePorTitulo() deve retornar a Avaliacao que achou na API, caso encontre")
    void getAvaliacaoFilmePorTituloEncontrado() {
        String titulo = "teste";
        AvaliacaoFilme esperado = mock(AvaliacaoFilme.class);
        when(esperado.getId()).thenReturn("id-xyz");

        when(clienteApi.getFilmePorTitulo(anyString(), eq(titulo), anyString())).thenReturn(Optional.of(esperado));

        AvaliacaoFilme resultado = service.getAvaliacaoFilmePorTitulo(titulo);

        assertEquals(esperado, resultado);
    }

    @Test
    @DisplayName("getAvaliacaoFilmePorTitulo() deve lançar ResponseStatusException caso o JSON venha todo nulo da API")
    void getAvaliacaoFilmePorTituloResponseStatusExceptionCamposNulos() {
        String titulo = "teste";

        when(clienteApi.getFilmePorTitulo(anyString(), eq(titulo), anyString()))
                                        .thenReturn(Optional.of(new AvaliacaoFilme()));

        ResponseStatusException exception =
                        assertThrows(ResponseStatusException.class, () -> service.getAvaliacaoFilmePorTitulo(titulo));

        assertEquals(NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("getAvaliacaoFilmePorTitulo() deve lançar ResponseStatusException caso a API retorne corpo inválido")
    void getAvaliacaoFilmePorTituloResponseStatusExceptionCorpoInvalido() {
        String titulo = "teste";

        doThrow(mock(DecodeException.class)).when(clienteApi).getFilmePorTitulo(anyString(), eq(titulo), anyString());

        ResponseStatusException exception =
                        assertThrows(ResponseStatusException.class, () -> service.getAvaliacaoFilmePorTitulo(titulo));

        assertEquals(NOT_FOUND, exception.getStatus());
    }
    
    @Test
    @DisplayName("getAvaliacaoFilmePorId() deve retornar a Avaliacao que achou na API, caso encontre")
    void getAvaliacaoFilmePorIdEncontrado() {
        String id = "teste";
        AvaliacaoFilme esperado = mock(AvaliacaoFilme.class);
        when(esperado.getId()).thenReturn("id-xyz");

        when(clienteApi.getFilmePorId(anyString(), eq(id), anyString(), anyString())).thenReturn(Optional.of(esperado));

        AvaliacaoFilme resultado = service.getAvaliacaoFilmePorId(id);

        assertEquals(esperado, resultado);
    }

    @Test
    @DisplayName("getAvaliacaoFilmePorId() deve lançar ResponseStatusException caso o JSON venha todo nulo da API")
    void getAvaliacaoFilmePorIdResponseStatusExceptionCamposNulos() {
        String id = "teste";

        when(clienteApi.getFilmePorId(anyString(), eq(id), anyString(), anyString()))
                                        .thenReturn(Optional.of(new AvaliacaoFilme()));

        ResponseStatusException exception =
                        assertThrows(ResponseStatusException.class, () -> service.getAvaliacaoFilmePorId(id));

        assertEquals(NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("getAvaliacaoFilmePorId() deve lançar ResponseStatusException caso a API retorne corpo inválido")
    void getAvaliacaoFilmePorIdResponseStatusExceptionCorpoInvalido() {
        String id = "teste";

        doThrow(mock(DecodeException.class)).when(clienteApi)
                                            .getFilmePorId(anyString(), eq(id), anyString(), anyString());

        ResponseStatusException exception =
                        assertThrows(ResponseStatusException.class, () -> service.getAvaliacaoFilmePorId(id));

        assertEquals(NOT_FOUND, exception.getStatus());
    }


    @Test
    @DisplayName("filmeNaoEncontradoException() deve retornar status 404 e a mensagem com o id ou título do argumento")
    void filmeNaoEncontradoException() {
        ResponseStatusException exception = service.filmeNaoEncontradoException("id-teste");
        assertEquals(NOT_FOUND, exception.getStatus());
        assertEquals("Filme 'id-teste' não encontrado", exception.getReason());
    }
}