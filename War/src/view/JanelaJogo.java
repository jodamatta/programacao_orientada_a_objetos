package view;

import model.GameModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.imageio.ImageIO;
import java.awt.*;


public class JanelaJogo extends Frame{
    private Frame frame;
    private GameModel gameModel;
    private static JanelaInicial janelaInicial;
    private Button btnTerminaRodada;
    private Button btnTrocaCartas;
    private Button btnMover; 
    private Button btnSalvarJogo;
    private List<Button> btnTerritorios;
    private BufferedImage imagemDeFundo;
    Map<String,  int[]> dictTerritorioPosicao = new HashMap<>();

    public JanelaJogo() {
        try {
            imagemDeFundo = ImageIO.read(new File("War\\src\\view\\images\\war_tabuleiro_mapa copy.png"));
        } catch (IOException e) {
            e.printStackTrace();
        };
        frame = new Frame("Janela de Jogo do war");
        setSize(imagemDeFundo.getWidth(), imagemDeFundo.getHeight());
        setLayout(null); // Desabilita o layout manager para posicionamento manual dos botões
        initButtons();   // Inicializa os botões
        setVisible(true);
    }

    private void initButtons() {
        btnTerminaRodada = new Button("Terminar Rodada");
        btnTerminaRodada.setBounds(890, 700, 120, 30); // Define posição e tamanho do botão
        add(btnTerminaRodada);

        btnTrocaCartas = new Button("Trocar Cartas");
        btnTrocaCartas.setBounds(765, 700, 120, 30);
        add(btnTrocaCartas);

        btnMover = new Button("Mover Exército");
        btnMover.setBounds(640, 700, 120, 30);
        add(btnMover);

        btnSalvarJogo = new Button("Salvar Jogo");
        btnSalvarJogo.setBounds(10, 30, 120, 30);
        add(btnSalvarJogo);
    }

    {
        dictTerritorioPosicao.put("ARGENTINA", new int[]{0, 0});
        dictTerritorioPosicao.put("BRASIL", new int[]{0, 0});
        dictTerritorioPosicao.put("PERU", new int[]{0, 0});
        dictTerritorioPosicao.put("VENEZUELA", new int[]{0, 0});
        dictTerritorioPosicao.put("MEXICO", new int[]{0, 0});
        dictTerritorioPosicao.put("CALIFORNIA", new int[]{0, 0});
        dictTerritorioPosicao.put("TEXAS", new int[]{0, 0});
        dictTerritorioPosicao.put("NOVAYORK", new int[]{0, 0});
        dictTerritorioPosicao.put("VANCOUVER", new int[]{0, 0});
        dictTerritorioPosicao.put("ALASCA", new int[]{0, 0});
        dictTerritorioPosicao.put("CALGARY", new int[]{0, 0});
        dictTerritorioPosicao.put("GROELANDIA", new int[]{0, 0});
        dictTerritorioPosicao.put("QUEBEC", new int[]{0, 0});
        dictTerritorioPosicao.put("REINO_UNIDO", new int[]{0, 0});
        dictTerritorioPosicao.put("ESPANHA", new int[]{0, 0});
        dictTerritorioPosicao.put("FRANCA", new int[]{0, 0});
        dictTerritorioPosicao.put("ITALIA", new int[]{0, 0});
        dictTerritorioPosicao.put("SUECIA", new int[]{0, 0});
        dictTerritorioPosicao.put("POLONIA", new int[]{0, 0});
        dictTerritorioPosicao.put("ROMENIA", new int[]{0, 0});
        dictTerritorioPosicao.put("UCRANIA", new int[]{0, 0});
        dictTerritorioPosicao.put("EGITO", new int[]{0, 0});
        dictTerritorioPosicao.put("ARGELIA", new int[]{0, 0});
        dictTerritorioPosicao.put("NIGERIA", new int[]{0, 0});
        dictTerritorioPosicao.put("ANGOLA", new int[]{0, 0});
        dictTerritorioPosicao.put("AFRICA_DO_SUL", new int[]{0, 0});
        dictTerritorioPosicao.put("SOMALIA", new int[]{0, 0});
        dictTerritorioPosicao.put("ARABIA_SAUDITA", new int[]{0, 0});
        dictTerritorioPosicao.put("JORDANIA", new int[]{0, 0});
        dictTerritorioPosicao.put("SIRIA", new int[]{0, 0});
        dictTerritorioPosicao.put("IRAQUE", new int[]{0, 0});
        dictTerritorioPosicao.put("IRA", new int[]{0, 0});
        dictTerritorioPosicao.put("PAQUISTAO", new int[]{0, 0});
        dictTerritorioPosicao.put("TURQUIA", new int[]{0, 0});
        dictTerritorioPosicao.put("LETONIA", new int[]{0, 0});
        dictTerritorioPosicao.put("ESTONIA", new int[]{0, 0});
        dictTerritorioPosicao.put("RUSSIA", new int[]{0, 0});
        dictTerritorioPosicao.put("SIBERIA", new int[]{0, 0});
        dictTerritorioPosicao.put("CAZAQUISTAO", new int[]{0, 0});
        dictTerritorioPosicao.put("MONGOLIA", new int[]{0, 0});
        dictTerritorioPosicao.put("JAPAO", new int[]{0, 0});
        dictTerritorioPosicao.put("COREIA_DO_NORTE", new int[]{0, 0});
        dictTerritorioPosicao.put("COREIA_DO_SUL", new int[]{0, 0});
        dictTerritorioPosicao.put("CHINA", new int[]{0, 0});
        dictTerritorioPosicao.put("INDIA", new int[]{0, 0});
        dictTerritorioPosicao.put("BANGLADESH", new int[]{0, 0});
        dictTerritorioPosicao.put("TAILANDIA", new int[]{0, 0});
        dictTerritorioPosicao.put("INDONESIA", new int[]{0, 0});
        dictTerritorioPosicao.put("NOVAZELANDIA", new int[]{0, 0});
        dictTerritorioPosicao.put("AUSTRALIA", new int[]{0, 0});
        dictTerritorioPosicao.put("PERTH", new int[]{0, 0});
    }

    private void initTerritorios() {
        btnTerritorios = new ArrayList<>();
        Set<String> nomesTerritorios = dictTerritorioPosicao.keySet();
        
        for (String nome : nomesTerritorios) {
            int[] coordenadas = dictTerritorioPosicao.get(nome);
            if (coordenadas != null) {
                Button btnTerritorio = new Button();
                btnTerritorio.setBounds(coordenadas[0], coordenadas[1], 30, 30);
                
                // Implemente a lógica para obter a cor do jogador e o número de exércitos
                // associados a cada território usando o nome
        
                btnTerritorios.add(btnTerritorio);
                add(btnTerritorio);
            }
        }
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(imagemDeFundo, 0, 0, this);
    }

    public static void main(String[] args) {
        new JanelaJogo();
    }
}
