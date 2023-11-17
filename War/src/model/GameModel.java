package model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import controller.GameController;

public class GameModel {
	private static GameModel instance = null;
	private static GameController instance_controller;
	private static final int MAX_JOGADORES = 6;
	private Scanner scanner;
	private int numTrocas = 0;
	private int numJogadorAtual = 0;
	private int faseRodada = 0;
	private DeckTerritorios deckCartas = new DeckTerritorios();
	private int numExercitosDisponiveis = 0;
	private Ataque ataqueAtual;
	private int rodadaInicialFlag = 0;
	private boolean alguemVenceu = false;
	// estados do jogo privados
	private static List<Jogador> jogadores = new ArrayList<>();
	private static List<Territorio> territorios = new ArrayList<>();
	private List<Objetivo> objetivosAtivos = new ArrayList<>();
	private List<Continente> continentes = new ArrayList<>(Arrays.asList(
		new Continente("Africa"),
		new Continente("America do Norte"),
		new Continente("Asia"),
		new Continente("America do Sul"),
		new Continente("Europa"),
		new Continente("Oceania")
	)); 
	
	private GameModel() {
	}
	
	public static GameModel getInstancia() {
		if(instance == null) {
			instance = new GameModel();
		}
		return instance;
	}
	
	public int getMaxJogadores() {
		return MAX_JOGADORES;
	}
	
	public void addJogador(String nome, String cor) {
		CorJogador color = CorJogador.valueOf(cor);
		Jogador jogador = new Jogador(nome, color);
		jogadores.add(jogador);
	}
	
	public String[] getCores() {
		return Arrays.stream(CorJogador.values()).map(Enum::name).toArray(String[]::new);
	}

	public boolean objetivoValido(ListaObjetivos objetivo, List<Jogador> jogadores) {
        switch(objetivo) {
            case ELIM_AZUL:
                return jogadores.stream().anyMatch(p -> p.getCor() == CorJogador.Azul);
            case ELIM_VERMELHO:
                return jogadores.stream().anyMatch(p -> p.getCor() == CorJogador.Vermelho);
            case ELIM_VERDE:
                return jogadores.stream().anyMatch(p -> p.getCor() == CorJogador.Verde);
            case ELIM_AMARELO:
                return jogadores.stream().anyMatch(p -> p.getCor() == CorJogador.Amarelo);
            case ELIM_BRANCO:
                return jogadores.stream().anyMatch(p -> p.getCor() == CorJogador.Branco);
            case ELIM_PRETO:
                return jogadores.stream().anyMatch(p -> p.getCor() == CorJogador.Preto);
            default:
                return true;
        }
    }

	public List<Objetivo> filtraObjetivos() {
        List<Objetivo> objetivos = Arrays.stream(ListaObjetivos.values()).map(Objetivo::new).collect(Collectors.toList());
        return objetivos.stream().filter(obj -> this.objetivoValido(obj.getObjetivo(), jogadores)).collect(Collectors.toList());
    }

	public void sorteiaObjetivos() {
		Map<String, Objetivo> dictCorElimCor = new HashMap<>();
		dictCorElimCor.put("Azul", new Objetivo(ListaObjetivos.ELIM_AZUL));
		dictCorElimCor.put("Vermelho", new Objetivo(ListaObjetivos.ELIM_VERMELHO));
		dictCorElimCor.put("Verde", new Objetivo(ListaObjetivos.ELIM_VERDE));
		dictCorElimCor.put("Amarelo", new Objetivo(ListaObjetivos.ELIM_AMARELO));
		dictCorElimCor.put("Branco", new Objetivo(ListaObjetivos.ELIM_BRANCO));
		dictCorElimCor.put("Preto", new Objetivo(ListaObjetivos.ELIM_PRETO));
        objetivosAtivos = filtraObjetivos();
        Collections.shuffle(objetivosAtivos);
        for (Jogador jogador:jogadores) {
			if(objetivosAtivos.get(0) == dictCorElimCor.get(jogador.getCor().toString())){
				Objetivo objetivoSorteado = objetivosAtivos.remove(1);
            	jogador.setObjetivo(objetivoSorteado.getObjetivo());  
			} else{
				Objetivo objetivoSorteado = objetivosAtivos.remove(0);
				jogador.setObjetivo(objetivoSorteado.getObjetivo());  
			}
		}
	}

	public void distribuiCartasTerritorio() {
		DeckTerritorios deckTerritoriosInical = new DeckTerritorios();
		int cartasDistribuidas = 0;
		while(cartasDistribuidas < 51){
			for (Jogador jogador:jogadores) {
				Carta carta = deckTerritoriosInical.drawCard();
				 if (carta == null) {
					 break;
				 }
				jogador.addCarta(carta);
				cartasDistribuidas++;
			}
		}
	}

	public void inicializaTerritorios(){
		for (Jogador jogador : jogadores){
			for (Carta carta : jogador.getCartas()){
				Territorio territorio = new Territorio(carta.getTerritorio(), jogador);
				territorios.add(territorio);
				jogador.addTerritorio(territorio);
				continentes.get(territorio.getContinente()).addPais(territorio);
			}
		}
	}

	public int querTrocar(Jogador jogador){
		if( jogador.verificaTroca() != 0){
			System.out.println("Voce tem uma troca valida. Voce quer trocar? (S/N)");
			String resposta;
			resposta = scanner.nextLine();
			while (!resposta.equals("S") && !resposta.equals("N")){
				System.out.println("Resposta inválida. Escolha S ou N");
				resposta = scanner.nextLine();
			}
			if(resposta.equals("S")){
				return 1;
			}
		}
		return 0;
	}

	public void addExercitoCarta(){
		Jogador jogador = jogadores.get(numJogadorAtual);
		if(jogador.verificaTroca() == 0){
			return;
		}
		jogador.removerCartas(jogador.verificaTroca(), deckCartas);
		int exercitoNumero = 0;
		switch(numTrocas){
			case 0:
				exercitoNumero = 4;
				break;
			case 1:
				exercitoNumero = 6;
				break;
			case 2:
				exercitoNumero = 8;
				break;
			case 3:
				exercitoNumero = 10;
				break;
			case 4:
				exercitoNumero = 12;
				break;
			case 5:
				exercitoNumero = 15;
				break;
			default:
				exercitoNumero = 15 + (numTrocas-5)*5;
				break;
		}
		numTrocas++;
		this.numExercitosDisponiveis += exercitoNumero;
		instance_controller.setCorJogadorController();
	}

	public void setOrdemJogada() {
		Collections.shuffle(jogadores);
	}

	public List<Jogador> getJogadores(){
		return Collections.unmodifiableList(jogadores);
	}
	
	public String getNomeJogadorAtual(){
		return jogadores.get(numJogadorAtual).getNome();
	}

	public String getCorJogadorAtual(){
		return jogadores.get(numJogadorAtual).getCor().toString();
	}

	public void exercitosDisponiveis(){
		Jogador jogador = jogadores.get(numJogadorAtual);
		int numTerritorios = jogador.getTerritorios().size();
		numExercitosDisponiveis = numTerritorios/2;
		if(numExercitosDisponiveis < 3){
			numExercitosDisponiveis = 3;
		}
		for(Continente c : continentes){
			if(c.foiDominado(jogador) == 1){
				numExercitosDisponiveis += c.getBonus();
			}
		}
		
	}

	public void passaVez(){
		numJogadorAtual = (numJogadorAtual +1) % jogadores.size();
		faseRodada = 0;
		descansaTodosExercitos();
		exercitosDisponiveis();
		instance_controller.setCorJogadorController();
	}

	public boolean passaFase(){
		checaFimJogo();
		if (rodadaInicialFlag < jogadores.size()){
			passaVez();
			rodadaInicialFlag++;
		} else{
			faseRodada++;
			if(faseRodada == 3){
				passaVez();
				faseRodada = 0;
			}
		}
		if (rodadaInicialFlag < jogadores.size()){
			return true;
		}
		return false;
	}

	public List<String> getTerritoriosJogadorAtual(){
		return jogadores.get(numJogadorAtual).getTerritorios().stream().map(Territorio::getNome).collect(Collectors.toList());
	}

	public List<String> getCartasJogadorAtual(){
		return jogadores.get(numJogadorAtual).getCartas().stream().map(Carta::getTerritorio).collect(Collectors.toList());
	}
	
	public void printPlayers() {
		System.out.println("Lista de jogadores:");
	    for (Jogador player : jogadores) {
	        System.out.println("Nome: " + player.getNome() + ", Cor: " + player.getCor().name());
	    }
	}

	public List<Continente> getContinentes(){
		return Collections.unmodifiableList(continentes);
	}

	public String getTerritorioCor(String nomeTerritorio){
		for (Territorio t : territorios){
			if (t.getNome() == nomeTerritorio){
				return t.getJogador().getCor().toString();
			}
		}
		return null;
	}

	public int getNumSoldados(String nomeTerritorio){
		for (Territorio t : territorios){
			if (t.getNome() == nomeTerritorio){
				return t.getNumeroSoldados();
			}
		}
		return 0;
	}

	public int getNumSoldadosTotal(String nomeTerritorio){
		Territorio t = stringToTerritorio(nomeTerritorio);
		return t.getNumeroSoldados() + t.getNumeroSoldadosCansados();
	}

	public String getObjetivoJogadorAtual(){
		return jogadores.get(numJogadorAtual).getObjetivo().toString();
	}

	public Territorio stringToTerritorio(String nomeTerritorio){
		for (Territorio t : territorios){
			if (t.getNome() == nomeTerritorio){
				return t;
			}
		}
		return null;
	}

	public List<String> getVizinhosAliados(String nomeTerritorio){
		Territorio territorio = stringToTerritorio(nomeTerritorio);
		Jogador jogador = territorio.getJogador();
        List<String> vizinhos = territorio.getVizinhos();
        List<String> vizinhosAliados = new ArrayList<String>();
        for (String vizinho : vizinhos) {
            if (jogador.equals(stringToTerritorio(vizinho).getJogador())) {
                vizinhosAliados.add(vizinho);
            }
        }
        return vizinhosAliados;
    }

	public void movimentaExercito(int numExercMovi, String nomeOrigem, String nomeDestino){
		Territorio origem = stringToTerritorio(nomeOrigem);
		Territorio destino = stringToTerritorio(nomeDestino);
		if (origem.getNumeroSoldados() - numExercMovi < 1 && origem.getNumeroSoldadosCansados() == 0){
			System.out.println("Numero de exercitos invalido");
			return;
		}
		origem.alteraNumSoldados(-numExercMovi);
		destino.addExercitoCansado(numExercMovi);
	}

	public int getNumSoldadosMovimentoValido(String nomeTerritorio){
		Territorio territorio = stringToTerritorio(nomeTerritorio);
		int numSoldados = territorio.getNumeroSoldados();
		int numSoldadosCansados = territorio.getNumeroSoldadosCansados();
		if (numSoldadosCansados == 0){
			return numSoldados-1;
		}
		return numSoldados;
	}
	
	public int getFaseRodada(){
		return faseRodada;
	}

	public void descansaTodosExercitos(){
		for (Territorio t : territorios){
			t.descansaExercito();
		}
	}

	public boolean addExercitoTerritorio(String nomeTerritorio, int numExercitos){
		for (Territorio t : territorios){
			if (t.getNome() == nomeTerritorio && t.getJogador() == jogadores.get(numJogadorAtual)){
				t.alteraNumSoldados(numExercitos);
				return true;
			}
		}
		return false;
	}

	public void diminuiNumSoldadosDisponiveis(int numExercitos){
		numExercitosDisponiveis -= numExercitos;
	}

	public List<String> ataqueTerritorio(String nomeTerritorio){
		for (Territorio t : territorios){
			if (t.getNome() == nomeTerritorio && t.getJogador() == jogadores.get(numJogadorAtual) && t.getNumeroSoldados() > 1){
				ataqueAtual = new Ataque(jogadores.get(numJogadorAtual));
				ataqueAtual.setPaisOrigem(t);
				List<String> alvos = ataqueAtual.getAlvos();
				return alvos;
			}
		}
		return null;
	}

	public void mataAtaque(){
		ataqueAtual = null;
	}

	public int getNumSoldadosDisponiveis(){
		return numExercitosDisponiveis;
	}

	public void destinoAtaque(String nomeTerritorio, int numExercitos){
		boolean foiDominado;
		for (Territorio t : territorios){
			if (t.getNome() == nomeTerritorio){
				ataqueAtual.setAlvo(t);
				ataqueAtual.setNumAtacantes(numExercitos);
				ataqueAtual.setNumDefensores(Math.min(t.getNumeroSoldados(), 3));
				ataqueAtual.setJogadorDefensor(t.getJogador());
				ataqueAtual.rolaDados();
				foiDominado = ataqueAtual.avaliaAtaque();
				if(foiDominado){
					ataqueAtual.setExercitosDeslocados(1);
					ataqueAtual.conquistaAndDeslocamento();
				}
				return;
			}
		}
	}

	public List<Integer> getDadosAtaque(){
		return ataqueAtual.getDadosAtaque();
	}

	public List<Integer> getDadosDefesa(){
		return ataqueAtual.getDadosDefesa();
	}
	
	public Jogador corToJogador(String cor){
		for (Jogador j : jogadores){
			if (j.getCor().toString() == cor){
				return j;
			}
		}
		return null;
	}

	public boolean verificaObjetivo(Jogador jogador){
		if(alguemVenceu){
			return false;
		}

		ListaObjetivos objetivo = jogador.getObjetivo();
		switch (objetivo) {
            case ELIM_AZUL:
                return corToJogador("Azul").getTerritorios().isEmpty(); //checar
            case ELIM_VERMELHO:
                return corToJogador("Vermelho").getTerritorios().isEmpty();
            case ELIM_VERDE:
				return corToJogador("Verde").getTerritorios().isEmpty();
			case ELIM_AMARELO:
				return corToJogador("Amarelo").getTerritorios().isEmpty();
			case ELIM_BRANCO:
				return corToJogador("Branco").getTerritorios().isEmpty();
			case ELIM_PRETO:
				return corToJogador("Preto").getTerritorios().isEmpty();
			case CONQ_24:
				return jogador.getTerritorios().size() >= 24;
			case CONQ_18_2:
				return jogador.getTerritorios().size() >= 18 && jogador.getTerritorios().stream().allMatch(t -> t.getNumeroSoldados() >= 2);
			case CONQ_AS_LATAM:
				return (continentes.get(2).foiDominado(jogador) * continentes.get(3).foiDominado(jogador) == 1);
			case CONQ_NA_AF:
				return (continentes.get(1).foiDominado(jogador) * continentes.get(0).foiDominado(jogador) == 1);
			case CONQ_NA_AU:
				return (continentes.get(1).foiDominado(jogador) * continentes.get(5).foiDominado(jogador) == 1);
			case CONQ_EU_LATAM:
				return (continentes.get(4).foiDominado(jogador) * continentes.get(3).foiDominado(jogador) == 1);
			case CONQ_EU_AU:
				return (continentes.get(4).foiDominado(jogador) * continentes.get(5).foiDominado(jogador) == 1);
       	    default:
                System.out.println("Opção inválida objetivo");
        }
		return false;
	}

	public void checaFimJogo(){
		for (Jogador jogador : jogadores){
			if (verificaObjetivo(jogador)){
				alguemVenceu = true;
				instance_controller.janelaFimJogoController(jogador.getNome());
			}
		}
	}

	public String getCorPorNome(String nome){
		for (Jogador jogador : jogadores){
			if (jogador.getNome() == nome){
				return jogador.getCor().toString();
			}
		}
		return null;
	}

	public static void resetInstancia() {
        instance = null;
    }

	public void jogoTeste(){
		hardStartGame();
		hardCodedSetup();
	}

	public void hardStartGame(){
		GameModel gameInstance = GameModel.getInstancia();	
		gameInstance.addJogador("Joana", "Azul");
		gameInstance.addJogador("Miguel", "Verde");
		gameInstance.addJogador("Murilo", "Vermelho");
		beginGame();
	}
	
	public void startGame(){
        instance_controller.janelaInicialController();
    }

	public void beginGame(){
        sorteiaObjetivos();
        distribuiCartasTerritorio();
        inicializaTerritorios();
        setOrdemJogada();
        for (Jogador jogador : jogadores){
            jogador.limpaMao();
        }
        instance_controller.janelaJogoController();
		passaVez();
    }

	public void hardCodedSetup() {
		/* 
        for (Jogador jogador : jogadores) {
            for (Territorio t : jogador.getTerritorios()) {

                Random random = new Random();
                int randomNumber = random.nextInt(5);
                t.alteraNumSoldados(randomNumber);
            }
        } */

		Jogador jogador1 = jogadores.get(numJogadorAtual);
		jogador1.addCarta(deckCartas.drawCard());
		jogador1.addCarta(deckCartas.drawCard());
		jogador1.addCarta(deckCartas.drawCard());
		jogador1.addCarta(deckCartas.drawCard());
		jogador1.addCarta(deckCartas.drawCard());
		jogador1.addCarta(deckCartas.drawCard());
		for (Territorio t : jogador1.getTerritorios()) {
                t.alteraNumSoldados(10);
            }
		Jogador jogador2 = jogadores.get((numJogadorAtual+1)%jogadores.size());
		jogador2.addCarta(deckCartas.drawCard());
		jogador2.addCarta(deckCartas.drawCard());
		jogador2.addCarta(deckCartas.drawCard());

		Jogador jogador3 = jogadores.get((numJogadorAtual+2)%jogadores.size());
		jogador3.addCarta(deckCartas.drawCard());
		jogador3.addCarta(deckCartas.drawCard());
		jogador3.addCarta(deckCartas.drawCard());

		instance_controller.voltaTerrioriosController();
    }

	public static void main(String[] args){
		GameModel gameInstance = GameModel.getInstancia();
		instance_controller = GameController.getInstanciaController();
		instance_controller.initGameController();
		System.out.println("Iniciando jogo...");
		gameInstance.startGame();
	}

}
