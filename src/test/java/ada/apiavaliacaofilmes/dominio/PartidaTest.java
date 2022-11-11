package ada.apiavaliacaofilmes.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PartidaTest {

    @Test
    @DisplayName("getAcertou() deve retornar null partida não finalizada")
    void getAcertouNaoFinalizada() {
        assertNull(new Partida().getAcertou());
    }

    @Test
    @DisplayName("getAcertou() deve retornar o resultado correto")
    void getAcertouFinalizada() {
        var partida = new Partida();

        partida.setAposta(1);
        partida.setVencedor(1);
        assertTrue(partida.getAcertou());

        partida.setAposta(1);
        partida.setVencedor(2);
        assertFalse(partida.getAcertou());

        partida.setAposta(2);
        partida.setVencedor(1);
        assertFalse(partida.getAcertou());
    }
}