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

 /*   public String getCombinacaoFilmes() {
        return Stream.of(filme1, filme2).sorted().collect(Collectors.joining(" e "));
    }*/

    /*public static void main(String[] args) {
        var r1 = new IniciarPartidaRequest(1, "j1", "fa", "fb");
        var r2 = new IniciarPartidaRequest(2, "j1", "fb", "fa");

        System.out.println(r1.getCombinacaoFilmes());
        System.out.println(r2.getCombinacaoFilmes());

        Map<String, List<IniciarPartidaRequest>> res1 = List.of(r1,r2).stream()
                .collect(groupingBy(IniciarPartidaRequest::getCombinacaoFilmes));

        var res2 = List.of(r1,r2).stream()
                .collect(Collectors.groupingBy(IniciarPartidaRequest::getCombinacaoFilmes, Collectors.counting()));

        System.out.println(res1);
        System.out.println(res2); // este!
    }
*/
}
