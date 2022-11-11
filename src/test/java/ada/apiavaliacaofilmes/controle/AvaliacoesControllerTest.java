package ada.apiavaliacaofilmes.controle;

import ada.apiavaliacaofilmes.dominio.Partida;
import ada.apiavaliacaofilmes.requisicao.IniciarPartidaRequest;
import ada.apiavaliacaofilmes.resposta.PontuacaoJogadorReponse;
import ada.apiavaliacaofilmes.servico.PartidaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class AvaliacoesControllerTest {

    AvaliacoesController controller;

    PartidaService service;

    @BeforeEach
    void setup() {
        service = mock(PartidaService.class);
        controller = new AvaliacoesController(service);
    }

    @Test
    @DisplayName("iniciar() deve setar o jogador da autenticação no DTO de requisição")
    void iniciarJogadorNaRequisicao() {
        IniciarPartidaRequest request = new IniciarPartidaRequest();
        Authentication authentication = mock(Authentication.class);
        String jogadorTeste = "jteste";

        when(authentication.getName()).thenReturn(jogadorTeste);

        ResponseEntity<Partida> resposta = controller.iniciar(request, authentication);

        assertEquals(jogadorTeste, request.getJogador());
        verify(service, times(1)).iniciarPartida(request);
    }

    @Test
    @DisplayName("iniciar() deve retornar status 201 e com a partida recém criada")
    void iniciarStatusECorpoCorretos() {
        IniciarPartidaRequest request = mock(IniciarPartidaRequest.class);

        Partida novaPartida = new Partida();
        novaPartida.setId(100);
        when(service.iniciarPartida(request)).thenReturn(novaPartida);

        ResponseEntity<Partida> resposta = controller.iniciar(request, mock(Authentication.class));

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(novaPartida, resposta.getBody());
    }

    @Test
    @DisplayName("jogar() deve deve enviar os dados corretos para a service")
    void jogarEnviarDadosCorretos() {
        int idPartida = 100;
        int filmeVencedor = 2;
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("jteste");

        controller.jogar(idPartida, filmeVencedor, authentication);

        verify(service, times(1)).jogarPartida(authentication.getName(), idPartida, filmeVencedor);
    }

    @Test
    @DisplayName("jogar() deve deve retornar status 200 e corpo correto se tudo der certo")
    void jogarStatusECorpoCorretos() {
        int idPartida = 100;
        int filmeVencedor = 2;
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("jteste");

        Partida partidaFinalizada = new Partida();
        partidaFinalizada.setId(idPartida);
        partidaFinalizada.setAposta(filmeVencedor);

        when(service.jogarPartida(authentication.getName(), idPartida, filmeVencedor)).thenReturn(partidaFinalizada);
        ResponseEntity<Partida> resposta = controller.jogar(idPartida, filmeVencedor, authentication);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(partidaFinalizada, resposta.getBody());
        assertEquals(idPartida, resposta.getBody().getId());
        assertEquals(filmeVencedor, resposta.getBody().getAposta());
    }

    @Test
    @DisplayName("ranking() deve retornar status 204 e sem corpo se o ranking estiver vazio")
    void ranking204SemCorpo() {
        when(service.getRanking()).thenReturn(new ArrayList<>());

        ResponseEntity<List<PontuacaoJogadorReponse>> resposta = controller.ranking();
        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertNull(resposta.getBody());
    }

    @Test
    @DisplayName("ranking() deve retornar status 200 e com corpo obtido da service caso existam dados no ranking")
    void ranking200ComCorpo() {
        List<PontuacaoJogadorReponse> lista = List.of(
            new PontuacaoJogadorReponse("j1", 100),
            new PontuacaoJogadorReponse("j2", 200)
        );
        when(service.getRanking()).thenReturn(lista);

        ResponseEntity<List<PontuacaoJogadorReponse>> resposta = controller.ranking();
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(lista, resposta.getBody());
    }
}