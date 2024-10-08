package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para montículos mínimos (<i>min heaps</i>).
 */
public class MonticuloMinimo<T extends ComparableIndexable<T>>
    implements Coleccion<T>, MonticuloDijkstra<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Índice del iterador. */
        private int indice;

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            // Aquí va su código.
            return indice < elementos;
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            // Aquí va su código.
            if(elementos <= indice)
               throw new NoSuchElementException();
            return arbol[indice++];
        }
    }

    /* Clase estática privada para adaptadores. */
    private static class Adaptador<T  extends Comparable<T>>
        implements ComparableIndexable<Adaptador<T>> {

        /* El elemento. */
        private T elemento;
        /* El índice. */
        private int indice;

        /* Crea un nuevo comparable indexable. */
        public Adaptador(T elemento) {
            // Aquí va su código.
            this.elemento = elemento;
            indice = -1;
        }

        /* Regresa el índice. */
        @Override public int getIndice() {
            // Aquí va su código.
            return indice;
        }

        /* Define el índice. */
        @Override public void setIndice(int indice) {
            // Aquí va su código.
            this.indice = indice;
        }

        /* Compara un adaptador con otro. */
        @Override public int compareTo(Adaptador<T> adaptador) {
            // Aquí va su código.
            return elemento.compareTo(adaptador.elemento);
        }
    }

    /* El número de elementos en el arreglo. */
    private int elementos;
    /* Usamos un truco para poder utilizar arreglos genéricos. */
    private T[] arbol;

    /* Truco para crear arreglos genéricos. Es necesario hacerlo así por cómo
       Java implementa sus genéricos; de otra forma obtenemos advertencias del
       compilador. */
    @SuppressWarnings("unchecked") private T[] nuevoArreglo(int n) {
        return (T[])(new ComparableIndexable[n]);
    }

    /**
     * Constructor sin parámetros. Es más eficiente usar {@link
     * #MonticuloMinimo(Coleccion)} o {@link #MonticuloMinimo(Iterable,int)},
     * pero se ofrece este constructor por completez.
     */
    public MonticuloMinimo() {
        // Aquí va su código.
        arbol = nuevoArreglo(100);
    }

    /**
     * Constructor para montículo mínimo que recibe una colección. Es más barato
     * construir un montículo con todos sus elementos de antemano (tiempo
     * <i>O</i>(<i>n</i>)), que el insertándolos uno por uno (tiempo
     * <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param coleccion la colección a partir de la cuál queremos construir el
     *                  montículo.
     */
    public MonticuloMinimo(Coleccion<T> coleccion) {
        this(coleccion, coleccion.getElementos());
    }
 
    private void agregaAux(T elem, int indice){
        arbol[indice] = elem;
        elem.setIndice(indice);
    }

    private void monticuleaAbajo(int i){
        int hi = 2 * i + 1;
        int hd = 2 * i + 2;

        int menor = i; if (hi < elementos && arbol[hi].compareTo(arbol[menor]) < 0)
            menor = hi;
        if (hd < elementos && arbol[hd].compareTo(arbol[menor]) < 0)
            menor = hd;

        if (menor != i) {
            intercambia(i, menor);
            monticuleaAbajo(menor);
        }
    }
   
    private void intercambia(int x, int y) {
        T temp = arbol[x];
        arbol[x] = arbol[y];
        arbol[y] = temp;
        arbol[x].setIndice(x);
        arbol[y].setIndice(y);
    }

    /**
     * Constructor para montículo mínimo que recibe un iterable y el número de
     * elementos en el mismo. Es más barato construir un montículo con todos sus
     * elementos de antemano (tiempo <i>O</i>(<i>n</i>)), que el insertándolos
     * uno por uno (tiempo <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param iterable el iterable a partir de la cuál queremos construir el
     *                 montículo.
     * @param n el número de elementos en el iterable.
     */
    public MonticuloMinimo(Iterable<T> iterable, int n) {
        // Aquí va su código.
        arbol = nuevoArreglo(n);
        for(T e : iterable)
            agregaAux(e, elementos++);

        for(int i = n/2 - 1; i>=0 ; i--)
            monticuleaAbajo(i);
    }

    /**
     * Agrega un nuevo elemento en el montículo.
     * @param elemento el elemento a agregar en el montículo.
     */
    @Override public void agrega(T elemento) {
        // Aquí va su código.
        if (arbol.length == elementos) {
            T[] nuevoArbol = nuevoArreglo(elementos * 2);
            for (int i = 0; i < elementos; i++)
                nuevoArbol[i] = arbol[i];
            arbol = nuevoArbol;
        }

        agregaAux(elemento, elementos);
        elementos++;
        monticuleaArriba(elementos - 1);
    }

    private void monticuleaArriba(int i) {
        int padre = (i - 1)/2;

        if (i > 0 && arbol[i].compareTo(arbol[padre]) < 0) {
            intercambia(i, padre);
            monticuleaArriba(padre);
        }    
    }

    /**
     * Elimina el elemento mínimo del montículo.
     * @return el elemento mínimo del montículo.
     * @throws IllegalStateException si el montículo es vacío.d
     */
    @Override public T elimina() {
        // Aquí va su código.  
        if (elementos == 0)
            throw new IllegalStateException("El montículo es vacío.");

        T temp = arbol[0];
        intercambia(0, elementos - 1);
        arbol[elementos - 1].setIndice(-1);

        elementos--;
        monticuleaAbajo(0);

        return temp; 
    }

    /**
     * Elimina un elemento del montículo.
     * @param elemento a eliminar del montículo.
     */
    @Override public void elimina(T elemento) {
        // Aquí va su código.
        if (elemento.getIndice() < 0 || elemento.getIndice() >= elementos)
            return;

        int indice = elemento.getIndice();
        intercambia(indice, elementos - 1);
        arbol[elementos - 1].setIndice(-1);

        elementos--;

        if (indice < elementos)
            reordena(arbol[indice]); 
    }

    /**
     * Nos dice si un elemento está contenido en el montículo.
     * @param elemento el elemento que queremos saber si está contenido.
     * @return <code>true</code> si el elemento está contenido,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        // Aquí va su código.
        if (elemento.getIndice() < 0 || elemento.getIndice() >= elementos)
            return false;

        return arbol[elemento.getIndice()].equals(elemento);
    }

    /**
     * Nos dice si el montículo es vacío.
     * @return <code>true</code> si ya no hay elementos en el montículo,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean esVacia() {
        // Aquí va su código.
        return elementos == 0;
    }

    /**
     * Limpia el montículo de elementos, dejándolo vacío.
     */
    @Override public void limpia() {
        // Aquí va su código.
        elementos = 0;
    }

   /**
     * Reordena un elemento en el árbol.
     * @param elemento el elemento que hay que reordenar.
     */
    @Override public void reordena(T elemento){ 
        // Aquí va su código.
        int indice = elemento.getIndice();
        monticuleaArriba(indice);
        monticuleaAbajo(indice);
    }

    /**
     * Regresa el número de elementos en el montículo mínimo.
     * @return el número de elementos en el montículo mínimo.
     */
    @Override public int getElementos() {
        // Aquí va su código.
        return elementos;
    }

    /**
     * Regresa el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @param i el índice del elemento que queremos, en <em>in-order</em>.
     * @return el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @throws NoSuchElementException si i es menor que cero, o mayor o igual
     *         que el número de elementos.
     */
    @Override public T get(int i) {
        // Aquí va su código.
        if (i < 0 || i >= elementos)
            throw new NoSuchElementException("No existe el elemento.");

        return arbol[i];
    }

    /**
     * Regresa una representación en cadena del montículo mínimo.
     * @return una representación en cadena del montículo mínimo.
     */
    @Override public String toString() {
        // Aquí va su código.
        String texto = "";

        for (T elemento : arbol)
            texto += elemento.toString() + ", ";

        return texto;
    }

    /**
     * Nos dice si el montículo mínimo es igual al objeto recibido.
     * @param objeto el objeto con el que queremos comparar el montículo mínimo.
     * @return <code>true</code> si el objeto recibido es un montículo mínimo
     *         igual al que llama el método; <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") MonticuloMinimo<T> monticulo =
            (MonticuloMinimo<T>)objeto;
        // Aquí va su código.
        if (monticulo.elementos != elementos)
            return false;

        for (int i = 0; i < elementos; i++)
            if (!arbol[i].equals(monticulo.arbol[i]))
                return false;

        return true;
    }

    /**
     * Regresa un iterador para iterar el montículo mínimo. El montículo se
     * itera en orden BFS.
     * @return un iterador para iterar el montículo mínimo.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Ordena la colección usando HeapSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param coleccion la colección a ordenar.
     * @return una lista ordenada con los elementos de la colección.
     */
    public static <T extends Comparable<T>>
    Lista<T> heapSort(Coleccion<T> coleccion) {
        // Aquí va su código.
        Lista<Adaptador<T>> l1 = new Lista<>();
        Lista<T> l2 = new Lista<>();

        for (T e : coleccion)
            l1.agrega(new Adaptador<T>(e));

        MonticuloMinimo<Adaptador<T>> monticulo = new MonticuloMinimo<>(l1);

        while (!monticulo.esVacia()) {
            Adaptador<T> eliminado = monticulo.elimina();
            l2.agrega(eliminado.elemento);
        }

        return l2;
    }
}
