# 00 · Fondamenta — Principi, Linguaggio Visivo, Convenzioni, Vincoli

> **Fonte di verità esclusiva**: [Product Bible](../product_bible/README.md), [Functional Bible](../functional_bible/README.md), [UX Bible](../ux_bible/README.md), [Data Model Bible](../data_model_bible/README.md), [Technical Architecture Bible](../technical_architecture_bible/README.md). Nessun mockup, nessuna schermata, nessuna immagine, nessun codice/CSS/Flutter/SwiftUI/Compose: solo il sistema — token, regole, componenti — indipendente da ogni tecnologia di rendering.
>
> **Regola anti-duplicazione**: dove Product Bible o UX Bible hanno già normato un principio o un vincolo, questo documento **cita**, non ripete. Il valore aggiunto di questa Bible è la **sistematizzazione visiva** (scale, ruoli semantici, token) che le Bible precedenti presuppongono ma non definiscono numericamente.

## 1. Design Principles (eredità diretta, non ridefiniti)

Questa Bible eredita integralmente i principi già stabiliti in [Product Bible §V "Design e qualità percepita" (P53-66)](../product_bible/08-product-principles.md) e [§VI "Accessibilità" (P67-76)](../product_bible/08-product-principles.md), oltre al [Titolo XIV della UX Constitution (UX-C-291-312)](../ux_bible/13-ux-constitution.md). I cinque principi guida di questa sistematizzazione:

| Principio ereditato | Conseguenza per il sistema di design |
|---|---|
| P53 — Design senza tempo | Nessun token lega il sistema a un trend visivo di stagione; le scale (tipografia, spaziatura) sono proporzionali, non di moda |
| P54 — Il contenuto è l'interfaccia | Il "chrome" (bordi, ombre, decorazioni) è minimo per costruzione: ogni token esiste per servire la leggibilità del contenuto |
| P55 / UX-C-291 — Un solo design system, nessuna eccezione per modulo | **Decisione sistemica**: nessun modulo ha una palette propria. L'identità di un modulo si esprime tramite icona e testo, mai tramite colore di superficie (§4) |
| P59 — La tipografia è l'80% dell'interfaccia | La scala tipografica (§2 in [01-token-visivi](01-token-visivi.md)) è il token più importante di questa Bible, trattato con più livelli di dettaglio di ogni altro |
| P56 / UX-C-293 — Dark e Light nascono insieme | Ogni token di colore è definito come **coppia** (valore per tema chiaro + valore per tema scuro), mai uno derivato automaticamente dall'altro (§5 in [02-linguaggio-visivo](02-linguaggio-visivo.md)) |

## 2. Visual Language — dichiarazione

> **OmniLife si vede come si comporta: calmo, leggibile, senza tempo.** Superfici quasi vuote, gerarchia affidata quasi interamente alla tipografia, colore usato con parsimonia per significato (mai per decorazione), movimento che spiega una relazione e mai intrattiene.

Questa dichiarazione non è nuova: è la traduzione visiva di [Product Bible §4.1 "Calm Technology"](../product_bible/01-mission.md) e di [UX-C-054 "Il contenuto è l'interfaccia"](../ux_bible/13-ux-constitution.md) (già citato in Product Bible come P54). Il sistema di design non introduce un'estetica: **rende eseguibile** l'estetica già promessa.

## 3. Naming Conventions

| Categoria | Convenzione | Esempio |
|---|---|---|
| Token di colore | `ruolo-superficie.variante` (mai un nome legato a un valore, es. mai "blu-500") | `superficie.primaria`, `testo.secondario`, `accento.base` |
| Token tipografici | `ruolo.peso` legato alla funzione, non alla dimensione in pixel | `titolo.schermata`, `corpo.default`, `etichetta.chip` |
| Token di spaziatura | Multiplo dell'unità base, mai un valore assoluto isolato | `spazio.2`, `spazio.4` (§1 in [01-token-visivi](01-token-visivi.md)) |
| Componenti | Nome funzionale, mai tecnico o legato a un framework | "Riga Entità", non "ListTile"; "Foglio di Dettaglio", non "BottomSheetModal" |
| Stati di componente | Verbo o aggettivo allo stato, condiviso tra tutti i componenti | `default · in_evidenza · premuto · selezionato · disabilitato · in_errore · in_caricamento` (§[04](04-stati-e-accessibilita-visiva.md)) |
| ID di questa Bible | `DS-CATEGORIA-##` per token/regole, `CMP-NOME` per componenti | `DS-COLORE-01`, `CMP-RIGA-ENTITA` |

**Regola**: ogni nome deve essere comprensibile senza consultare il codice che lo implementerà — coerente con l'indipendenza tecnologica di questa Bible (nessun nome ispirato a una libreria o framework specifico).

## 4. Design Constraints (vincoli ereditati, applicati al sistema)

| Vincolo | Fonte | Applicazione al sistema di design |
|---|---|---|
| Profondità di navigazione massima 3 livelli | UX Bible, [MUC §5](../ux_bible/00-modello-ux-comune.md#5-pattern-universale-di-navigazione-profondità-e-ritorno) | Nessun componente di navigazione (§[05](05-componenti-navigazione.md)) supporta nidificazione oltre 2 livelli visivi (tab → dettaglio) |
| Massimo 5 elementi per card Home | Functional Bible, HOME-001 scheda estesa | Il componente Card (§[06](06-componenti-contenuto.md)) ha una variante "compatta" con limite di contenuto esplicito |
| Target di tocco ≥ 44×44pt/48×48dp | UX Bible, [Accessibility Bible §3](../ux_bible/12-accessibility-bible.md) | Vincolo non derogabile per ogni componente interattivo (§[04](04-stati-e-accessibilita-visiva.md)) |
| Animazioni ≤ 350ms, sempre disattivabili | UX Bible, [MUC §4](../ux_bible/00-modello-ux-comune.md#4-animazione-durate-e-curve-token-condivisi-con-il-design-system-del-doc-tecnico-04) | Ogni token di motion (§[03-motion](03-motion.md)) eredita questo limite, mai lo ridefinisce |
| Nessuna informazione veicolata dal solo colore | Product Constitution art. 97 (via UX Bible) | Ogni stato semantico (§4) ha sempre un secondo canale (icona, testo, forma) |
| Massimo 1 solo design system, nessuna eccezione per modulo | P55, UX-C-291 | Nessuna palette per modulo (§4 in [02-linguaggio-visivo](02-linguaggio-visivo.md)) |

## 5. Design Invariants (nuovi, propri di questa Bible)

Coerenti con il metodo delle Bible precedenti (Data Model Bible §16, Technical Architecture Bible): invarianti che nessuna futura estensione del sistema può violare senza un emendamento esplicito.

| ID | Invariante |
|---|---|
| **DS-INV-01** | Ogni token di colore esiste in coppia chiaro/scuro definita contestualmente, mai calcolata automaticamente da un solo valore |
| **DS-INV-02** | Nessun componente introduce un valore di spaziatura, raggio o durata fuori dalla scala definita in [01-token-visivi](01-token-visivi.md) |
| **DS-INV-03** | Ogni componente interattivo ha una rappresentazione visiva distinta per ciascuno degli stati generici applicabili (§[04](04-stati-e-accessibilita-visiva.md)) |
| **DS-INV-04** | Nessun componente nuovo entra nella libreria (§[05](05-componenti-navigazione.md)-[06](06-componenti-contenuto.md)) se un componente esistente, esteso con una variante, può servire lo stesso scopo (eredita P61/UX-C-292) |
| **DS-INV-05** | Ogni informazione di stato ha almeno due canali percettivi indipendenti (mai il solo colore, mai il solo movimento) |
| **DS-INV-06** | La libreria di componenti è identica per ogni modulo: nessun modulo introduce una variante di componente che non sia disponibile agli altri |

---

*Indice: [README](README.md) · Prossimo: [Token Visivi](01-token-visivi.md)*
