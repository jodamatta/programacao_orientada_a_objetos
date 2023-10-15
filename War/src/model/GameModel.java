package model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameModel {
	private static GameModel instance = null;
	private static final int MIN_JOGADORES = 3;
	private static final int MAX_JOGADORES = 6;
	
	// estados do jogo privados
	// exemplo: private List<Territorio> territorios
	private List<Jogador> jogadores = new ArrayList<>();
	private List<Objetivo> objetivosAtivos = new ArrayList<>();
	private List<Carta> CartasTerritorios = new ArrayList<>(); //Miguel
	
	private GameModel() {
	}
	
	public static GameModel getInstancia() {
		if(instance == null) {
			instance = new GameModel();
		}
		return instance;
	}
	
	protected void addJogador(String nome, CorJogador cor) {
		if (jogadores.size()>= MAX_JOGADORES) {
			throw new IllegalStateException("Numero máximo de jogadores atingido.");
		}
		Jogador jogador = new Jogador(nome, cor);
		jogadores.add(jogador);
	}

	protected List<Objetivo> filtraObjetivos() {
		List<Objetivo> objetivos = Arrays.stream(ListaObjetivos.values()).map(Objetivo::new).collect(Collectors.toList());
	
		return objetivos.stream().filter(obj->obj.objetivoValido(jogadores)).collect(Collectors.toList());
	}
	
	protected void sorteiaObjetivos() {
		objetivosAtivos = filtraObjetivos();
		Collections.shuffle(objetivosAtivos);
		for (Jogador jogador:jogadores) {
	        Objetivo objetivoSorteado = objetivosAtivos.remove(0);
	        jogador.setObjetivo(objetivoSorteado.getObjetivo());  // Pass the ListaObjetivos enum
	    }
	}

	protected void distribuiCartasTerritorio() {
		DeckTerritorios DeckTerritorios = new DeckTerritorios();
		int cartasDistribuidas = 0;
		while(cartasDistribuidas < DeckTerritorios.getSize()){
			for (Jogador jogador:jogadores) {
				Carta carta = DeckTerritorios.drawCard();
				 if (carta != null) {
					 jogador.addCarta(carta);
					 cartasDistribuidas++;
				 }
				 else{
					break;
				 }
			}
		}
	}

	protected void inicializaTerritorios(){
		for (Jogador jogador : jogadores){
			for (Carta carta : jogador.getCartas()){
				Territorio territorio = new Territorio(carta.getTerritorio(), jogador);
				jogador.addTerritorio(territorio);
			}
		}
	}
	
	protected void setOrdemJogada() {
		Collections.shuffle(jogadores);
	}
	
	protected void setPrimeirosExercitos() {
		
	}
	// funcao temporaria para teste
	public List<Jogador> getJogadores(){
		return Collections.unmodifiableList(jogadores);
	}
}