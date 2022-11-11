package ada.apiavaliacaofilmes.requisicao;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class IniciarPartidaRequestTest {

    @Test
    @DisplayName("validar() deveria lançar ResponseStatusException caso o tituloFilme1 seja o mesmo que tituloFilme2")
    void validarExcecao() throws IllegalAccessException {
        String filmeTeste = "filme teste";
        IniciarPartidaRequest request = new IniciarPartidaRequest();
        FieldUtils.writeDeclaredField(request, "tituloFilme1", filmeTeste, true);
        FieldUtils.writeDeclaredField(request, "tituloFilme2", filmeTeste, true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, request::validar);

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(
                "Não é possível criar uma partida usando o mesmo título ('filme teste') para os 2 filmes",
                exception.getReason()
        );
    }

    @Test
    @DisplayName("validar() NÃO deveria lançar ResponseStatusException caso o tituloFilme1 != tituloFilme2")
    void validarSemExcecao() throws IllegalAccessException {

        IniciarPartidaRequest request = new IniciarPartidaRequest();
        FieldUtils.writeDeclaredField(request, "tituloFilme1", "filme A", true);
        FieldUtils.writeDeclaredField(request, "tituloFilme2", "filme B", true);

        assertDoesNotThrow(request::validar);
    }
}