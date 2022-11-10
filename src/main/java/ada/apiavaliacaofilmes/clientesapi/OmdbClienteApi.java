package ada.apiavaliacaofilmes.clientesapi;

import ada.apiavaliacaofilmes.dominio.AvaliacaoFilme;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(value = "omdbapi", url = "http://www.omdbapi.com")
public interface OmdbClienteApi {

    @GetMapping
    Optional<AvaliacaoFilme> getFilmePorTitulo(@RequestParam String apikey,
                                               @RequestParam String t,
                                               @RequestParam String type);

    @GetMapping
    Optional<AvaliacaoFilme> getFilmePorId(@RequestParam String apikey,
                                           @RequestParam String i,
                                           @RequestParam String type,
                                           @RequestParam String plot);
}
