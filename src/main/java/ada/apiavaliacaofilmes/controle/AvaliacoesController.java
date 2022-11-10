package ada.apiavaliacaofilmes.controle;

import ada.apiavaliacaofilmes.dominio.AvaliacaoFilme;
import ada.apiavaliacaofilmes.dominio.Partida;
import ada.apiavaliacaofilmes.requisicao.IniciarPartidaRequest;
import ada.apiavaliacaofilmes.resposta.PontuacaoJogadorReponse;
import ada.apiavaliacaofilmes.servico.OmdbService;
import ada.apiavaliacaofilmes.servico.PartidaService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;


@RequiredArgsConstructor
@RestController
@RequestMapping("/avaliacoes-filmes")
@Secured("ROLE_jogador")
public class AvaliacoesController { // ou AvaliacoesResource

    private final PartidaService partidaService;

    @PostMapping
    public ResponseEntity<Partida> iniciar(@RequestBody IniciarPartidaRequest request,
                                           Authentication authentication) {
        request.setJogador(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(partidaService.iniciarPartida(request));
    }

    @PatchMapping("/{idPartida}/{filmeVencedor}")
    public ResponseEntity<Partida> jogar(@PathVariable
                                         @Parameter(description = "Código da partida")
                                         int idPartida,
                                         @PathVariable
                                         @Parameter(description = "Filme que julga ser o vencedor. Apenas 1 ou 2")
                                         int filmeVencedor,
                                         Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(
                                partidaService.jogarPartida(authentication.getName(), idPartida, filmeVencedor));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<PontuacaoJogadorReponse>> ranking() {
        List<PontuacaoJogadorReponse> ranking = partidaService.getRanking();

        return ranking.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.status(HttpStatus.OK).body(ranking);
    }
}
