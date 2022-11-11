package ada.apiavaliacaofilmes.controle;

import ada.apiavaliacaofilmes.dominio.Partida;
import ada.apiavaliacaofilmes.requisicao.IniciarPartidaRequest;
import ada.apiavaliacaofilmes.resposta.PontuacaoJogadorReponse;
import ada.apiavaliacaofilmes.servico.PartidaService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;


@RequiredArgsConstructor
@RestController
@RequestMapping("/avaliacoes-filmes")
@Secured("ROLE_jogador")
public class AvaliacoesController { // ou AvaliacoesResource

    private final PartidaService partidaService;

    @PostMapping
    public ResponseEntity<Partida> iniciar(@RequestBody @Valid IniciarPartidaRequest request,
                                           Authentication authentication) {
        request.setJogador(authentication.getName());
        return status(CREATED).body(partidaService.iniciarPartida(request));
    }

    @PatchMapping("/{idPartida}/{filmeVencedor}")
    public ResponseEntity<Partida> jogar(@PathVariable
                                         @Parameter(description = "Código da partida")
                                         int idPartida,
                                         @PathVariable
                                         @Parameter(description = "Filme que julga ser o vencedor. Apenas 1 ou 2")
                                         int filmeVencedor,
                                         Authentication authentication) {
        return status(OK).body(partidaService.jogarPartida(authentication.getName(), idPartida, filmeVencedor));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<PontuacaoJogadorReponse>> ranking() {
        List<PontuacaoJogadorReponse> ranking = partidaService.getRanking();

        return ranking.isEmpty()
                ? status(HttpStatus.NO_CONTENT).build()
                : status(OK).body(ranking);
    }
}
