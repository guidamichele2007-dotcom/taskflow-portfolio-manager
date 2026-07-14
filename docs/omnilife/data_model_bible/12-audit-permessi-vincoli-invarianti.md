# 12 · Audit Trail, Permessi, Vincoli di Business e Invarianti (catalogo cross-entità)

> Eredita il [MDC](00-modello-dati-comune.md). Consolida ciò che è **cross-entità**: le regole già motivate dentro ogni modulo (TASK-R-*, FIN-R-*, HAB-R-*, ecc.) restano lì e sono solo richiamate per riferimento — qui solo ciò che attraversa i moduli o che il MDC non ha già coperto.

## 1. Audit trail — modello consolidato

L'audit trail dell'utente **coincide** con la cronologia (per-campo o a snapshot, MDC §7): non esiste, e non deve esistere, un log di controllo separato e più dettagliato per l'utente finale — sarebbe una duplicazione (P31, C-art. 61).

| Ambito | Che cosa si traccia | Dove |
|---|---|---|
| Entità di dominio (Task, Transazione, Abitudine, Nota, Obiettivo…) | Ogni modifica di campo (o versione, per Note) | Cronologia dell'entità stessa (MDC §7) |
| Sicurezza dell'account | Tentativi di accesso, dispositivi collegati/revocati, rigenerazioni di chiave | `DM-SYS-02 Device`, `DM-SYS-05 RecoveryKeyMetadata` — locale + lato server per gli eventi di autenticazione, **mai le password** (MFC §5, C-art. 22) |
| Notifiche | Esito di ogni richiesta (mostrata/azionata/ignorata) | `DM-NTF-01 NotificationRequest`, solo locale, usato per NTF-006 |
| Import/Export | Non un audit persistito: l'anteprima prima della conferma è la garanzia (MFC-E-13); l'export stesso è l'evidenza di cosa è stato estratto | EXP-001/002 |

**Regola**: nessuna voce di cronologia è mai eliminabile dall'utente singolarmente (si elimina con l'intera entità, nel rispetto del ciclo cestino/eliminazione definitiva) — la cronologia non è un dato "extra" separabile dal suo soggetto.

## 2. Permessi — modello consolidato (MVP e v1.x)

Nessun modello di permessi multi-utente esiste nell'MVP (MDC §15). Le uniche due forme di controllo di accesso:

| Forma | Che cosa controlla | Dove | Natura |
|---|---|---|---|
| Blocco per modulo sensibile | Visibilità sullo schermo (non i dati stessi) | Finanze, Note (opzionale), Salute — MFC-R-21 | Locale al dispositivo (biometria/codice), non un permesso sui dati |
| Consenso di sistema per fonti esterne | Se leggiamo/scriviamo Calendario o Salute | CalendarSource, HealthPlatformReading | Concesso dall'utente al sistema operativo, non gestito da un nostro modello di permessi |

**Vincolo esplicito**: nessuna entità di questa Bible presuppone un campo "chi può vedere/modificare questo elemento" a grana fine — sarebbe una funzionalità non ancora specificata dalla Functional Bible (le "condivisioni future" sono opt-in granulari dichiarate ma non specificate, MFC-R-20 in chiusura, Product Bible Business Strategy §4.3). Introdurlo ora sarebbe scrivere funzionalità nuova, non modellare l'esistente — per questo è un [decisione rinviata](14-report.md).

## 3. Vincoli di business cross-entità (non duplicati dai moduli)

Le regole di business specifiche di un solo modulo restano nella loro sede (TASK-R-*, FIN-R-*, HAB-R-*, CAL-R-*, NOTE-R-*, GOAL-R-*, SET-R-*, SYNC-R-*, BKP-R-*, EXP-R-*, NTF-R-*, INS-R-*). Qui solo i vincoli che coinvolgono **più moduli contemporaneamente**, impliciti nella Functional Bible ma mai raccolti in un solo posto:

| ID | Vincolo cross-entità | Moduli coinvolti | Fonte |
|---|---|---|---|
| VCB-01 | L'eliminazione (non definitiva) di un'entità sospende, non elimina, ogni GraphLink e ogni riferimento non strutturale che la coinvolge (es. TimeBox verso Task, GraphLink verso Note/Goal) | Tutti | MFC-R-12 |
| VCB-02 | La disattivazione di un modulo non elimina mai le entità di quel modulo né i GraphLink che le coinvolgono da altri moduli — questi risultano "in pausa" | Tutti, in particolare GOAL↔ogni modulo | MFC-R-13, GOAL-003 scheda estesa |
| VCB-03 | Un'esecuzione derivata da una fonte automatica (Salute→Abitudine) e una manuale per lo stesso giorno non producono mai un doppio conteggio | HAB, HLTH | HAB-AC-05, HLTH-AC-02 |
| VCB-04 | Un evento pubblicato da un modulo disattivato non viene mai generato; i moduli sottoscrittori tollerano silenziosamente l'assenza del producer | Tutti (bus eventi) | MFC §8, C-art. 184 |
| VCB-05 | Nessun contatore derivato (saldo conto, aderenza abitudine, progresso obiettivo) è mai scritto direttamente da un utente o da una sincronizzazione: è sempre ricalcolato dai dati sorgente | FIN, HAB, GOAL | FIN-R-02, [11 §6](11-versionamento-e-sincronizzazione.md#6-modello-concettuale-di-convergenza-per-tipo-di-campo-crdt-senza-scegliere-unimplementazione) |
| VCB-06 | L'export completo deve permettere la ricostruzione manuale di ogni informazione utente, inclusi i GraphLink espressi come riferimenti leggibili | Tutti | EXP-001 |
| VCB-07 | Un'entità in stato "cestinata" non compare mai nei risultati di ricerca di default, ma resta raggiungibile con filtro esplicito, in ogni modulo allo stesso modo | Tutti | MFC-R-09, SRCH-006 |

## 4. Invarianti — indice consolidato

Gli invarianti universali (INV-01…14) sono catalogati nel [MDC §16](00-modello-dati-comune.md#16-invarianti-universali-catalogo-non-duplicato-nei-moduli). Qui solo gli invarianti aggiuntivi emersi dall'approfondimento sul versionamento e sulla sincronizzazione (§11), per completezza:

| ID | Invariante | Rif. |
|---|---|---|
| INV-15 | La generazione di un'occorrenza ricorrente (Task o Transazione) è idempotente per (regola, periodo): non esistono due occorrenze per lo stesso periodo, anche con generazione concorrente multi-device | [11 §3](11-versionamento-e-sincronizzazione.md#3-generazione-delle-occorrenze-ricorrenti-task-dm-task-01-finanze-dm-fin-01) |
| INV-16 | La fascia di aderenza di un'Abitudine non può mai scendere di più di un livello per un singolo giorno saltato | [11 §4](11-versionamento-e-sincronizzazione.md#4-calcolo-delladerenza-resiliente-abitudini-dm-hab-02) |
| INV-17 | La fascia complessiva di un Obiettivo è sempre determinata dal fronte attivo con il progresso nativo peggiore, mai da una media | [11 §5](11-versionamento-e-sincronizzazione.md#5-calcolo-del-progresso-aggregato-obiettivi-dm-goal-01) |
| INV-18 | Nessuna entità di tipo `HealthPlatformReading` esiste al di fuori della cache locale a finestra; non ne esiste mai copia sul cloud | HLTH-R-02 |

---

*Prossimo: [ERM e Matrici](13-erm-e-matrici.md)*
