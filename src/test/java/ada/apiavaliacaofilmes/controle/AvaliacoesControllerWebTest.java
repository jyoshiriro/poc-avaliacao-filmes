package ada.apiavaliacaofilmes.controle;

import ada.apiavaliacaofilmes.dominio.Partida;
import ada.apiavaliacaofilmes.requisicao.IniciarPartidaRequest;
import ada.apiavaliacaofilmes.resposta.PontuacaoJogadorReponse;
import ada.apiavaliacaofilmes.servico.PartidaService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AvaliacoesController.class)
@AutoConfigureMockMvc(addFilters = false)
class AvaliacoesControllerWebTest {

    private static final String URI_BASE = "/avaliacoes-filmes";

    @MockBean
    PartidaService service;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper;

    Principal principal;


    @BeforeEach
    void setup() {
        mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        principal = mock(Authentication.class);
        when(principal.getName()).thenReturn("jteste");
    }

    @Test
    @DisplayName("iniciar jogada com requisição inválida deve retornar 400")
    void iniciarErroValidacao() throws Exception {
        IniciarPartidaRequest request = new IniciarPartidaRequest();

        mockMvc.perform(
                post(URI_BASE).contentType(MediaType.APPLICATION_JSON).principal(principal)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("iniciar jogada com requisição inválida deve retornar o erro da service")
    void iniciarErroValidacaoService() throws Exception {
        IniciarPartidaRequest request = new IniciarPartidaRequest();
        FieldUtils.writeDeclaredField(request, "tituloFilme1", "meu filme", true);
        FieldUtils.writeDeclaredField(request, "tituloFilme2", "meu filme", true);

        when(service.iniciarPartida(any())).then(invocation -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "erro!");
        });

        mockMvc.perform(
                post(URI_BASE).contentType(MediaType.APPLICATION_JSON).principal(principal)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("iniciar jogada com requisição válida deve retornar 201 e com corpo válido")
    void iniciarCriado() throws Exception {
        IniciarPartidaRequest request1 = new IniciarPartidaRequest();
        FieldUtils.writeDeclaredField(request1, "tituloFilme1", "meu filme 1", true);
        FieldUtils.writeDeclaredField(request1, "tituloFilme2", "meu filme 2", true);

        when(service.iniciarPartida(any())).thenReturn(new Partida());

        MvcResult resposta = mockMvc.perform(
                        post(URI_BASE).contentType(MediaType.APPLICATION_JSON).principal(principal)
                                .content(mapper.writeValueAsString(request1)))
                .andExpect(status().isCreated())
                .andReturn();

        assertDoesNotThrow(() -> mapper.readValue(resposta.getResponse().getContentAsString(), Partida.class));
    }


    @Test
    @DisplayName("finalizar jogada com requisição inválida deve retornar o erro lançado pela service")
    void jogarErroValidacao() throws Exception {
        int idPartida = 100;
        int filmeVencedor = 2;

        when(service.jogarPartida(principal.getName(), idPartida, filmeVencedor)).thenAnswer(invocation -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "erro!");
        });

        mockMvc.perform(
                patch("%s/%d/%d".formatted(URI_BASE, idPartida, filmeVencedor)).principal(principal))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("iniciar jogada com requisição válida deve retornar 200 e com corpo válido")
    void jogarValido() throws Exception {
        int idPartida = 100;
        int filmeVencedor = 2;

        when(service.jogarPartida(principal.getName(), idPartida, filmeVencedor)).thenReturn(new Partida());

        MvcResult resposta = mockMvc.perform(
                patch("%s/%d/%d".formatted(URI_BASE, idPartida, filmeVencedor)).principal(principal))
                .andExpect(status().isOk())
                .andReturn();

        assertDoesNotThrow(() -> mapper.readValue(resposta.getResponse().getContentAsString(), Partida.class));
    }


    @Test
    @DisplayName("ranking deve retornar 204 e sem corpo caso não existam dados")
    void ranking204SemCorpo() throws Exception {
        when(service.getRanking()).thenReturn(new ArrayList<>());

        MvcResult resposta = mockMvc.perform(
                get("%s/ranking".formatted(URI_BASE)).principal(principal))
                .andExpect(status().isNoContent())
                .andReturn();

        assertEquals(0, resposta.getResponse().getContentLength());
    }

    @Test
    @DisplayName("ranking deve retornar 200 e com os dados da service no corpo")
    void ranking200ComCorpo() throws Exception {
        var lista = List.of(
            new PontuacaoJogadorReponse("j1", 1_000),
            new PontuacaoJogadorReponse("j2", 300),
            new PontuacaoJogadorReponse("j3", 200)
        );
        when(service.getRanking()).thenReturn(lista);

        MvcResult resposta = mockMvc.perform(
                get("%s/ranking".formatted(URI_BASE)).principal(principal))
                .andExpect(status().isOk())
                .andReturn();

        assertDoesNotThrow(() -> {
            mapper.readerForListOf(PontuacaoJogadorReponse.class)
                    .readValue(resposta.getResponse().getContentAsString());
        });
    }
}