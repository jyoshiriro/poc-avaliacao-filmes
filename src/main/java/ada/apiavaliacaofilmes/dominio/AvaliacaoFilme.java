package ada.apiavaliacaofilmes.dominio;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class AvaliacaoFilme {
    @JsonSetter("Title") private String titulo;
    @JsonSetter("Year") private Integer ano;
    @JsonSetter("Poster") private String poster;
    @JsonSetter("imdbRating") private Double nota;
    @JsonSetter("imdbVotes") private String votos; // retorna valores como "1,382,964" :(
    @JsonSetter("imdbID") private String id;

    public int getVotos() {
        try {
            return votos == null ? 0 : Integer.parseInt(votos.replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException excecao) { // retorna valores como N/A" :(
            return 0;
        }
    }

    public double getPontuacao() {
        return nota == null ? 0.0 : nota * getVotos();
    }
}
/*public record AvaliacaoFilme(
    @JsonSetter("Title") String titulo,
    @JsonSetter("Year") Integer ano,
    @JsonSetter("Poster") String poster,
    @JsonSetter("imdbRating") Double nota,
    @JsonSetter("imdbVotes") String votos, // retorna valores como "1,382,964" :(
    @JsonSetter("imdbID") String id
) {

    public int contagemVotos() {
        try {
            return Integer.parseInt(votos.replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException excecao) {
            return 0;
        }
    }

}*/
