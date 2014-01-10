

 La libreria cpim è stata estesa per permettere alle applicazioni che ne fanno uso di poter essere eseguite anche su glassfish AS. Tutti i servizi esposti dalla libreria sono stati opportunamente implementati per poter essere disponibili nel caso di deploy su glassfish. Inizia con una panoramica dei vari servizi e delle soluzioni adottate per l'implementazione su glassfish. La seconda parte invece illustra i passaggi e gli strumenti necessari per poter effettuare il deploy di un applicazione su glassfish.

DETTAGLI IMPLEMENTAZIONE SERVIZI CPIM PER GLASSFISH

SERVIZIO NoSQL
Per l'implementazione del servizio NoSQL si è utilizzata la specifica JPA. Il provider scelto è EclipseLink disponibile di default su glassfish. Per poter utilizzare il servizio NoSQL è necessario inserire nella cartella META-INF il file persistence.xml, opportunamente configurato. Il persistence.xml dovrebbe avere una struttura simile a quella riportata nel file persistence-template.xml reperibile dal file tools.zip.

SERVIZIO SQL
Il servizio SQL è stato implementato in maniera simile a quanto fatto per gli altri vendor. In questo caso la stringa di connessione che sarà quella creata tra Glassfish e MySQL come si vedrà nel paragrafo sulla configurazione.

SERVIZIO MAIL
L'implementazione è stata effettuata utilizzando la specifica JavaMail. Nel caso di Glassfish va notato che a differenza di GAE e Azure è necessario specificare nel file di configurazione il server SMTP che si vuole utilizzare per il servizio mail, non essendocene uno utilizzato dall'AS di default, specificandone indirizzo e porta.

SERVIZIO MESSAGEQUEUE
Il servizio messagequeue è stato implementato utilizzando la specifica JMS ed appoggiandosi quindi a JNDI. Una ConnectionFactory JMS viene registrata su Glassfish e richiamata tramite JNDI per poter ottenere delle connessioni. Le cose di messaggi sono invece mantenute dall'applicazione. Per questo servizio come si vedrà in seguito, sarà quindi necessario configurare opportunamente Glassfish.

SERVIZIO BLOB
Per quanto riguarda questo servizio, poichiè gli altri vendor dispongono di un database specifico in cui immagazzinare i file Blob e delle specifiche API per gestirli, si è pensato di creare anche in questo caso un secondo database, appoggiandosi tuttavia sempre a MySQL ed utilizzando l'interfaccia Blob disponibile nel package java.sql. 
Per far sì che il secondo database sia riconosciuto dalla libreria, si è estesa anche la struttura e il parsing del file configuration.xml includendo una seconda stringa di connessione verso un secondo database ed estendendo la classe CloudMetadata affinchè tale stringa venga parificata nel caso di Glassfish come vendor. 
Il database per i file di tipo Blob va inoltre solo creato e connesso all'applicazione in quanto la sua inizializzazione è effettuata internamente alla libreria al momento dell'istanziazione della classe GlassfishBlobManagerFactory. Si rimanda al paragrafo relativo alla configurazion per ulteriori dettagli.

SERVIZIO MEMCACHE
Anche in questo caso non essendo presente in Glassfish un implementazione di tale servizio è stato necessario ricorrere a strumenti esterni. 
Innanzitutto è necessario disporre di un memecached server che implementi il servizio in locale e che acceda quindi alla RAM locale realizzando il protocollo memcache. Si è scelta l'applicazione memcached disponibile per windows.
Per quanto riguarda l'implementazione vera e propria tra le API che vengono incontro a tale problema si è scelto di utilizzare la libreria spymemcached già utilizzata per l'implementazione del servizio relativamente al provider Azure. Per poter utilizzare tale libreria correttamente è necessario anche istruire Glassfish fornendogli i moduli necessari. Anche in tal caso si rimanda al capitolo sulla configurazione e gli strumenti necessari alla messa in opera per ulteriori dettagli.

SERVIZIO TASKQUEUE
Per tale servizio è stata utilizzata una normale coda che registra tutti gli oggetti di tipo CloudTask e che viene poi gestita da un apposito Consumer (GlassfishTaskQueueHandler) che periodicamente analizza i dati di uno specifico CloudTask e richiama la servlet necessaria.

GUIDA ALL'INSTALLAZIONE E ALLA CONFIGURAZIONE

I passi da seguire per poter ottenere una struttura funzionante sono i seguenti:

-installare Glassfish AS
-installare MySQL
-configurare glassfish per permettergli di utilizzare MySQL connector/j
-creare tramite MySQL due database uno per i dati ordinari delle applicazioni e uno per i dati di tipo Blob
-installare ed avviare in locale il server memcached
-creare su Glassfish una connessione due connessioni JDBC per i due database e una connessione JMS per il servizio di MessageQueue -includere tra i moduli di glassfish tutti i jar necessari all'utilizzo della libreria spymemcache presenti nel pacchetto degli strumenti utili sotto la directory "jar couchbase manager"
-includere nella direttory WEB-INF il file di configurazione di glassfish glassfish-web.xml (necessario per utilizzare il protocollo memcached)

Nel caso mancassero è inoltre necessario instllare sotto maven le libreri jpa4azure e simplejpa utilizzate rispettivamente nella libreria cpim per Azure e per Amazon al fine di non riscontrare errori.

Una nota va fatta per il file di configurazione della cpim configuration.xml. Come si è detto nel caso di glassfish tale file è stato ampliato nella sua struttura prevedendo ora l'inclusione di una seconda stringa di connessione necessaria per il database che conterrà i file Blob. Tale stringa viene parificata solo nel caso in cui venga scelto Glassfish come vendor ed è necessario quindi includerla per abilitare il servizio. Il file configuration.xml avrà quindi una struttura simile a quella riportata nel file configuration-template.xml reperibile dal file tools.zip.

Per quanto riguarda il file queue.xml la configurazione di questo resta invariata rispetto a quella adottata per gli altri vendor.

Tutti gli strumenti, le librerie e quanto necessario per effettuare il setup completo del framework necessario per il deploy su glassfish sono reperibili nel file tools.zip.