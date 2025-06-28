package com.securevault.repository;

import com.securevault.model.Credential;
import com.securevault.observer.VaultSubject;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Repository che combina múltiples pattern:
 * - Repository Pattern: astrae l'accesso ai dati
 * - Observer Pattern: notifica cambiamenti (estende VaultSubject)  
 * - Iterator Pattern: implementa Iterable per foreach
 * - Uso intensivo di Stream API e Collections Framework
 */
public class VaultRepository extends VaultSubject implements Iterable<Credential> {

    // Struttura dati: Map per organizzazione gerarchica per categoria
    private final Map<String, List<Credential>> groupedCredentials;

    /**
     * Costruttore che inizializza la struttura dati.
     */
    public VaultRepository() {
        this.groupedCredentials = new HashMap<>();
    }

    // === OPERAZIONI CRUD CON OBSERVER INTEGRATION ===

    /**
     * Aggiunge una credenziale notificando gli observer.
     * Pattern: Repository + Observer
     * 
     * @param category categoria di organizzazione
     * @param credential credenziale da aggiungere
     */
    public void addCredential(String category, Credential credential) {
        // computeIfAbsent: crea lista se categoria non esiste
        groupedCredentials.computeIfAbsent(category, k -> new ArrayList<>()).add(credential);
        
        // OBSERVER PATTERN: notifica automatica
        notifyCredentialAdded(credential, category);
    }

    /**
     * Rimuove una credenziale con cleanup automatico e notifiche.
     * 
     * @param category categoria della credenziale
     * @param credential credenziale da rimuovere
     */
    public void removeCredential(String category, Credential credential) {
        List<Credential> creds = groupedCredentials.get(category);
        if (creds != null && creds.remove(credential)) {
            // CLEANUP: rimuovi categoria se vuota
            if (creds.isEmpty()) {
                groupedCredentials.remove(category);
            }
            
            // OBSERVER PATTERN: notifica rimozione
            notifyCredentialRemoved(credential, category);
        }
    }

    /**
     * Modifica una credenziale esistente.
     * Pattern: Repository + Observer
     * 
     * @param category categoria della credenziale
     * @param oldCredential credenziale da sostituire
     * @param newCredential nuova credenziale
     */
    public void modifyCredential(String category, Credential oldCredential, Credential newCredential) {
        List<Credential> creds = groupedCredentials.get(category);
        if (creds != null) {
            int index = creds.indexOf(oldCredential);
            if (index != -1) {
                // SOSTITUZIONE IN-PLACE
                creds.set(index, newCredential);
                
                // OBSERVER PATTERN: notifica modifica con old e new
                notifyCredentialModified(oldCredential, newCredential, category);
            }
        }
    }

    /**
     * Svuota completamente il vault.
     */
    public void clearAll() {
        groupedCredentials.clear();
        // OBSERVER PATTERN: notifica svuotamento
        notifyVaultCleared();
    }

    // === ITERATOR PATTERN IMPLEMENTATION ===

    /**
     * Implementa Iterable per supportare enhanced for-loops.
     * Usa Stream API per flatten delle liste annidate.
     * 
     * @return iterator su tutte le credenziali
     */
    @Override
    public Iterator<Credential> iterator() {
        return groupedCredentials.values().stream()
                .flatMap(Collection::stream)  // Flatten Map<String, List<Credential>> -> Stream<Credential>
                .iterator();
    }

    /**
     * Conta totale credenziali usando Stream API.
     * 
     * @return numero totale di credenziali
     */
    public int countAll() {
        return groupedCredentials.values().stream()
                .mapToInt(List::size)        // Map List -> int (size)
                .sum();                      // Reduce to sum
    }

    // === STREAM API E FUNZIONALITÀ AVANZATE ===

    /**
     * Ricerca per servizio con case-insensitive matching.
     * Pattern: Repository + Stream API + Lambda
     * 
     * @param query termine di ricerca
     * @return credenziali che matchano
     */
    public List<Credential> searchByService(String query) {
        return groupedCredentials.values().stream()
                .flatMap(Collection::stream)                           // Flatten collections
                .filter(cred -> cred.getService()                     // Lambda predicate
                    .toLowerCase()
                    .contains(query.toLowerCase()))                    // Case-insensitive
                .collect(Collectors.toList());                         // Collect results
    }

    /**
     * Ricerca per username con Stream API.
     * 
     * @param username username da cercare
     * @return credenziali matchanti
     */
    public List<Credential> searchByUsername(String username) {
        return groupedCredentials.values().stream()
                .flatMap(Collection::stream)
                .filter(cred -> cred.getUsername()
                    .toLowerCase()
                    .contains(username.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Filtro personalizzato usando Predicate funzionale.
     * Permette al client di definire logiche di filtro custom.
     * 
     * @param predicate condizione di filtro (lambda o method reference)
     * @return credenziali che soddisfano il predicato
     */
    public List<Credential> filterCredentials(Predicate<Credential> predicate) {
        return groupedCredentials.values().stream()
                .flatMap(Collection::stream)
                .filter(predicate)                    // Apply custom predicate
                .collect(Collectors.toList());
    }

    /**
     * Raggruppa credenziali per servizio usando Collectors.groupingBy.
     * Dimostra uso avanzato di Stream API collectors.
     * 
     * @return Map servizio -> lista credenziali per quel servizio
     */
    public Map<String, List<Credential>> groupByService() {
        return groupedCredentials.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(Credential::getService));  // Method reference
    }

    /**
     * Rileva credenziali duplicate (stesso servizio + username).
     * Combina groupingBy con filtering per logica complessa.
     * 
     * @return lista delle credenziali duplicate
     */
    public List<Credential> findDuplicates() {
        // Crea chiave composita servizio:username
        Map<String, List<Credential>> grouped = groupedCredentials.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(cred -> 
                    cred.getService() + ":" + cred.getUsername()));     // Composite key
        
        // Filtra gruppi con size > 1 e flatten
        return grouped.values().stream()
                .filter(list -> list.size() > 1)           // Keep only duplicates
                .flatMap(Collection::stream)               // Flatten back to credentials
                .collect(Collectors.toList());
    }

    /**
     * Statistiche per categoria usando Collectors avanzati.
     * 
     * @return Map categoria -> numero credenziali
     */
    public Map<String, Integer> getCategoryStatistics() {
        return groupedCredentials.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,                           // Key mapper: categoria
                    entry -> entry.getValue().size()            // Value mapper: size lista
                ));
    }

    /**
     * Top categorie più popolate con sorting e limiting.
     * Dimostra pipeline complessa di Stream API.
     * 
     * @param limit numero massimo di categorie da restituire
     * @return lista ordinata delle categorie più popolate
     */
    public List<String> getTopCategories(int limit) {
        return groupedCredentials.entrySet().stream()
                .sorted(Map.Entry.<String, List<Credential>>comparingByValue(
                    (a, b) -> Integer.compare(b.size(), a.size())))    // Sort by size DESC
                .limit(limit)                                          // Take top N
                .map(Map.Entry::getKey)                               // Extract category names
                .collect(Collectors.toList());
    }

    /**
     * Verifica esistenza con predicato usando anyMatch.
     * Short-circuiting operation per performance.
     * 
     * @param predicate condizione da verificare
     * @return true se almeno una credenziale soddisfa la condizione
     */
    public boolean hasCredentialMatching(Predicate<Credential> predicate) {
        return groupedCredentials.values().stream()
                .flatMap(Collection::stream)
                .anyMatch(predicate);                      // Short-circuit on first match
    }

    // === GETTER CON DEFENSIVE COPYING ===

    /**
     * Restituisce categorie disponibili.
     * 
     * @return Set delle categorie (defensive copy)
     */
    public Set<String> getCategories() {
        return groupedCredentials.keySet();  // KeySet è già una view, non serve copying
    }

    /**
     * Restituisce credenziali per categoria.
     * 
     * @param category categoria richiesta
     * @return lista credenziali (defensive copy)
     */
    public List<Credential> getByCategory(String category) {
        return groupedCredentials.getOrDefault(category, Collections.emptyList());
    }
}