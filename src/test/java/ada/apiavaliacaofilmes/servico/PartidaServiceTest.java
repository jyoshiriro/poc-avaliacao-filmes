package ada.apiavaliacaofilmes.servico;

import ada.apiavaliacaofilmes.dominio.AvaliacaoFilme;
import ada.apiavaliacaofilmes.dominio.Partida;
import ada.apiavaliacaofilmes.repositorio.PartidaRepository;
import ada.apiavaliacaofilmes.requisicao.IniciarPartidaRequest;
import ada.apiavaliacaofilmes.resposta.PontuacaoJogadorReponse;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PartidaServiceTest {

    PartidaService service;
    PartidaRepository partidaRepository;
    OmdbService omdbService;

    @BeforeEach
    void setup() throws IllegalAccessException {
        partidaRepository = mock(PartidaRepository.class);
        omdbService = mock(OmdbService.class);

        service = new PartidaService(partidaRepository, omdbService);

        FieldUtils.writeDeclaredField(service, "limiteErrors", 3, true);
    }

    @Test
    @DisplayName("validarNovaPartida() deve lançar ResponseStatusException em caso de mais de 3 erros por jogador")
    void validarNovaPartidaMais3Erros() {
        Partida partida = new Partida();
        partida.setJogador("jteste");

        when(partidaRepository.countErrosPorJogador(partida.getJogador())).thenReturn(4);

        ResponseStatusException exception =
                                assertThrows(ResponseStatusException.class, () -> service.validarNovaPartida(partida));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Não é possível jogar após 4 erros", exception.getReason());
    }

    @Test
    @DisplayName("validarNovaPartida() deve lançar ResponseStatusException em caso de partida em aberto")
    void validarNovaPartidaPartidaEmAberto() {
        Partida partida = new Partida();
        partida.setJogador("jteste");

        when(partidaRepository.existsByJogadorAndApostaIsNull(partida.getJogador())).thenReturn(true);

        ResponseStatusException exception =
                                assertThrows(ResponseStatusException.class, () -> service.validarNovaPartida(partida));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Há uma partida não finalizada por você", exception.getReason());
    }

    @Test
    @DisplayName("validarNovaPartida() deve lançar ResponseStatusException em caso de outra partida com o mesmo par")
    void validarNovaPartidaMesmosFilmes() {
        Partida partida = new Partida();
        partida.setJogador("jteste");
        partida.setIdFilme1("idA");
        partida.setIdFilme2("idB");

        when(partidaRepository.existsByFilmes(partida.getJogador(), partida.getIdFilme1(), partida.getIdFilme2()))
                .thenReturn(true);

        ResponseStatusException exception =
                                assertThrows(ResponseStatusException.class, () -> service.validarNovaPartida(partida));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Já existe uma partida com os mesmos 2 filmes para você", exception.getReason());
    }

    @Test
    @DisplayName("validarNovaPartida() NÃO deve lançar ResponseStatusException caso tudo esteja válido")
    void validarNovaPartidaValida() {
        Partida partida = new Partida();
        partida.setJogador("jteste");
        partida.setIdFilme1("idA");
        partida.setIdFilme2("idB");

        when(partidaRepository.countErrosPorJogador(partida.getJogador())).thenReturn(3);
        when(partidaRepository.existsByJogadorAndApostaIsNull(partida.getJogador())).thenReturn(false);
        when(partidaRepository.existsByFilmes(partida.getJogador(), partida.getIdFilme1(), partida.getIdFilme2()))
                .thenReturn(false);

        assertDoesNotThrow(() -> service.validarNovaPartida(partida));
    }

    @Test
    @DisplayName("validarJogada() deve lançar ResponseStatusException se filme <1 ou >2")
    void validarJogadaFilmeInvalido() {

        String jogador = "jteste";
        int idPartida = 100;
        int filmeVencedor0 = 0;
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                                            service.validarJogada(jogador, idPartida, filmeVencedor0));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Filme vencedor deve ser 1 ou 2. Recebido: 0", exception.getReason());

        int filmeVencedor3 = 3;
        exception = assertThrows(ResponseStatusException.class, () ->
                    service.validarJogada(jogador, idPartida, filmeVencedor3));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Filme vencedor deve ser 1 ou 2. Recebido: 3", exception.getReason());
    }

    @Test
    @DisplayName("validarJogada() deve lançar ResponseStatusException se idPartida for inválido")
    void validarJogadaPartidaInvalida() {
        String jogador = "jteste";
        int idPartida = 100;
        int filmeVencedor = 1;

        when(partidaRepository.existsById(idPartida)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                                            service.validarJogada(jogador, idPartida, filmeVencedor));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Partida 100 não existe", exception.getReason());
    }

    @Test
    @DisplayName("validarJogada() deve lançar ResponseStatusException se idPartida for de outro jogador")
    void validarJogadaPartidaOutroJogador() {
        String jogador = "jteste";
        int idPartida = 100;
        int filmeVencedor = 1;

        when(partidaRepository.existsById(idPartida)).thenReturn(true);
        when(partidaRepository.existsByIdAndJogador(idPartida, jogador)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                                            service.validarJogada(jogador, idPartida, filmeVencedor));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Essa partida é de outro jogador", exception.getReason());
    }

    @Test
    @DisplayName("validarJogada() deve lançar ResponseStatusException se a partida já foi finalizada")
    void validarJogadaPartidaFinalizada() {
        String jogador = "jteste";
        int idPartida = 100;
        int filmeVencedor = 1;

        when(partidaRepository.existsById(idPartida)).thenReturn(true);
        when(partidaRepository.existsByIdAndJogador(idPartida, jogador)).thenReturn(true);
        when(partidaRepository.existsByIdAndApostaIsNotNull(idPartida)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                                            service.validarJogada(jogador, idPartida, filmeVencedor));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Essa partida já foi finalizada", exception.getReason());
    }

    @Test
    @DisplayName("validarJogada() NÃO deve lançar ResponseStatusException se a tudo estiver válido")
    void validarJogadaPartidaValido() {
        String jogador = "jteste";
        int idPartida = 100;
        int filmeVencedor = 1;

        when(partidaRepository.existsById(idPartida)).thenReturn(true);
        when(partidaRepository.existsByIdAndJogador(idPartida, jogador)).thenReturn(true);
        when(partidaRepository.existsByIdAndApostaIsNotNull(idPartida)).thenReturn(false);

        assertDoesNotThrow(() -> service.validarJogada(jogador, idPartida, filmeVencedor));
    }

    @Test
    @DisplayName("iniciarPartida() deve lançar ResponseStatusException caso as filmes sejam o mesmo")
    void iniciarPartidaMesmoFilme() throws IllegalAccessException {
        IniciarPartidaRequest request = new IniciarPartidaRequest();
        FieldUtils.writeDeclaredField(request, "tituloFilme1", "meu filme", true);
        FieldUtils.writeDeclaredField(request, "tituloFilme2", "meu filme", true);

        ResponseStatusException exception =
                                assertThrows(ResponseStatusException.class, () -> service.iniciarPartida(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    @DisplayName("iniciarPartida() deve retornar a Partida recém criada para o jogador")
    void iniciarPartidaValido() throws IllegalAccessException {
        IniciarPartidaRequest request = criarRequestValido();
        Integer novoId = 100;

        when(omdbService.getAvaliacaoFilmePorTitulo(anyString())).thenReturn(new AvaliacaoFilme());
        when(partidaRepository.save(any(Partida.class))).thenAnswer(invocation -> {
            Partida partida = invocation.getArgument(0, Partida.class);
            partida.setId(novoId);
            partida.setJogador(request.getJogador());
            return partida;
        });

        Partida partida = service.iniciarPartida(request);

        assertEquals(novoId, partida.getId());
        assertEquals(request.getJogador(), partida.getJogador());
        assertNull(partida.getAcertou());
    }

    IniciarPartidaRequest criarRequestValido() {
        IniciarPartidaRequest request = new IniciarPartidaRequest();
        try {
            FieldUtils.writeDeclaredField(request, "idPartida", 100, true);
            FieldUtils.writeDeclaredField(request, "jogador", "jteste", true);
            FieldUtils.writeDeclaredField(request, "tituloFilme1", "meu filme 1", true);
            FieldUtils.writeDeclaredField(request, "tituloFilme2", "meu filme 2", true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return request;
    }

    @Test
    @DisplayName("jogarPartida() deve retornar a Partida com os dados corretos, de partida válida")
    void jogarPartida() {
        String jogador = "jteste";
        int idPartida = 100;
        int filmeVencedor = 1;

        Partida partida = new Partida();
        partida.setId(idPartida);
        partida.setJogador(jogador);
        partida.setIdFilme1("f1");
        partida.setIdFilme2("f2");
        partida.setAposta(filmeVencedor);

        AvaliacaoFilme avaliacao1 = mock(AvaliacaoFilme.class);
        when(avaliacao1.getPontuacao()).thenReturn(500.0);

        AvaliacaoFilme avaliacao2 = mock(AvaliacaoFilme.class);
        when(avaliacao2.getPontuacao()).thenReturn(9_000.0);

        when(partidaRepository.getReferenceById(idPartida)).thenReturn(partida);
        when(partidaRepository.existsById(idPartida)).thenReturn(true);
        when(partidaRepository.existsByIdAndJogador(idPartida, jogador)).thenReturn(true);
        when(partidaRepository.existsByIdAndApostaIsNotNull(idPartida)).thenReturn(false);
        when(omdbService.getAvaliacaoFilmePorId(partida.getIdFilme1())).thenReturn(avaliacao1);
        when(omdbService.getAvaliacaoFilmePorId(partida.getIdFilme2())).thenReturn(avaliacao2);

        when(partidaRepository.save(any(Partida.class))).thenReturn(partida);

        Partida partidaResultado = service.jogarPartida(jogador, idPartida, filmeVencedor);

        assertEquals(idPartida, partidaResultado.getId());
        assertEquals(jogador, partidaResultado.getJogador());
        assertEquals(filmeVencedor, partidaResultado.getAposta());
        assertEquals(2, partidaResultado.getVencedor());
        assertFalse(partidaResultado.getAcertou());


        when(avaliacao1.getPontuacao()).thenReturn(500.0);
        when(avaliacao2.getPontuacao()).thenReturn(90.0);

        partidaResultado = service.jogarPartida(jogador, idPartida, filmeVencedor);
        assertEquals(1, partidaResultado.getVencedor());
        assertTrue(partidaResultado.getAcertou());

    }

    @Test
    @DisplayName("getRanking() deve entregar a exata lista que obteve da Repository")
    void getRanking() {
        List<PontuacaoJogadorReponse> rankingVazio = service.getRanking();
        assertTrue(rankingVazio.isEmpty());

        List<PontuacaoJogadorReponse> pontuacoes = List.of(
                new PontuacaoJogadorReponse("j1", 200.0),
                new PontuacaoJogadorReponse("j2", 300.0)
        );
        when(partidaRepository.findPontuacoes()).thenReturn(pontuacoes);

        List<PontuacaoJogadorReponse> rankingPreenchido = service.getRanking();

        assertEquals(pontuacoes, rankingPreenchido);
    }
}