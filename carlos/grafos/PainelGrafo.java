package carlos.grafos;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * [RECURSO EXTRA SUGERIDO POR IA] - Painel de renderização visual em tempo real.
 */
public class PainelGrafo extends JPanel {
    private final Grafo grafo;
    private List<Vertice> caminhoDestacado;
    private final int RAIO_NO = 22;

    // Paleta combinada
    private final Color COLOR_BG = new Color(15, 23, 42);
    private final Color COLOR_ACCENT_BLUE = new Color(59, 130, 246);
    private final Color COLOR_ACCENT_GREEN = new Color(16, 185, 129);

    public PainelGrafo(Grafo grafo) {
        this.grafo = grafo;
        setBackground(COLOR_BG);
    }

    public void setCaminhoDestacado(List<Vertice> caminho) {
        this.caminhoDestacado = caminho;
        repaint();
    }

    public void limparDestaque() {
        this.caminhoDestacado = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        List<Vertice> vertices = grafo.getVertices();
        int totalNos = vertices.size();
        if (totalNos == 0) {
            g2.setColor(new Color(100, 116, 139));
            g2.setFont(new Font("SansSerif", Font.ITALIC, 14));
            String msg = "Nenhum nó inserido. Mapeie vértices ou carregue o mapa padrão.";
            g2.drawString(msg, getWidth() / 2 - g2.getFontMetrics().stringWidth(msg) / 2, getHeight() / 2);
            return;
        }

        int centroX = getWidth() / 2;
        int centroY = getHeight() / 2;
        int raioOrbita = Math.min(centroX, centroY) - 60;
        if (raioOrbita < 40) raioOrbita = 40;

        Point[] pontos = new Point[totalNos];
        for (int i = 0; i < totalNos; i++) {
            double angulo = 2 * Math.PI * i / totalNos - Math.PI / 2;
            pontos[i] = new Point(centroX + (int) (raioOrbita * Math.cos(angulo)), centroY + (int) (raioOrbita * Math.sin(angulo)));
        }

        int[][] matriz = grafo.getMatrizAdjacencia();

        // Desenha Arestas
        for (int i = 0; i < totalNos; i++) {
            for (int j = 0; j < totalNos; j++) {
                if (matriz[i][j] == 1) {
                    boolean pertenceAoCaminho = estaNoCaminhoDestacado(vertices.get(i), vertices.get(j));
                    if (pertenceAoCaminho) {
                        g2.setColor(COLOR_ACCENT_GREEN);
                        g2.setStroke(new BasicStroke(3.5f));
                    } else {
                        g2.setColor(new Color(71, 85, 105));
                        g2.setStroke(new BasicStroke(1.5f));
                    }

                    if (grafo.isDirecionado()) {
                        desenharSeta(g2, pontos[i].x, pontos[i].y, pontos[j].x, pontos[j].y);
                    } else if (i <= j) {
                        g2.drawLine(pontos[i].x, pontos[i].y, pontos[j].x, pontos[j].y);
                    }
                }
            }
        }

        // Desenha Círculos dos Vértices
        for (int i = 0; i < totalNos; i++) {
            Point p = pontos[i];
            Vertice v = vertices.get(i);

            g2.setColor(caminhoDestacado != null && caminhoDestacado.contains(v) ? COLOR_ACCENT_GREEN : COLOR_ACCENT_BLUE);
            g2.fillOval(p.x - RAIO_NO, p.y - RAIO_NO, 2 * RAIO_NO, 2 * RAIO_NO);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(p.x - RAIO_NO, p.y - RAIO_NO, 2 * RAIO_NO, 2 * RAIO_NO);

            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(v.getNome(), p.x - fm.stringWidth(v.getNome()) / 2, p.y + fm.getAscent() / 2 - 2);
        }
    }

    private boolean estaNoCaminhoDestacado(Vertice v1, Vertice v2) {
        if (caminhoDestacado == null || caminhoDestacado.size() < 2) return false;
        for (int k = 0; k < caminhoDestacado.size() - 1; k++) {
            if (caminhoDestacado.get(k).equals(v1) && caminhoDestacado.get(k + 1).equals(v2)) return true;
            if (!grafo.isDirecionado() && caminhoDestacado.get(k).equals(v2) && caminhoDestacado.get(k + 1).equals(v1)) return true;
        }
        return false;
    }

    private void desenharSeta(Graphics2D g2, int x1, int y1, int x2, int y2) {
        double dx = x2 - x1, dy = y2 - y1;
        double dist = Math.hypot(dx, dy);
        if (dist == 0) return;

        int sx = (int) (x1 + (dx * RAIO_NO / dist));
        int sy = (int) (y1 + (dy * RAIO_NO / dist));
        int ex = (int) (x2 - (dx * RAIO_NO / dist));
        int ey = (int) (y2 - (dy * RAIO_NO / dist));

        g2.drawLine(sx, sy, ex, ey);

        double angulo = Math.atan2(ey - sy, ex - sx);
        int[] xPts = {ex, (int) (ex - 10 * Math.cos(angulo - Math.PI/6)), (int) (ex - 10 * Math.cos(angulo + Math.PI/6))};
        int[] yPts = {ey, (int) (ey - 10 * Math.sin(angulo - Math.PI/6)), (int) (ey - 10 * Math.sin(angulo + Math.PI/6))};
        g2.fillPolygon(xPts, yPts, 3);
    }
}