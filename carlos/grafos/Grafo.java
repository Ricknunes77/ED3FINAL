package carlos.grafos;

import java.util.*;

/**
 * Motor lógico do Grafo utilizando Matriz de Adjacência [REQUISITO 3.1].
 */
public class Grafo {
    private final int MAX_VERTICES = 50;
    private final int[][] matrizAdjacencia;
    private final List<Vertice> vertices;
    private final Map<String, Integer> mapaIndices;
    private boolean direcionado;

    public Grafo(boolean direcionado) {
        this.matrizAdjacencia = new int[MAX_VERTICES][MAX_VERTICES];
        this.vertices = new ArrayList<>();
        this.mapaIndices = new HashMap<>();
        this.direcionado = direcionado;
    }

    public boolean isDirecionado() { return direcionado; }
    public void setDirecionado(boolean direcionado) { this.direcionado = direcionado; }
    public List<Vertice> getVertices() { return vertices; }
    public int[][] getMatrizAdjacencia() { return matrizAdjacencia; }

    public void limpar() {
        this.vertices.clear();
        this.mapaIndices.clear();
        for (int i = 0; i < MAX_VERTICES; i++) {
            Arrays.fill(matrizAdjacencia[i], 0);
        }
    }

    public void adicionarVertice(String nome) throws GrafoException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new GrafoException("O nome do vértice não pode ser vazio.");
        }
        Vertice v = new Vertice(nome);
        if (mapaIndices.containsKey(v.getNome())) {
            throw new GrafoException("O Vértice '" + v.getNome() + "' já existe no grafo.");
        }
        if (vertices.size() >= MAX_VERTICES) {
            throw new GrafoException("Capacidade máxima de 50 vértices atingida.");
        }
        vertices.add(v);
        mapaIndices.put(v.getNome(), vertices.size() - 1);
    }

    public void adicionarAresta(String nomeOrigem, String nomeDestino) throws GrafoException {
        String orig = nomeOrigem.trim().toUpperCase();
        String dest = nomeDestino.trim().toUpperCase();

        if (!mapaIndices.containsKey(orig) || !mapaIndices.containsKey(dest)) {
            throw new GrafoException("Vértice de origem ou destino não mapeado.");
        }

        int indiceOrigem = mapaIndices.get(orig);
        int indiceDestino = mapaIndices.get(dest);

        matrizAdjacencia[indiceOrigem][indiceDestino] = 1;
        if (!direcionado) {
            matrizAdjacencia[indiceDestino][indiceOrigem] = 1;
        }
    }

    public boolean existeVertice(String nome) {
        return mapaIndices.containsKey(nome.trim().toUpperCase());
    }

    // [CÓDIGO SUGERIDO POR IA - BFS COM FILA AUXILIAR]
    public List<Vertice> buscarEmLargura(String nomeOrigem, String nomeDestino) {
        Vertice origem = vertices.get(mapaIndices.get(nomeOrigem.trim().toUpperCase()));
        Vertice destino = vertices.get(mapaIndices.get(nomeDestino.trim().toUpperCase()));

        Queue<Vertice> fila = new LinkedList<>();
        Set<Vertice> visitados = new HashSet<>(); // Evita loops infinitos [REQUISITO 3.2.6]
        Map<Vertice, Vertice> antecessores = new HashMap<>();

        fila.add(origem);
        visitados.add(origem);
        boolean encontrado = false;

        while (!fila.isEmpty()) {
            Vertice atual = fila.poll();

            if (atual.equals(destino)) {
                encontrado = true;
                break;
            }

            int idxAtual = mapaIndices.get(atual.getNome());
            for (int i = 0; i < vertices.size(); i++) {
                if (matrizAdjacencia[idxAtual][i] == 1) {
                    Vertice vizinho = vertices.get(i);
                    if (!visitados.contains(vizinho)) {
                        visitados.add(vizinho);
                        antecessores.put(vizinho, atual);
                        fila.add(vizinho);
                    }
                }
            }
        }
        return encontrado ? reconstruirCaminho(antecessores, destino) : null;
    }

    // [CÓDIGO SUGERIDO POR IA - DFS RECURSIVA]
    public List<Vertice> buscarEmProfundidade(String nomeOrigem, String nomeDestino) {
        Vertice origem = vertices.get(mapaIndices.get(nomeOrigem.trim().toUpperCase()));
        Vertice destino = vertices.get(mapaIndices.get(nomeDestino.trim().toUpperCase()));

        Set<Vertice> visitados = new HashSet<>();
        Map<Vertice, Vertice> antecessores = new HashMap<>();

        boolean encontrado = dfsRecursiva(origem, destino, visitados, antecessores);
        return encontrado ? reconstruirCaminho(antecessores, destino) : null;
    }

    private boolean dfsRecursiva(Vertice atual, Vertice destino, Set<Vertice> visitados, Map<Vertice, Vertice> antecessores) {
        visitados.add(atual);

        if (atual.equals(destino)) return true;

        int idxAtual = mapaIndices.get(atual.getNome());
        for (int i = 0; i < vertices.size(); i++) {
            if (matrizAdjacencia[idxAtual][i] == 1) {
                Vertice vizinho = vertices.get(i);
                if (!visitados.contains(vizinho)) {
                    antecessores.put(vizinho, atual);
                    if (dfsRecursiva(vizinho, destino, visitados, antecessores)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private List<Vertice> reconstruirCaminho(Map<Vertice, Vertice> antecessores, Vertice destino) {
        List<Vertice> caminho = new LinkedList<>();
        Vertice atual = destino;
        while (atual != null) {
            caminho.add(0, atual);
            atual = antecessores.get(atual);
        }
        return caminho;
    }

    public String formatarCaminho(List<Vertice> caminho) {
        if (caminho == null || caminho.isEmpty()) {
            return "Caminho não encontrado entre os nós informados."; // Exigência de aviso claro [REQUISITO 2.16]
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < caminho.size(); i++) {
            sb.append(caminho.get(i).getNome());
            if (i < caminho.size() - 1) {
                sb.append(" para "); // Formato exigido: A para C para B [REQUISITO 2.15]
            }
        }
        return sb.toString();
    }
}