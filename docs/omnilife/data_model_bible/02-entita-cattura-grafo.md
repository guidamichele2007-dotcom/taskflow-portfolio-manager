# 02 · Cattura e Grafo (entità trasversali)

> Eredita il [MDC](00-modello-dati-comune.md). Le due entità che rendono possibile, rispettivamente, l'ingresso universale dei dati (J1-J3) e l'integrazione tra moduli (J5) — il cuore differenziante di OmniLife.

## DM-CAPT-01 · CaptureInboxItem (elemento in Inbox)

**Descrizione**: un pensiero catturato che il parser non ha potuto (o l'utente non ha voluto) assegnare subito a un tipo/modulo. Garantisce che **nessuna cattura vada mai persa** (CAPT-R-02): è il fallback universale.

| Campo | Tipo concettuale | Obbligatorio | Note |
|---|---|---|---|
| `testo_grezzo` | Testo | Sì | Il testo originale, intatto |
| `interpretazione_proposta` | Struttura opzionale (tipo, campi suggeriti) | No | Prodotta da CAPT-004, mai applicata senza conferma (CAPT-R-03) |
| `motivo_in_inbox` | Enum: parser_incerto · modulo_non_attivo · scelta_utente | Sì | CAPT-010 per il caso modulo non attivo |
| `origine` | Enum: testo · voce · widget · condivisione_sistema | Sì | CAPT-002/003/009 |

**Relazioni**: nessuna relazione strutturale in ingresso; alla risoluzione (smistamento), l'elemento **si trasforma** nell'entità di destinazione (Task, Transazione, Abitudine, Nota…) — l'Inbox item cessa di esistere come tale (non resta un duplicato).

**Dipendenze**: CAPT-004 (parser), GAL (per il caso modulo non attivo).

**Regole**: gli elementi più vecchi di 14 giorni compaiono nella Revisione settimanale (CAPT-R-05); nessun limite di piano alla cattura o all'Inbox (CAPT-R-01, C-art. 170).

**Stati**: in attesa di smistamento · risolto (transizione, non uno stato persistente).

**Eventi collegati**: pubblica `capt.inbox.item.added` (Home mostra badge; REV include lo smistamento); alla risoluzione, l'evento `capt.item.captured` è pubblicato dal modulo di destinazione, non dall'Inbox item stesso.

**Riferimenti Functional Bible**: CAPT-001…010, in particolare CAPT-008/010, CAPT-R-01…05.

---

## DM-LINK-01 · GraphLink (Collegamento)

**Descrizione**: l'entità che formalizza "il grafo dati personale" già presente come concetto trasversale nella Functional Bible (dipendenza "Grafo" dichiarata da NOTE-003, TASK-015, HAB-012, GOAL-002). Un arco tipizzato, bidirezionale, tra due entità qualsiasi del sistema — la manifestazione concreta della differenziazione architetturale del prodotto (Product Bible, Problem Space P2, Competitor Bible §9.1). **MDEC-02** (MDC §4.2): un solo tipo di entità per tutte le relazioni cross-modulo, invece di un tipo per ogni coppia di moduli.

| Campo | Tipo concettuale | Obbligatorio | Note |
|---|---|---|---|
| `entita_a` | Riferimento (id + tipo) | Sì | Un endpoint del collegamento |
| `entita_b` | Riferimento (id + tipo) | Sì | L'altro endpoint |
| `ruolo` | Enum: collegamento_nota · contributo_obiettivo · altro (estendibile) | Sì | Distingue l'uso semantico (es. NOTE-003 vs GOAL-002) senza creare tipi diversi di entità |
| `creato_da_dispositivo` / `creato_il` | Vedi Envelope MDC §3 | Sì | — |

**Relazioni**: per definizione, **è** una relazione — non ha relazioni proprie oltre ai suoi due endpoint. Un'entità può avere un numero qualsiasi di GraphLink (GOAL-R-01: "un contributo può servire più obiettivi").

**Dipendenze**: nessuna verso moduli specifici (è posseduto dal Core, non da un modulo — coerente con "il Core possiede il grafo" del principio architetturale, non un modulo).

**Regole**:
- **INV-03/04** (MDC §4.2, §16): un GraphLink non referenzia mai un'entità eliminata definitivamente; convergenza per unione insiemistica, mai persa.
- Un GraphLink verso un'entità **cestinata** (non definitivamente eliminata) resta visibile come "riferimento a elemento eliminato" (MFC-R-12) — non si nasconde, si etichetta.
- Un GraphLink verso un'entità di un **modulo disattivato** non si elimina: il fronte/collegamento risulta "in pausa" (GOAL-003 scheda estesa), mai contato come assente.
- La rimozione di un GraphLink (scollegare) non elimina mai le due entità collegate.

**Stati**: attivo · sospeso (endpoint cestinato o modulo disattivato) · rimosso (per azione esplicita, non per cancellazione a cascata).

**Eventi collegati**: pubblica `note.item.linked/unlinked`; consumato da GOAL (ricalcolo progresso), da ogni scheda di dettaglio che mostra "collegamenti" (NOTE-074/UX IA-074, GOAL IA-092).

**Riferimenti Functional Bible**: NOTE-003, TASK-015, HAB-012, GOAL-002, C-art. 184 (nessuna dipendenza implicita).

---

*Prossimo: [Entità Attività](03-entita-attivita.md)*
