package ada.apiavaliacaofilmes.servico;

import ada.apiavaliacaofilmes.clientesapi.OmdbClienteApi;
import ada.apiavaliacaofilmes.dominio.AvaliacaoFilme;
import feign.codec.DecodeException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OmdbService {

    protected final static String TIPO_MOVIE = "movie";
    protected final static String PLOT_FULL = "full";

    private final OmdbClienteApi clienteApi;

    @Value("${omdb-api.key}")
    private String apiKey;

    public AvaliacaoFilme getAvaliacaoFilmePorTitulo(String titulo) {
        try {
            AvaliacaoFilme avaliacaoFilme = clienteApi.getFilmePorTitulo(apiKey, titulo, TIPO_MOVIE).get();
            if (avaliacaoFilme.getId() == null) {
                throw filmeNaoEncontradoException(titulo); // terrível WA porque a API OMDB tem péssimo design!
            }
            return avaliacaoFilme;
        } catch (DecodeException exception) {
            throw filmeNaoEncontradoException(titulo);
        }
    }

    public AvaliacaoFilme getAvaliacaoFilmePorId(String id) {
        try {
            AvaliacaoFilme avaliacaoFilme = clienteApi.getFilmePorId(apiKey, id, TIPO_MOVIE, PLOT_FULL).get();
            if (avaliacaoFilme.getId() == null) {
                throw filmeNaoEncontradoException(id); // terrível WA porque a API OMDB tem péssimo design!
            }
            return avaliacaoFilme;
        } catch (DecodeException exception) {
            throw filmeNaoEncontradoException(id);
        }
    }

    protected ResponseStatusException filmeNaoEncontradoException(String idOuTitulo) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Filme '%s' não encontrado".formatted(idOuTitulo));
    }

    /*
    As soluções abaixo seriam as ideais, no entanto, devido ao PÉSSIMO design da API OMDB, ocorre JsonParseException/DecodeException em caso de filme não encontrado,
    pois retorna um status 200 (sim!) e um JSON {"Response":"False","Error":"Movie not found!"} ao melhor estilo "via cep"
     */
    /*public AvaliacaoFilme getAvaliacaoFilmePorTitulo(String titulo) {
        Optional<AvaliacaoFilme> avaliacao = clienteApi.getFilmePorTitulo(apiKey, titulo, TIPO_MOVIE);
        return avaliacao.orElseThrow(FilmeNaoEncontradoException::new);
    }

    public AvaliacaoFilme getAvaliacaoFilmePorId(String id) {
        Optional<AvaliacaoFilme> avaliacao = clienteApi.getFilmePorId(apiKey, id, TIPO_MOVIE, PLOT_FULL);
        return avaliacao.orElseThrow(FilmeNaoEncontradoException::new);
    }*/

}
