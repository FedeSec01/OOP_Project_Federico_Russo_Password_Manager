package com.securevault.composite;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Iterator per attraversamento in profondità (depth-first) del Composite Pattern.
 * Utilizza uno Stack per mantenere lo stato dell'attraversamento.
 * Permette di navigare tutto l'albero senza conoscere la struttura interna.
 */
public class CompositeIterator implements Iterator<VaultComponent> {
    // Stack di iterator per gestire l'attraversamento ricorsivo
    private final Stack<Iterator<VaultComponent>> stack = new Stack<>();
    
    // Cache per il prossimo elemento (lazy evaluation)
    private VaultComponent next = null;
    private boolean hasNextCached = false;

    /**
     * Costruttore che inizializza l'iterator con l'iterator della radice.
     * @param rootIterator iterator del nodo radice
     */
    public CompositeIterator(Iterator<VaultComponent> rootIterator) {
        if (rootIterator != null) {
            stack.push(rootIterator);
        }
    }

    /**
     * Verifica se ci sono altri elementi da iterare.
     * Usa lazy evaluation con cache per evitare calcoli ripetuti.
     * @return true se ci sono altri elementi
     */
    @Override
    public boolean hasNext() {
        if (hasNextCached) {
            return next != null;
        }
        
        // Cerca il prossimo elemento e lo mette in cache
        next = findNext();
        hasNextCached = true;
        return next != null;
    }

    /**
     * Restituisce il prossimo elemento dell'attraversamento.
     * @return prossimo VaultComponent
     * @throws NoSuchElementException se non ci sono più elementi
     */
    @Override
    public VaultComponent next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements");
        }
        
        VaultComponent result = next;
        next = null;              // Reset cache
        hasNextCached = false;    // Invalidate cache
        return result;
    }

    /**
     * Algoritmo di attraversamento depth-first usando Stack.
     * Logica core dell'iterator che gestisce la navigazione ricorsiva.
     * @return prossimo VaultComponent o null se finito
     */
    private VaultComponent findNext() {
        while (!stack.isEmpty()) {
            Iterator<VaultComponent> currentIterator = stack.peek();
            
            if (currentIterator.hasNext()) {
                VaultComponent component = currentIterator.next();
                
                // Se il componente ha figli, aggiungi il suo iterator allo stack
                // Questo implementa l'attraversamento depth-first
                try {
                    Iterator<VaultComponent> childIterator = component.iterator();
                    if (childIterator != null && childIterator.hasNext()) {
                        stack.push(childIterator);
                    }
                } catch (UnsupportedOperationException e) {
                    // Componente foglia (Credential) - ignora l'eccezione
                    // Questo è il comportamento atteso per le foglie
                }
                
                return component;
            } else {
                // Iterator corrente esaurito, rimuovilo dallo stack
                // Continua con l'iterator del livello superiore
                stack.pop();
            }
        }
        
        return null; // Attraversamento completato
    }
}