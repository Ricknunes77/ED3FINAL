package carlos.grafos;

/**
 * Exceção customizada para interceptar regras de negócio do Grafo [REQUISITO 3.3.9].
 */
public class GrafoException extends Exception {
    public GrafoException(String mensagem) {
        super(mensagem);
    }
}