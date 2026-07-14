# 12 · Accessibility Bible

> Eredita [MUC §11](00-modello-ux-comune.md#11-pattern-universale-di-accessibilità-baseline-applicata-da-ogni-schermata). WCAG 2.2 AA è il **minimo**, non il traguardo (C-art. 98, RNF-U2/U3). Persona di riferimento primaria: Franca (S7, 63 anni, tech 1,5/5) e ogni utente di tecnologie assistive.

## 1. VoiceOver / TalkBack (Screen Reader)

| Requisito | Specifica |
|---|---|
| Ordine di lettura | Segue il focus order logico: intestazione → contenuto principale → azioni contestuali → tab bar. Mai un ordine che salta illogicamente tra aree |
| Etichette | Ogni elemento interattivo ha un'etichetta descrittiva dell'azione, non della sola apparenza ("Completa task: Chiamare il commercialista" non "Cerchio") |
| Ruoli | Bottoni annunciati come bottoni, link come link, campi come campi con lo stato (es. "modificabile", "selezionato") |
| Annunci di stato | Ogni cambio di stato prodotto da un'azione (completamento, errore, sync) genera un annuncio (live region) equivalente al feedback visivo (C-art. 99) |
| Contenuti grafici | Ogni grafico (budget, storico abitudini, tendenze salute) ha una descrizione testuale e una tabella dati navigabile in alternativa (C-art. 97) |
| Gesture personalizzate | Ogni swipe/long-press/drag ha un'azione equivalente esposta nel rotor (iOS) o nel menu locale (Android) — mai solo gesto fisico (C-art. 100, §8 Gestures) |
| Cattura vocale | Compatibile: la dettatura di sistema e VoiceOver/TalkBack possono coesistere (la cattura non intercetta gesti riservati all'assistivo) |

## 2. Switch Control / Comandi di accesso motorio

| Requisito | Specifica |
|---|---|
| Navigazione a scansione | Ogni elemento interattivo è raggiungibile per scansione sequenziale, in un numero ragionevole di passi (max 2 gruppi di scansione per raggiungere qualunque azione da una schermata) |
| Target di tocco | ≥ 44×44pt/48×48dp ovunque, inclusi widget e notifiche azionabili (C-art. 102) |
| Azioni temporizzate | Nessuna azione richiede un tocco entro un tempo limite (es. lo swipe di eliminazione non "scade"); il long-press ha soglia fissa non affrettabile ma nemmeno penalizzante per chi è più lento |
| Drag & Drop | Sempre affiancato da un'alternativa non gestuale (§8 Gestures: "Sposta su/giù") |

## 3. Contrasto

| Requisito | Specifica |
|---|---|
| Testo normale | ≥ 4.5:1 (AA) verificato sui token del design system in CI (doc tecnico 04 §2.1, §6) |
| Testo grande/componenti UI | ≥ 3:1 |
| Modalità alto contrasto di sistema | Rispettata: i token hanno varianti dedicate, mai un semplice aumento automatico che romperebbe la gerarchia visiva |
| Stati (ambra/verde/neutro) | Mai il solo colore a distinguere gli stati budget/aderenza: sempre accompagnato da testo/icona (C-art. 97) |

## 4. Riduzione delle animazioni

| Requisito | Specifica |
|---|---|
| "Riduci movimento" di sistema attivo | Ogni animazione (§7 Microinterazioni) ha variante statica a durata 0, con solo il cambio di stato finale istantaneo | 
| Nessuna eccezione | Incluse le celebrazioni/milestone (§7.6): diventano un cambio di stato immediato con lo stesso microcopy | 

## 5. Testo dinamico (Dynamic Type / font scaling)

| Requisito | Specifica |
|---|---|
| Scala supportata | Fino al 200% senza perdita di funzioni (C-art. 101) |
| Comportamento di layout | Reflow (a capo, elementi che si impilano), mai troncamento con "…" per contenuti primari; i pulsanti si adattano in altezza, mai in leggibilità del testo |
| Verifica | Test automatici a 3 taglie (100%, 150%, 200%) per ogni schermata, gate di rilascio |

## 6. Focus (tastiera esterna, navigazione a puntatore)

| Requisito | Specifica |
|---|---|
| Ordine di tabulazione | Coerente con l'ordine visivo e con l'ordine screen reader (stessa sequenza logica) |
| Indicatore di focus | Sempre visibile (contorno distinto), mai rimosso per estetica |
| Tastiera esterna | Scorciatoie minime standard (invio per confermare, esc per chiudere fogli/dialoghi, frecce per navigare liste) |

## 7. Comandi vocali

| Requisito | Specifica |
|---|---|
| Cattura vocale (CAPT-003) | Hands-free completo: dettatura → parsing → conferma vocale/aptica, nessun tocco necessario dall'inizio alla fine |
| Assistente di sistema (Siri Shortcuts / App Actions) | Cattura e spunta abitudine raggiungibili senza aprire l'app (WID-006) |
| Compatibilità | Non interferisce con VoiceOver/TalkBack attivi contemporaneamente |

## 8. Linguaggio e carico cognitivo (accessibilità cognitiva, P79)

| Requisito | Specifica |
|---|---|
| Lessico | Quotidiano, mai gergo tecnico non spiegato (P91) — verificato con il "test Franca": ogni frase dell'interfaccia deve essere comprensibile a un'utente di 63 anni con bassa competenza tecnologica |
| Scelte proposte | Mai più di 3 opzioni in un dialogo o selettore (P90) |
| Una azione per schermata nei flussi guidati | Onboarding, Revisione settimanale: una decisione alla volta (P28) |
| Messaggi di errore | Sempre concreti e mai ansiogeni (§10 Error Experience) |

## 9. Localizzazione e direzionalità

| Requisito | Specifica |
|---|---|
| RTL (preparazione futura) | L'architettura di layout è pronta per RTL (specchiatura di icone direzionali, ordine di lettura invertito) anche se non lanciata al day one (RNF-U4) |
| Formati | Data/ora/valuta secondo locale, mai hardcoded (MFC-E-11) |

## 10. Verifica e governance

- **Audit di accessibilità** a ogni release (automatico + manuale), gate di pubblicazione (RNF-U2).
- **Test con utenti reali con disabilità** almeno 2 volte l'anno (doc tecnico 04 §6, ripreso qui come requisito UX).
- **Una responsabilità formale** per l'accessibilità nel team, con potere di bloccare una release (analogo al "guardiano della semplicità", Constitution art. 203).
- **Ogni funzione della Functional Bible** eredita per costruzione questa Bible: non esiste una funzione "esente" da accessibilità (C-art. 98).

---

*Prossimo: [UX Constitution](13-ux-constitution.md)*
