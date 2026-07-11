package carlos.grafos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Painel Supervisor / Frame Principal da Aplicação Swing [REQUISITO 3.3].
 */
public class TelaPrincipal extends JFrame {
    private final Grafo grafo;
    private PainelGrafo painelGrafo;

    private final Color COLOR_BG = new Color(15, 23, 42);
    private final Color COLOR_CARD = new Color(30, 41, 59);
    private final Color COLOR_ACCENT_BLUE = new Color(59, 130, 246);
    private final Color COLOR_ACCENT_GREEN = new Color(16, 185, 129);
    private final Color COLOR_TEXT_LIGHT = new Color(249, 250, 251);

    private JTextField txtVertice, txtOrigemAresta, txtDestinoAresta, txtDe, txtPara;
    private JTextArea txtResultado;
    private JRadioButton rbNaoDirecionado, rbDirecionado;

    public TelaPrincipal() {
        this.grafo = new Grafo(false);
        configurarJanela();
        inicializarUI();
        this.setVisible(true);
    }

    private void configurarJanela() {
        setTitle("IFMA | ADS - Localizador & Visualizador de Caminhos em Grafos (BFS/DFS)");
        setSize(1240, 840); // Aumentamos um pouco a largura e altura aqui
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
    }
    private void inicializarUI() {
        setLayout(new BorderLayout(20, 20));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(25, 25, 25, 25));

        // Painel que conterá todos os cards da esquerda
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_BG);

        // Adiciona os cards ao painel lateral
        sidebar.add(criarCardConfiguracao());
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(criarCardControle("GERENCIAR VÉRTICES", "Inserir Novo Nó:", txtVertice = new JTextField(), "Adicionar Vértice", COLOR_ACCENT_BLUE, this::adicionarVertice));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(criarCardArestas());
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(criarCardBusca());

        // CORREÇÃO: Coloca a barra lateral dentro de um scroll para garantir que o card de busca apareça!
        JScrollPane scrollSidebar = new JScrollPane(sidebar);
        scrollSidebar.setBorder(null);
        scrollSidebar.setOpaque(false);
        scrollSidebar.getViewport().setOpaque(false);
        scrollSidebar.setPreferredSize(new Dimension(380, 0));
        scrollSidebar.getVerticalScrollBar().setUnitIncrement(16); // Rolagem suave

        add(scrollSidebar, BorderLayout.WEST);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(COLOR_CARD);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 1), new EmptyBorder(15, 15, 15, 15)));

        JLabel lblRes = new JLabel("MONITOR VISUAL E CONSOLE DE EXECUÇÃO");
        lblRes.setForeground(COLOR_ACCENT_GREEN); lblRes.setFont(new Font("SansSerif", Font.BOLD, 13));
        mainPanel.add(lblRes, BorderLayout.NORTH);

        txtResultado = new JTextArea();
        txtResultado.setBackground(new Color(15, 23, 42));
        txtResultado.setForeground(new Color(187, 247, 208));
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtResultado.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollConsole = new JScrollPane(txtResultado);

        painelGrafo = new PainelGrafo(grafo);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, painelGrafo, scrollConsole);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.7);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel criarCardConfiguracao() {
        JPanel card = new JPanel(new GridLayout(0, 1, 5, 5));
        card.setBackground(COLOR_CARD); card.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel lbl = new JLabel("PROPRIEDADES DO GRAFO");
        lbl.setForeground(COLOR_TEXT_LIGHT); lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        card.add(lbl);

        rbNaoDirecionado = new JRadioButton("Não-Direcionado (Bidirecional)", true);
        rbDirecionado = new JRadioButton("Direcionado (Sentido Único)", false);
        ButtonGroup group = new ButtonGroup(); group.add(rbNaoDirecionado); group.add(rbDirecionado);
        rbNaoDirecionado.setBackground(COLOR_CARD); rbNaoDirecionado.setForeground(Color.LIGHT_GRAY);
        rbDirecionado.setBackground(COLOR_CARD); rbDirecionado.setForeground(Color.LIGHT_GRAY);

        card.add(rbNaoDirecionado); card.add(rbDirecionado);

        rbNaoDirecionado.addActionListener(e -> { grafo.setDirecionado(false); painelGrafo.repaint(); });
        rbDirecionado.addActionListener(e -> { grafo.setDirecionado(true); painelGrafo.repaint(); });

        JButton btnLoad = estilizarBotao("CARREGAR MAPA DE EXEMPLO", COLOR_ACCENT_BLUE);
        btnLoad.addActionListener(e -> carregarGrafoExemplo());
        card.add(btnLoad);
        return card;
    }

    private JPanel criarCardControle(String titulo, String label, JTextField field, String btnText, Color btnColor, Runnable acao) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(COLOR_CARD); card.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel(titulo); lblTitle.setForeground(COLOR_TEXT_LIGHT); lblTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridy = 0; card.add(lblTitle, gbc);
        JLabel lblField = new JLabel(label); lblField.setForeground(new Color(148, 163, 184));
        gbc.gridy = 1; gbc.insets = new Insets(8, 0, 4, 0); card.add(lblField, gbc);
        estilizarField(field); gbc.gridy = 2; gbc.insets = new Insets(0, 0, 0, 0); card.add(field, gbc);

        JButton btn = estilizarBotao(btnText, btnColor); btn.addActionListener(e -> acao.run());
        gbc.gridy = 3; gbc.insets = new Insets(10, 0, 0, 0); card.add(btn, gbc);
        return card;
    }

    private JPanel criarCardArestas() {
        JPanel card = new JPanel(new GridLayout(0, 1, 4, 4));
        card.setBackground(COLOR_CARD); card.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel lbl = new JLabel("CONECTAR VÉRTICES (ARESTA)");
        lbl.setForeground(COLOR_TEXT_LIGHT); lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        card.add(lbl);

        txtOrigemAresta = new JTextField(); estilizarField(txtOrigemAresta);
        txtDestinoAresta = new JTextField(); estilizarField(txtDestinoAresta);

        card.add(new JLabel("Nó Origem:") {{ setForeground(Color.GRAY); }}); card.add(txtOrigemAresta);
        card.add(new JLabel("Nó Destino:") {{ setForeground(Color.GRAY); }}); card.add(txtDestinoAresta);

        JButton btn = estilizarBotao("Estabelecer Conexão", COLOR_ACCENT_BLUE);
        btn.addActionListener(e -> adicionarAresta());
        card.add(btn);
        return card;
    }

    private JPanel criarCardBusca() {
        JPanel card = new JPanel(new GridLayout(0, 1, 5, 5));
        card.setBackground(new Color(17, 24, 39));
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_ACCENT_GREEN, 1), new EmptyBorder(15, 15, 15, 15)));

        JLabel lbl = new JLabel("RESOLVER TRAJETÓRIA (A ➔ B)");
        lbl.setForeground(COLOR_ACCENT_GREEN); lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        card.add(lbl);

        txtDe = new JTextField(); estilizarField(txtDe);
        txtPara = new JTextField(); estilizarField(txtPara);

        card.add(new JLabel("Ponto de Origem (A):") {{ setForeground(Color.GRAY); }}); card.add(txtDe);
        card.add(new JLabel("Ponto de Destino (B):") {{ setForeground(Color.GRAY); }}); card.add(txtPara);

        JButton btn = estilizarBotao("RESOLVER TRAJETÓRIA", COLOR_ACCENT_GREEN);
        btn.addActionListener(e -> executarBuscas()); // Chama o método com o nome unificado
        card.add(btn);
        return card;
    }

    private JButton estilizarBotao(String texto, Color baseColor) {
        JButton btn = new JButton(texto); btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setBackground(baseColor); btn.setForeground(Color.WHITE); btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(baseColor.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(baseColor); }
        });
        return btn;
    }

    private void estilizarField(JTextField f) {
        f.setBackground(new Color(15, 23, 42)); f.setForeground(Color.WHITE); f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)), new EmptyBorder(6, 10, 6, 10)));
    }

    private void adicionarVertice() {
        try {
            String nome = txtVertice.getText();
            grafo.adicionarVertice(nome);
            txtResultado.append("✔️ [NÓ] Vértice '" + nome.toUpperCase() + "' inserido com sucesso.\n");
            txtVertice.setText("");
            painelGrafo.repaint();
        } catch (GrafoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adicionarAresta() {
        try {
            String o = txtOrigemAresta.getText();
            String d = txtDestinoAresta.getText();
            grafo.adicionarAresta(o, d);
            txtResultado.append("🔗 [ARESTA] Link criado: " + o.toUpperCase() + " ➔ " + d.toUpperCase() + "\n");
            txtOrigemAresta.setText(""); txtDestinoAresta.setText("");
            painelGrafo.repaint();
        } catch (GrafoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarGrafoExemplo() {
        grafo.limpar(); painelGrafo.limparDestaque();
        txtResultado.setText("♻️ Carregando grafo padrão de testes...\n");
        try {
            String[] nos = {"A", "B", "C", "D", "E", "F"};
            for (String n : nos) grafo.adicionarVertice(n);
            grafo.adicionarAresta("A", "B"); grafo.adicionarAresta("A", "C");
            grafo.adicionarAresta("B", "D"); grafo.adicionarAresta("C", "D");
            grafo.adicionarAresta("D", "E"); grafo.adicionarAresta("E", "F");
            txtResultado.append("✅ Grafo demonstrativo injetado com sucesso!\n\n");
            painelGrafo.repaint();
        } catch (GrafoException e) {
            txtResultado.append("❌ Falha ao carregar modelo.\n");
        }
    }

    // NOME UNIFICADO PARA EVITAR ERROS DE ASSINATURA
    private void executarBuscas() {
        try {
            String de = txtDe.getText().trim().toUpperCase();
            String para = txtPara.getText().trim().toUpperCase();

            if (de.isEmpty() || para.isEmpty()) {
                throw new GrafoException("Por favor, insira os nós de Origem e Destino.");
            }

            if (!grafo.existeVertice(de) || !grafo.existeVertice(para)) {
                throw new GrafoException("Os nós digitados não existem no grafo atualmente.\nCertifique-se de adicioná-los ou clicar em 'Carregar Mapa de Exemplo'.");
            }

            List<Vertice> rotaBFS = grafo.buscarEmLargura(de, para);
            List<Vertice> rotaDFS = grafo.buscarEmProfundidade(de, para);

            txtResultado.append(" BUSCA REALIZADA (" + de + " até " + para + "):\n");
            txtResultado.append("   • Caminho BFS (Largura): " + grafo.formatarCaminho(rotaBFS) + "\n");
            txtResultado.append("   • Caminho DFS (Profundidade): " + grafo.formatarCaminho(rotaDFS) + "\n");
            txtResultado.append("------------------------------------------------------------------------\n");

            if (rotaBFS != null) {
                painelGrafo.setCaminhoDestacado(rotaBFS);
            } else {
                painelGrafo.limparDestaque();
            }

        } catch (GrafoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso do Sistema", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro interno do Java: " + ex.toString(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaPrincipal::new);
    }
}