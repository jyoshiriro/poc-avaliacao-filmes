package ada.apiavaliacaofilmes.servico;

import ada.apiavaliacaofilmes.dominio.AvaliacaoFilme;
import ada.apiavaliacaofilmes.dominio.Partida;
import ada.apiavaliacaofilmes.repositorio.PartidaRepository;
import ada.apiavaliacaofilmes.requisicao.IniciarPartidaRequest;
import ada.apiavaliacaofilmes.resposta.PontuacaoJogadorReponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartidaService {

    @Value("${regras.limite-erros}")
    private int limiteErrors;
    private final PartidaRepository partidaRepository;
    private final OmdbService omdbService;

    @Transactional
    public Partida iniciarPartida(IniciarPartidaRequest request) {

        request.validar();

        Partida novaPartida = new Partida();
        novaPartida.setJogador(request.getJogador());
        novaPartida.setIdFilme1(omdbService.getAvaliacaoFilmePorTitulo(request.getTituloFilme1()).getId());
        novaPartida.setIdFilme2(omdbService.getAvaliacaoFilmePorTitulo(request.getTituloFilme2()).getId());

        validarNovaPartida(novaPartida);

        return partidaRepository.save(novaPartida);
    }

    protected void validarNovaPartida(Partida partida) {
        String erro = null;

        if (partidaRepository.countErrosPorJogador(partida.getJogador()) > limiteErrors) {
            erro = "Não é possível jogar após %d erros".formatted(limiteErrors+1);
        } else if (partidaRepository.existsByJogadorAndApostaIsNull(partida.getJogador())) {
            erro = "Há uma partida não finalizada por você";
        } else
            if (partidaRepository.existsByFilmes(partida.getJogador(), partida.getIdFilme1(), partida.getIdFilme2())) {
            erro = "Já existe uma partida com os mesmos 2 filmes para você";
        }

        if (erro != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, erro);
        }
    }

    @Transactional
    public Partida jogarPartida(String jogador, int idPartida, int filmeVencedor) {
        validarJogada(jogador, idPartida, filmeVencedor);

        Partida partida = partidaRepository.getReferenceById(idPartida);
        AvaliacaoFilme avaliacaoFilme1 = omdbService.getAvaliacaoFilmePorId(partida.getIdFilme1());
        AvaliacaoFilme avaliacaoFilme2 = omdbService.getAvaliacaoFilmePorId(partida.getIdFilme2());
        partida.setAposta(filmeVencedor);
        partida.setVencedor(avaliacaoFilme1.getPontuacao() >= avaliacaoFilme2.getPontuacao() ? 1 : 2);

        return partidaRepository.save(partida);
    }

    protected void validarJogada(String jogador, int idPartida, int filmeVencedor) {
        String erro = null;
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (filmeVencedor < 1 || filmeVencedor > 2) {
            erro = "Filme vencedor deve ser 1 ou 2. Recebido: %d".formatted(filmeVencedor);
        } else if (!partidaRepository.existsById(idPartida)) {
            erro = "Partida %d não existe".formatted(filmeVencedor);
            status = HttpStatus.NOT_FOUND;
        } else if (!partidaRepository.existsByIdAndJogador(idPartida, jogador)) {
            erro = "Essa partida não é de outro jogador"; // regra não descrita na tarefa
        } else if (partidaRepository.existsByIdAndApostaIsNotNull(idPartida)) {
            erro = "Essa partida já foi finalizada";
        }

        if (erro != null) {
            throw new ResponseStatusException(status, erro);
        }
    }

    public List<PontuacaoJogadorReponse> getRanking() {
        return partidaRepository.findPontuacoes();
    }

}
