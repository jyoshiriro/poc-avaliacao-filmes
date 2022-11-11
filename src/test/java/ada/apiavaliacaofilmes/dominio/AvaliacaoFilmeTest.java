package ada.apiavaliacaofilmes.dominio;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AvaliacaoFilmeTest {

    @Test
    @DisplayName("getVotos() deve retornar 0 caso votos seja null")
    void getVotosNullVotos() {
        assertEquals(0, new AvaliacaoFilme().getVotos());
    }

    @Test
    @DisplayName("getVotos() deve retornar 0 caso votos não seja um número válido")
    void getVotos0VotosInvalidos() throws IllegalAccessException {
        var avaliacao = new AvaliacaoFilme();
        FieldUtils.writeDeclaredField(avaliacao, "votos", "N/A", true);
        assertEquals(0, avaliacao.getVotos());
    }

    @Test
    @DisplayName("getVotos() deve retornar o número correto caso votos seja um número válido")
    void getVotosValidos() throws IllegalAccessException {
        var avaliacao = new AvaliacaoFilme();

        FieldUtils.writeDeclaredField(avaliacao, "votos", "16", true);
        assertEquals(16, avaliacao.getVotos());

        FieldUtils.writeDeclaredField(avaliacao, "votos", "123,456,789", true);
        assertEquals(123_456_789, avaliacao.getVotos());
    }

    @Test
    @DisplayName("getPontuacao() deve retornar 0 caso nota seja null")
    void getPontuacaoNull() {
        assertEquals(0.0, new AvaliacaoFilme().getPontuacao());
    }

    @Test
    @DisplayName("getPontuacao() deve retornar o número correto caso nota não seja null")
    void getPontuacaoNaoNull() throws IllegalAccessException {
        var avaliacao = new AvaliacaoFilme();

        FieldUtils.writeDeclaredField(avaliacao, "votos", "10", true);

        FieldUtils.writeDeclaredField(avaliacao, "nota", 3.0, true);
        assertEquals(30.0, avaliacao.getPontuacao());

        FieldUtils.writeDeclaredField(avaliacao, "nota", 4.54, true);
        assertEquals(45.4, avaliacao.getPontuacao());

        FieldUtils.writeDeclaredField(avaliacao, "nota", 8.122, true);
        assertEquals(81.22, avaliacao.getPontuacao());
    }

}