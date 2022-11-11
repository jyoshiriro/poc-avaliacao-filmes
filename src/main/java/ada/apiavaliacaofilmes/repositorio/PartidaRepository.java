package ada.apiavaliacaofilmes.repositorio;

import ada.apiavaliacaofilmes.dominio.Partida;
import ada.apiavaliacaofilmes.resposta.PontuacaoJogadorReponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PartidaRepository extends JpaRepository<Partida, Integer> {

    boolean existsByJogadorAndApostaIsNull(String jogador);

    boolean existsByIdAndApostaIsNotNull(int idPartida);

    boolean existsByIdAndJogador(int idPartida, String jogador);

    @Query("""
        select count(p) from Partida p where p.jogador = ?1 
        and p.aposta is not null and p.aposta != p.vencedor
    """)
    int countErrosPorJogador(String jogador);

    @Query("""
    select count(p)>0 from Partida p where 
    p.jogador = ?1 and 
    ( (p.idFilme1 = ?2 and p.idFilme2 = ?3) or (p.idFilme1 = ?3 and p.idFilme2 = ?2) )  
    """)
    boolean existsByFilmes(String jogador, String idFilme1, String idFilme2);

    @Query("""
    select new ada.apiavaliacaofilmes.resposta.PontuacaoJogadorReponse(
        p.jogador, 
        ( 
          count(p.jogador) * 
          ((select count(p2.jogador) from Partida p2 
            where p2.jogador = p.jogador and p2.aposta = p2.vencedor) / (count(p.jogador)*1.0)) * 100 
        ) as pontuacao 
    )
    from Partida p 
    group by p.jogador
    order by pontuacao desc
    """)
    List<PontuacaoJogadorReponse> findPontuacoes();

}
