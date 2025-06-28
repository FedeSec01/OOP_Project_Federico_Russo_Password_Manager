package com.securevault;

import com.securevault.composite.FolderComposite;
import com.securevault.composite.VaultComponent;
import com.securevault.factory.CredentialFactory;
import com.securevault.model.Credential;
import com.securevault.observer.VaultNotificationService;
import com.securevault.repository.VaultRepository;
import com.securevault.security.CryptoManager;
import com.securevault.security.EncryptionStrategy;
import com.securevault.security.MasterPasswordManager;
import com.securevault.service.SecureStorageService;
import com.securevault.util.CSVExportTask;
import com.securevault.util.InputSanitizer;
import com.securevault.util.LogConfig;
import com.securevault.util.ReflectionUtil;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

/**
 * MAIN APPLICATION - Point of Entry & Pattern Orchestration
 * 
 * Questa classe rappresenta il punto di ingresso dell'applicazione e dimostra
 * l'integrazione di TUTTI i design pattern richiesti dal progetto:
 * 
 * PATTERN:
 * 1. Factory Pattern - CredentialFactory per creazione oggetti
 * 2. Composite Pattern - FolderComposite per struttura ad albero  
 * 3. Iterator Pattern - Navigazione della struttura composite
 * 4. Observer Pattern - VaultNotificationService per eventi real-time
 * 5. Strategy Pattern - EncryptionStrategy intercambiabile via Reflection
 * 6. Exception Shielding - Gestione sicura degli errori
 * 
 * TECNOLOGIE AVANZATE:
 * - Stream API & Lambda Expressions (ricerca avanzata)
 * - Multithreading (export CSV asincrono)
 * - Reflection (caricamento dinamico strategie)
 * - Java I/O (gestione file sicura)
 * - Logging (audit trail completo)
 * - Collections Framework & Generics
 * 
 * SICUREZZA:
 * - Input Sanitization per prevenire injection
 * - Master password con hashing SHA-256
 * - Crittografia AES-256-GCM delle credenziali
 * - Exception shielding per information leakage
 * - Auto-hide password per sicurezza visiva
 */
public class MainApp {
    
    // === LOGGING INFRASTRUCTURE ===
    private static final Logger logger = Logger.getLogger(MainApp.class.getName());
    
    // === CONFIGURAZIONE FILE (No hardcoded secrets!) ===
    private static final String STORAGE_PATH = "vault.enc";    // File credenziali cifrate
    private static final String KEY_PATH = "vault.key";       // File chiave crittografica
    private static final String MASTER_PATH = "master.hash";  // File hash master password

    /**
     * METODO PRINCIPALE - Orchestrazione completa del sistema
     * 
     * FLUSSO DI ESECUZIONE:
     * 1. INIT: Setup logging e configurazione
     * 2. AUTH: Autenticazione master password con retry limit
     * 3. CRYPTO: Setup sistema crittografico con Strategy Pattern
     * 4. PATTERNS: Inizializzazione Observer, Factory, Composite
     * 5. DATA: Caricamento credenziali esistenti
     * 6. UI: Loop principale interfaccia utente
     * 7. CLEANUP: Gestione chiusura e statistiche
     */
    public static void main(String[] args) {
        
        // === FASE 1: INIZIALIZZAZIONE SISTEMA ===
        
        // Configura sistema di logging centralizzato per tutto il vault
        LogConfig.configure();
        setupLogger();  // Logger specifico per MainApp
        logger.info("=== AVVIO SECUREVAULT ===");

        // Try-with-resources per gestione automatica Scanner
        try (Scanner scanner = new Scanner(System.in)) {
            
            // === FASE 2: AUTENTICAZIONE MASTER PASSWORD ===
            
            File masterFile = new File(MASTER_PATH);
            
            // FIRST-TIME SETUP: Se non esiste master password, creala
            if (!masterFile.exists()) {
                System.out.print("Imposta una nuova master password: ");
                String newPwd = scanner.nextLine().trim();
                MasterPasswordManager.saveMasterPassword(newPwd, MASTER_PATH);
                System.out.println("Master password salvata.");
                logger.info("Master password configurata per la prima volta");
            }

            // AUTHENTICATION LOOP con protezione brute-force (max 5 tentativi)
            int attempts = 0;
            boolean authenticated = false;
            while (attempts < 5 && !authenticated) {
                System.out.print("Inserisci la master password: ");
                String input = scanner.nextLine().trim();
                
                if (MasterPasswordManager.authenticate(input, MASTER_PATH)) {
                    authenticated = true;
                    System.out.println("Accesso consentito.");
                    logger.info("Autenticazione riuscita");
                } else {
                    attempts++;
                    System.out.println("Master password errata. Tentativi rimasti: " + (5 - attempts));
                    logger.warning("Tentativo di autenticazione fallito. Tentativi rimasti: " + (5 - attempts));
                    
                    // SECURITY: Blocca accesso dopo troppi tentativi
                    if (attempts == 5) {
                        logger.severe("Troppi tentativi falliti. Accesso negato.");
                        return;  // Exit applicazione per sicurezza
                    }
                }
            }

            // === FASE 3: SETUP SISTEMA CRITTOGRAFICO ===
            
            // Inizializza servizio di storage sicuro
            SecureStorageService storage = new SecureStorageService(STORAGE_PATH);
            File keyFile = new File(KEY_PATH);
            
            // GESTIONE CHIAVE CRITTOGRAFICA: Carica esistente o genera nuova
            if (keyFile.exists()) {
                // Carica chiave esistente da file serializzato
                storage.loadKeyFromFile(keyFile);
                System.out.println("Chiave caricata.");
                logger.info("Chiave crittografica caricata da file esistente");
            } else {
                // FIRST-TIME: Genera nuova chiave AES-256 e salvala
                SecretKey genKey = CryptoManager.generateKey();
                storage.setEncryptionKey(genKey);
                storage.saveKeyToFile(keyFile);
                System.out.println("Nuova chiave generata e salvata.");
                logger.info("Nuova chiave crittografica generata e salvata");
            }

            // === STRATEGY PATTERN: Caricamento dinamico algoritmo crittografia ===
            SecretKey vaultKey = storage.getEncryptionKey();
            
            // REFLECTION: Carica strategia da config.properties
            // Permette di cambiare algoritmo senza ricompilare (AES vs Dummy vs Custom)
            EncryptionStrategy strategy = ReflectionUtil.loadStrategy(vaultKey);
            CryptoManager cryptoManager = new CryptoManager(strategy);

            // === FASE 4: INIZIALIZZAZIONE PATTERN COMPONENTS ===
            
            // REPOSITORY PATTERN: Gestione dati in memoria con categorizzazione
            VaultRepository repository = new VaultRepository();
            
            // OBSERVER PATTERN: Setup sistema notifiche real-time
            VaultNotificationService notificationService = new VaultNotificationService();
            repository.addObserver(notificationService);  // Registra observer
            System.out.println("🔔 Sistema di notificazioni attivato\n");
            logger.info("Sistema di notificazioni Observer attivato");
            
            // === FASE 5: CARICAMENTO DATI ESISTENTI ===
            
            // Carica credenziali dal file cifrato e popola repository
            List<Credential> stored = storage.loadCredentials();
            logger.info("Caricate " + stored.size() + " credenziali dal vault");
            
            // Popola repository (questo triggera eventi Observer)
            for (Credential c : stored) {
                repository.addCredential("Default", c);
            }

            // === COMPOSITE PATTERN: Setup struttura ad albero ===
            
            // Crea gerarchia cartelle: Root -> Social/Work
            FolderComposite root = new FolderComposite("Vault");
            FolderComposite social = new FolderComposite("Social");
            FolderComposite work = new FolderComposite("Work");
            
            // ALGORITMO DI CATEGORIZZAZIONE: Distribuisci credenziali per tipo
            for (Credential c : stored) {
                // Logic: se contiene "book" va in Social, altrimenti Work
                if (c.getService().toLowerCase().contains("book")) {
                    social.add(c);
                } else {
                    work.add(c);
                }
            }
            
            // Costruisci albero: Root contiene Social e Work
            root.add(social);
            root.add(work);

            // === FACTORY PATTERN: Setup factory per creazione credenziali ===
            CredentialFactory factory = new CredentialFactory();
            
            // === FASE 6: MAIN APPLICATION LOOP ===
            
            boolean running = true;
            while (running) {
                
                // === INTERFACCIA UTENTE MENU ===
                System.out.println("\n--- SecurePassVault ---");
                System.out.println("1. Aggiungi credenziale");      // Factory + Observer + Composite
                System.out.println("2. Rimuovi credenziale");       // Repository + Observer + Composite
                System.out.println("3. Visualizza credenziali");    // Composite + Iterator Pattern
                System.out.println("4. Esporta CSV");               // Multithreading + Strategy
                System.out.println("5. Cerca per servizio");        // Stream API + Lambda
                System.out.println("6. Modifica credenziali");      // Factory + Observer
                System.out.println("7. Cambia master password");    // Security Management
                System.out.println("8. Esci");                      // Cleanup + Statistics
                System.out.print("Scelta: ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    
                    // === CASE 1: AGGIUNGI CREDENZIALE ===
                    // Dimostra: Factory Pattern, Observer Pattern, Composite Pattern, Input Sanitization
                    case "1" -> {
                        try {
                            // INPUT SANITIZATION: Protegge da injection attacks
                            System.out.print("Servizio: ");
                            String service = InputSanitizer.sanitize(scanner.nextLine());
                            System.out.print("Username: ");
                            String user = InputSanitizer.sanitize(scanner.nextLine());
                            System.out.print("Password: ");
                            // NOTA: Password NON sanitizzata per preservare caratteri speciali
                            String pass = scanner.nextLine().trim();

                            // FACTORY PATTERN: Creazione oggetto tramite factory
                            Credential cred = (Credential) factory.create(service, user, pass);
                            
                            // REPOSITORY + OBSERVER: Aggiunta con notifica automatica
                            repository.addCredential("Default", cred);
                            
                            // PERSISTENCE: Salvataggio sicuro con crittografia
                            storage.saveCredential(cred);
                            
                            // DEMO CRITTOGRAFIA: Mostra password cifrata (educational)
                            System.out.println("Password cifrata: " + cryptoManager.encrypt(pass));
                            logger.info("Nuova credenziale aggiunta: " + service);

                            // COMPOSITE PATTERN: Aggiunta alla struttura ad albero
                            System.out.println("In quale cartella? 1) Social  2) Work");
                            String folder = scanner.nextLine().trim();
                            if ("2".equals(folder)) {
                                work.add(cred);  // Aggiunge a cartella Work
                            } else {
                                social.add(cred);  // Default: cartella Social
                            }
                            
                        } catch (IllegalArgumentException e) {
                            // EXCEPTION SHIELDING: Gestione errori sanitization
                            System.out.println("Errore nei dati inseriti: " + e.getMessage());
                            logger.warning("Input validation error: " + e.getMessage());
                        } catch (Exception e) {
                            // EXCEPTION SHIELDING: Gestione errori generici
                            System.out.println("Errore durante l'aggiunta della credenziale.");
                            logger.severe("Error adding credential: " + e.getMessage());
                        }
                    }

                    // === CASE 2: RIMUOVI CREDENZIALE ===
                    // Dimostra: Repository Pattern, Observer Pattern, Composite Pattern, Input Validation
                    case "2" -> {
                        List<Credential> all = repository.getByCategory("Default");
                        
                        // EDGE CASE: Vault vuoto
                        if (all.isEmpty()) {
                            System.out.println("Nessuna credenziale da rimuovere.");
                            break;
                        }
                        
                        // DISPLAY CREDENZIALI: Lista numerata per selezione
                        for (int i = 0; i < all.size(); i++) {
                            System.out.printf("%d. %s | %s%n",
                                i + 1, all.get(i).getService(), all.get(i).getUsername());
                        }
                        System.out.print("Numero da rimuovere: ");
                        
                        try {
                            // INPUT VALIDATION: Parse e validazione indice
                            int idx = parseAndValidateIndex(scanner.nextLine().trim(), all.size());
                            Credential toRemove = all.get(idx - 1);  // Convert to 0-based
                            
                            // REPOSITORY + OBSERVER: Rimozione con notifica
                            repository.removeCredential("Default", toRemove);
                            
                            // PERSISTENCE: Aggiornamento file storage
                            storage.overwriteAll(repository.getByCategory("Default"));
                            
                            // COMPOSITE PATTERN: Rimozione da struttura ad albero
                            social.remove(toRemove);
                            work.remove(toRemove);
                            
                            System.out.println("Credenziale rimossa.");
                            logger.info("Credenziale rimossa: " + toRemove.getService());
                            
                        } catch (NumberFormatException e) {
                            // INPUT VALIDATION ERROR
                            System.out.println("Inserire un numero valido.");
                            logger.warning("Invalid number format in removal: " + e.getMessage());
                        } catch (IndexOutOfBoundsException e) {
                            // RANGE VALIDATION ERROR
                            System.out.println("Numero fuori dall'intervallo valido.");
                            logger.warning("Index out of bounds in removal: " + e.getMessage());
                        } catch (Exception e) {
                            // GENERIC ERROR HANDLING
                            System.out.println("Errore durante la rimozione.");
                            logger.severe("Error removing credential: " + e.getMessage());
                        }
                    }

                    // === CASE 3: VISUALIZZA CREDENZIALI ===
                    // Dimostra: Composite Pattern, Iterator Pattern, Collections Framework
                    case "3" -> {
                        List<Credential> list = repository.getByCategory("Default");
                        
                        // BASIC LISTING: Visualizzazione semplice
                        if (list.isEmpty()) {
                            System.out.println("Vault vuoto.");
                        } else {
                            // LAMBDA EXPRESSION: forEach con method reference
                            list.forEach(c -> System.out.printf("%s | %s%n", c.getService(), c.getUsername()));
                        }

                        // COMPOSITE PATTERN DEMO: Visualizzazione gerarchica
                        System.out.println("\n🔗 Struttura ad albero:");
                        root.print(0);  // Stampa ricorsiva dell'albero

                        // ITERATOR PATTERN DEMO: Attraversamento con enhanced for-loop
                        System.out.println("\n🔍 Tutti i componenti:");
                        for (VaultComponent comp : root) {  // Usa CompositeIterator internamente
                            System.out.println(" • " + comp.getName());
                        }
                    }

                    // === CASE 4: ESPORTA CSV ===
                    // Dimostra: Multithreading, Strategy Pattern, Input Sanitization
                    case "4" -> {
                        try {
                            // INPUT SANITIZATION: Nome file sicuro
                            System.out.print("Nome file CSV (es. export.csv): ");
                            String fileName = InputSanitizer.sanitize(scanner.nextLine());
                            if (!fileName.endsWith(".csv")) fileName += ".csv";
                            
                            List<Credential> creds = repository.getByCategory("Default");
                            if (creds.isEmpty()) {
                                System.out.println("Vault vuoto.");
                            } else {
                                // MULTITHREADING: Export asincrono in background
                                // Evita di bloccare UI durante I/O intensive operations
                                new Thread(new CSVExportTask(creds, fileName, cryptoManager)).start();
                                System.out.println("Export avviato in background...");
                                logger.info("Export CSV avviato: " + fileName);
                            }
                            
                        } catch (IllegalArgumentException e) {
                            // INPUT VALIDATION ERROR
                            System.out.println("Nome file non valido: " + e.getMessage());
                            logger.warning("Invalid filename for export: " + e.getMessage());
                        } catch (Exception e) {
                            // GENERIC ERROR HANDLING
                            System.out.println("Errore durante l'export.");
                            logger.severe("Error during export: " + e.getMessage());
                        }
                    }

                    // === CASE 5: CERCA PER SERVIZIO ===
                    // Dimostra: Stream API, Lambda Expressions, Functional Programming
                    case "5" -> {
                        try {
                            // INPUT SANITIZATION + PREPROCESSING
                            System.out.print("Servizio da cercare: ");
                            String query = InputSanitizer.sanitize(scanner.nextLine()).toLowerCase();
                            
                            // STREAM API + LAMBDA: Ricerca funzionale avanzata
                            List<Credential> filtered = repository.getByCategory("Default").stream()
                                .filter(c -> c.getService().toLowerCase().contains(query))  // Lambda predicate
                                .collect(Collectors.toList());  // Terminal operation

                            if (filtered.isEmpty()) {
                                System.out.println("Nessun risultato trovato.");
                            } else {
                                // DISPLAY RESULTS: Lista numerata
                                System.out.println("Risultati trovati:");
                                for (int i = 0; i < filtered.size(); i++) {
                                    System.out.printf("%d. %s | %s%n",
                                        i + 1, filtered.get(i).getService(), filtered.get(i).getUsername());
                                }
                                
                                // SECURE PASSWORD DISPLAY: Con auto-hide per sicurezza
                                System.out.print("Vuoi visualizzare la password di uno di questi? Inserisci il numero (o premi INVIO per saltare): ");
                                String scelta = scanner.nextLine().trim();
                                if (!scelta.isBlank()) {
                                    try {
                                        int pos = parseAndValidateIndex(scelta, filtered.size());
                                        Credential selected = filtered.get(pos - 1);
                                        
                                        // SECURITY FEATURE: Temporary password display
                                        System.out.println("Password: " + selected.getPassword());
                                        System.out.println("La password sarà nascosta tra 5 secondi...");
                                        
                                        // AUTO-HIDE: Timer + screen clear per sicurezza
                                        Thread.sleep(5000);
                                        for (int i = 0; i < 20; i++) System.out.println();  // Clear screen
                                        System.out.println("La password è stata nascosta.");
                                        
                                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                                        System.out.println("Scelta non valida.");
                                        logger.warning("Invalid selection in search: " + e.getMessage());
                                    } catch (InterruptedException e) {
                                        // THREAD INTERRUPTION HANDLING
                                        System.out.println("Operazione interrotta.");
                                        Thread.currentThread().interrupt();  // Restore interrupt status
                                    }
                                }
                            }
                            
                        } catch (IllegalArgumentException e) {
                            // INPUT VALIDATION ERROR
                            System.out.println("Query di ricerca non valida: " + e.getMessage());
                            logger.warning("Invalid search query: " + e.getMessage());
                        } catch (Exception e) {
                            // GENERIC ERROR HANDLING
                            System.out.println("Errore durante la ricerca.");
                            logger.severe("Error during search: " + e.getMessage());
                        }
                    }

                    // === CASE 6: MODIFICA CREDENZIALI ===
                    // Dimostra: Factory Pattern, Observer Pattern, Input Validation
                    case "6" -> {
                        List<Credential> all = repository.getByCategory("Default");
                        
                        // EDGE CASE: Vault vuoto
                        if (all.isEmpty()) { 
                            System.out.println("Vault vuoto."); 
                            break; 
                        }
                        
                        // DISPLAY + SELECTION: Lista credenziali esistenti
                        for (int i = 0; i < all.size(); i++) {
                            System.out.printf("%d. %s | %s%n", i + 1, all.get(i).getService(), all.get(i).getUsername());
                        }
                        System.out.print("Numero da modificare: ");
                        
                        try {
                            // INPUT VALIDATION: Selezione credenziale
                            int idx2 = parseAndValidateIndex(scanner.nextLine().trim(), all.size());
                            Credential old = all.get(idx2 - 1);

                            // INPUT: Nuovo username (opzionale)
                            System.out.print("Nuovo username (vuoto = mantieni): ");
                            String nu = scanner.nextLine().trim();
                            if (!nu.isEmpty()) {
                                nu = InputSanitizer.sanitize(nu);  // Sanitize se fornito
                            } else {
                                nu = old.getUsername();  // Mantieni esistente
                            }

                            // INPUT: Nuova password (opzionale)
                            System.out.print("Nuova password (vuoto = mantieni): ");
                            String np = scanner.nextLine().trim();
                            if (np.isEmpty()) {
                                np = old.getPassword();  // Mantieni esistente
                            }

                            // FACTORY PATTERN: Crea nuova credenziale con dati aggiornati
                            Credential upd = (Credential) factory.create(old.getService(), nu, np);
                            
                            // REPOSITORY + OBSERVER: Modifica con notifica automatica
                            repository.modifyCredential("Default", old, upd);
                            
                            // PERSISTENCE: Salvataggio modifiche
                            storage.overwriteAll(repository.getByCategory("Default"));
                            
                            // COMPOSITE PATTERN: Aggiornamento struttura ad albero
                            social.remove(old); 
                            social.add(upd);
                            
                            System.out.println("Credenziale aggiornata.");
                            
                        } catch (NumberFormatException e) {
                            System.out.println("Inserire un numero valido.");
                            logger.warning("Invalid number format in modification: " + e.getMessage());
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Numero fuori dall'intervallo valido.");
                            logger.warning("Index out of bounds in modification: " + e.getMessage());
                        } catch (IllegalArgumentException e) {
                            // INPUT VALIDATION ERROR
                            System.out.println("Dati non validi: " + e.getMessage());
                            logger.warning("Invalid data in modification: " + e.getMessage());
                        } catch (Exception e) {
                            // GENERIC ERROR HANDLING
                            System.out.println("Errore durante la modifica.");
                            logger.severe("Error modifying credential: " + e.getMessage());
                        }
                    }

                    // === CASE 7: CAMBIA MASTER PASSWORD ===
                    // Dimostra: Security Management, Authentication
                    case "7" -> {
                        try {
                            // AUTHENTICATION: Verifica password corrente
                            System.out.print("Vecchia master password: ");
                            String op = scanner.nextLine().trim();
                            
                            if (MasterPasswordManager.authenticate(op, MASTER_PATH)) {
                                // INPUT VALIDATION: Nuova password
                                System.out.print("Nuova master password: ");
                                String npw = scanner.nextLine().trim();
                                
                                // SECURITY POLICY: Minimo 8 caratteri
                                if (npw.length() < 8) {
                                    System.out.println("La password deve essere di almeno 8 caratteri.");
                                    break;
                                }
                                
                                // UPDATE: Salva nuova password hashata
                                MasterPasswordManager.saveMasterPassword(npw, MASTER_PATH);
                                System.out.println("Master password aggiornata.");
                                logger.info("Master password aggiornata con successo");
                            } else {
                                // AUTHENTICATION FAILED
                                System.out.println("Master password errata.");
                            }
                            
                        } catch (Exception e) {
                            // SECURITY ERROR HANDLING
                            System.out.println("Errore durante il cambio password.");
                            logger.severe("Error changing master password: " + e.getMessage());
                        }
                    }

                    // === CASE 8: ESCI ===
                    // Dimostra: Cleanup, Statistics, Observer Pattern
                    case "8" -> {
                        running = false;  // Termina main loop
                        
                        // OBSERVER PATTERN: Mostra statistiche dalla sessione
                        notificationService.showStatistics();
                        System.out.println("Chiusura applicazione.");
                    }

                    // === DEFAULT: INPUT NON VALIDO ===
                    default -> System.out.println("Scelta non valida. Inserire un numero da 1 a 8.");
                }
            }

        } catch (IOException e) {
            // TOP-LEVEL I/O ERROR HANDLING
            System.err.println("Errore I/O: " + e.getMessage());
            logger.severe("I/O error in MainApp: " + e.getMessage());
        } catch (Exception e) {
            // TOP-LEVEL EXCEPTION SHIELDING
            // Previene crash dell'applicazione e information leakage
            System.err.println("Errore inatteso. Contatta l'amministratore.");
            logger.severe("Unexpected error in MainApp: " + e.getMessage());
        }
    }

    /**
     * UTILITY METHOD - Input Validation & Parsing
     * 
     * Converte e valida input stringa in indice numerico valido.
     * Implementa validation pattern per prevenire errori comuni.
     * 
     * VALIDATION RULES:
     * - Input deve essere numerico
     * - Indice deve essere nel range [1, maxSize] (1-based per UI user-friendly)
     * - Conversione automatica a 0-based per array access
     * 
     * @param input stringa da convertire (user input)
     * @param maxSize dimensione massima della lista (upper bound)
     * @return indice validato (1-based, pronto per conversion a 0-based)
     * @throws NumberFormatException se input non è numerico
     * @throws IndexOutOfBoundsException se indice fuori dal range valido
     */
    private static int parseAndValidateIndex(String input, int maxSize) 
            throws NumberFormatException, IndexOutOfBoundsException {
        
        // PARSING: Converte stringa a int (può lanciare NumberFormatException)
        int index = Integer.parseInt(input);
        
        // RANGE VALIDATION: Verifica bounds (1-based indexing per UI)
        if (index < 1 || index > maxSize) {
            throw new IndexOutOfBoundsException("Indice deve essere tra 1 e " + maxSize);
        }
        
        return index;  // Caller dovrà sottrarre 1 per 0-based array access
    }

    /**
     * SETUP METHOD - Application-specific Logger Configuration
     * 
     * Configura logger specifico per MainApp oltre al logging globale.
     * Permette tracking separato degli eventi dell'applicazione principale.
     * 
     * FEATURES:
     * - File separato "vault.log" per eventi MainApp
     * - Append mode per preservare storico
     * - SimpleFormatter per leggibilità
     * - Graceful fallback se configurazione fallisce
     */
    private static void setupLogger() {
        try {
            // FILE HANDLER: vault.log in append mode
            FileHandler fileHandler = new FileHandler("vault.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            
        } catch (IOException e) {
            // GRACEFUL FALLBACK: Non crashare se logging fallisce
            System.err.println("Errore configurazione logger: " + e.getMessage());
            // Applicazione continua senza logging su file (degraded mode)
        }
    }
}