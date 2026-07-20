# 02 · Linguaggio Visivo — Iconografia, Illustrazione, Griglia, Responsive/Adaptive, Dark/Light

> Eredita [00-fondamenta](00-fondamenta.md), [01-token-visivi](01-token-visivi.md).

## 1. Iconografia

Eredita [UX-C-297](../ux_bible/13-ux-constitution.md): "ogni icona appartiene a un solo set coerente, stroke uniforme" — qui sistematizzato in regole applicabili.

| Regola | Descrizione |
|---|---|
| **DS-05** | Un solo set iconografico per l'intero prodotto, un solo stile di tratto (stroke), un solo peso — nessuna eccezione per modulo (coerente con P55) |
| **DS-06** | Ogni icona interattiva rispetta il target di tocco minimo (§[04](04-stati-e-accessibilita-visiva.md)), indipendentemente dalla dimensione visiva del glifo |
| **DS-07** | Le icone non sono mai l'unico portatore di significato in un'azione primaria: sempre accompagnate da un'etichetta testuale o raggiungibili con etichetta accessibile (UX Bible, Accessibility Bible §1) |
| **DS-08** | Le icone di stato (es. icona di sincronizzazione, §Icon Button in [05](05-componenti-navigazione.md)) usano forme distinte, non solo colori distinti, per restare comprensibili senza colore (DS-INV-05) |
| **DS-09** | Nessuna icona a colori pieni/multicolore: il set resta monocromatico, colorabile solo tramite i ruoli di `testo`/`accento` (§[01 §3](01-token-visivi.md)) — coerente con "senza tempo" (P53) |

## 2. Illustration Guidelines

Le illustrazioni compaiono solo negli stati vuoti didattici (UX Bible, [09-empty-states](../ux_bible/09-empty-states.md)) e nell'onboarding (UX Bible, [11-onboarding-experience](../ux_bible/11-onboarding-experience.md)) — mai come decorazione di schermate con contenuto.

| Regola | Descrizione |
|---|---|
| **DS-10** | Stile coerente con l'iconografia: tratto semplice, mai fotorealistico, mai stock-art generico (P53, senza tempo) |
| **DS-11** | Nessuna illustrazione veicola un'informazione che non sia anche disponibile in testo (accessibilità: le illustrazioni sono decorative rispetto al significato, mai portatrici esclusive) |
| **DS-12** | Le illustrazioni di stato vuoto sono **distinte** tra "mai usato" e "filtrato senza risultati" (già richiesto in [UX-R-020](../ux_bible/09-empty-states.md#principio-di-coerenza-visiva-tra-stati)) — questa Bible aggiunge: nessuna delle due condivide l'illustrazione degli stati di errore |
| **DS-13** | Le illustrazioni non contengono mai testo incorporato nell'immagine (viola la localizzazione, MFC-E-11, e l'accessibilità dello screen reader) |

## 3. Grid System

Non definito nelle Bible precedenti (formalizzazione nuova, coerente con il perimetro mobile-first del prodotto — Product Bible, architettura mobile).

| Regola | Descrizione |
|---|---|
| **DS-14** | Layout a colonna singola per il formato telefono (coerente con l'assenza di side navigation, [UX Bible Navigation Bible §3](../ux_bible/02-navigation-bible.md)) |
| **DS-15** | Margine esterno orizzontale costante = `spazio.4` (§[01 §1](01-token-visivi.md)); gutter interno tra elementi correlati = `spazio.2` |
| **DS-16** | Un'unica griglia per ogni schermata del catalogo ([UX Bible Screen Inventory](../ux_bible/03-screen-inventory.md)) — nessuna eccezione di layout per modulo (coerente con DS-INV-06) |
| **Griglia per formati più larghi (tablet/desktop)** | **Decisione rinviata** — il Product Bible colloca il client tablet/desktop come opzione futura condizionata (Business Strategy §5); questa Bible non definisce una griglia multi-colonna finché quella decisione di prodotto non è confermata (vedi [08-report §4](08-report.md)) |

## 4. Responsive Rules (adattamento alla dimensione/preferenze del testo)

| Regola | Descrizione | Fonte |
|---|---|---|
| **DS-17** | Ogni schermata regge il 200% di ingrandimento testo tramite **reflow**, mai troncamento del contenuto primario | Eredita [UX-C-192-193](../ux_bible/13-ux-constitution.md), non ridefinito |
| **DS-18** | I componenti a lunghezza fissa (chip, badge) troncano solo il testo secondario/etichetta, mai il valore primario (es. un importo non si tronca mai) | Nuovo, coerente con P6 (mai mentire sui dati) |
| **DS-19** | Nessun contenuto interattivo primario è collocato in aree potenzialmente riservate dal sistema (bordi estremi dello schermo) | Nuovo — generico, indipendente da dispositivo specifico |

## 5. Adaptive Rules (adattamento al contesto/piattaforma)

| Regola | Descrizione | Fonte |
|---|---|---|
| **DS-20** | Il tema (chiaro/scuro) segue l'impostazione di sistema per default, con override manuale (SET-001, gruppo "Aspetto") | Functional Bible |
| **DS-21** | Il layout non introduce varianti strutturali diverse per piattaforma (iOS/Android): la coerenza è "di significato", non di pixel (UX-C-106, Product Principle P63) | Product Bible/UX Bible |
| **DS-22** | Preparazione RTL: ogni componente con orientamento direzionale (icone di navigazione, ordine di lettura) ha una regola di specchiatura dichiarata, anche se non lanciata al day one | Eredita [UX Bible Accessibility §9](../ux_bible/12-accessibility-bible.md) |
| **DS-23** | "Riduci movimento" di sistema attivo → ogni token di motion (§[03](03-motion.md)) ha equivalente statico, **sempre**, senza eccezioni di componente | Eredita [UX-C-191](../ux_bible/13-ux-constitution.md) |

## 6. Dark/Light Mode

Eredita P56/UX-C-293 ("nascono insieme, mai una derivata dall'altra") — qui le conseguenze sistemiche:

| Regola | Descrizione |
|---|---|
| **DS-24** | Ogni token di colore (§[01 §3](01-token-visivi.md)) è definito come coppia esplicita: nessun algoritmo di inversione automatica |
| **DS-25** | L'elevazione in scuro privilegia la variazione tonale rispetto all'ombra (§[01 §4](01-token-visivi.md), DS-04) |
| **DS-26** | Il contrasto minimo (AA, §[04](04-stati-e-accessibilita-visiva.md)) è verificato **in entrambi i temi indipendentemente** — un valore che supera AA in chiaro non è automaticamente assunto valido in scuro |
| **DS-27** | `accento.base` (§[01 §3.2](01-token-visivi.md)) può avere una luminosità diversa tra i due temi (spesso desaturato in scuro per ridurre l'affaticamento) — la coppia resta comunque un solo ruolo, non due palette |

---

*Prossimo: [Motion](03-motion.md)*
