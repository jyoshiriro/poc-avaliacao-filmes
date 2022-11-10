package ada.apiavaliacaofilmes.dominio;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Data
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String jogador;

    @Column(length = 20)
    private String idFilme1;

    @Column(length = 20)
    private String idFilme2;

    private Integer aposta;

    private Integer vencedor;

    @CreationTimestamp
    private LocalDateTime inicio;

    @UpdateTimestamp
    private LocalDateTime fim;

    @Transient
    public Boolean getAcertou() {
        return vencedor == null ? null : Objects.equals(aposta, vencedor);
    }

}
