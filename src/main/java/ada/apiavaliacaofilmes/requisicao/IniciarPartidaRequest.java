package ada.apiavaliacaofilmes.requisicao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotBlank;

@Getter
public class IniciarPartidaRequest {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idPartida;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Setter
    private String jogador;

    @NotBlank
    private String tituloFilme1;

    @NotBlank
    private String tituloFilme2;

    public void validar() {
        if (tituloFilme1.trim().equalsIgnoreCase(tituloFilme2.trim())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Não é possível criar uma partida usando o mesmo título ('%s') para os 2 filmes".formatted(tituloFilme1)
            );
        }
    }

}
