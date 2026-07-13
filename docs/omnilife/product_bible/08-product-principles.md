# 08 · Product Principles — I principi progettuali

> **110 principi operativi.** Sono le regole con cui si progetta, si scrive, si decide ogni giorno. Differenza rispetto alla [Constitution](15-product-constitution.md): la Constitution è il vincolo supremo (che cosa non si fa mai); i principi sono la guida quotidiana (come si fa ciò che si fa). Dove un principio ammette eccezioni, l'eccezione va motivata nel [Decision Log](14-decision-log.md).
>
> Uso: nelle review di design e di prodotto si citano per numero ("viola il 12", "il 47 dice di no").

## I. Dati e fiducia (1–12)

1. **L'utente non deve mai perdere dati.** Mai. Un dato confermato alla UI è un dato al sicuro. Ogni altra proprietà del sistema è negoziabile prima di questa.
2. **Ogni operazione importante deve essere annullabile.** L'undo sostituisce la conferma preventiva: prima si agisce, poi si può tornare indietro. I dialoghi "sei sicuro?" sono un fallimento di design.
3. **Le cancellazioni sono reversibili per 30 giorni**, salvo richiesta esplicita di distruzione immediata.
4. **Nessun dato viene raccolto senza una funzione che lo giustifichi** — visibile all'utente.
5. **Tutto ciò che l'utente crea deve essere esportabile** in formati aperti, in ogni momento, dal dispositivo.
6. **Il sistema non deve mai mentire sui dati**: se qualcosa non è sincronizzato, salvato o aggiornato, lo stato è visibile (con discrezione, senza allarme).
7. **I dati sensibili non appaiono mai** in notifiche, screenshot di sistema recenti, log o messaggi di errore.
8. **Il device è la fonte di verità.** Il cloud serve il device, mai il contrario.
9. **La fiducia si costruisce nei dettagli noiosi**: il backup che funziona, il ripristino perfetto, l'export completo. Ogni release li ritesta.
10. **Nessuna funzione può richiedere di leggere i contenuti lato server.** Se la richiede, la funzione è sbagliata (Constitution, art. 1).
11. **La sicurezza non è mai un'opzione premium.**
12. **Il consenso è specifico, informato, revocabile** — e la revoca è facile quanto la concessione.

## II. Attrito e tempo (13–24)

13. **Ogni tocco deve guadagnarsi il diritto di esistere.** Ogni flusso ha un budget di interazioni dichiarato; superarlo è un bug.
14. **La cattura di qualsiasi pensiero deve costare meno di 3 secondi.** È il principio fondativo del prodotto.
15. **Il valore prima del lavoro**: l'utente riceve qualcosa di utile prima di dover configurare qualsiasi cosa.
16. **Default eccellenti, personalizzazione facoltativa.** Il prodotto è finito, non un kit (anti-J18).
17. **Zero configurazione obbligatoria.** Se una scelta può essere presa bene da noi, la prendiamo noi — reversibilmente.
18. **Ogni flusso ricorrente dev'essere più veloce a ogni ripetizione**: il sistema impara (categorie, orari, importi ricorrenti).
19. **Mai chiedere due volte la stessa informazione.**
20. **Il percorso più frequente è il più corto.** Le statistiche d'uso ridisegnano le gerarchie, mai il marketing interno.
21. **Aprire l'app non deve mai essere necessario per le azioni da un gesto**: widget, notifiche azionabili, voce.
22. **Il tempo nell'app non è una metrica di successo: è un costo per l'utente.** Ottimizziamo il time-to-done.
23. **Le attese vanno eliminate, non decorate.** Se serve uno spinner più di 300 ms, il problema è l'architettura del flusso.
24. **Nessuna schermata intermedia che esista solo per far scegliere**: le scelte si fanno nel contesto, non in stanze d'attesa.

## III. Semplicità e carico cognitivo (25–38)

25. **Ogni schermata comunica un solo concetto principale.**
26. **Mai sacrificare la semplicità per aggiungere funzionalità.** La potenza si aggiunge in profondità (rivelazione progressiva), non in superficie.
27. **Rivelazione progressiva sempre**: il 100% degli utenti vede il 20% essenziale; il 20% che vuole di più lo trova dove si aspetta.
28. **Massimo tre opzioni per ogni decisione proposta.** Di più = paralisi.
29. **Le parole prima dei numeri, i numeri prima dei grafici.** "Sei a buon punto" batte "73,2%".
30. **Il gergo è vietato.** Se serve un glossario, il design ha fallito. (Test: Franca capisce ogni parola?)
31. **Nessuna funzione duplicata.** Due strade per lo stesso risultato = una di troppo, tranne l'accessibilità (gesto + tocco).
32. **Le gerarchie profonde sono un fallimento**: massimo 3 livelli di navigazione, massimo 2 di organizzazione dati.
33. **Un'anatomia sola per concetti analoghi**: task, abitudine e spesa si presentano con la stessa struttura visiva; imparata una, imparate tutte.
34. **Gli stati vuoti insegnano.** Ogni schermata vuota contiene un esempio concreto e un'azione — mai il vuoto e basta.
35. **Ogni funzione deve essere scopribile** nel contesto in cui serve — mai richiedere il manuale, mai richiedere il tour.
36. **Un solo suggerimento per sessione.** La scoperta è un condimento, non un pasto.
37. **Contare gli elementi sullo schermo.** Ogni elemento visibile compete per attenzione: se non aiuta la decisione corrente, si toglie.
38. **La densità è una scelta dell'utente esperto** (viste compatte opzionali), mai il default.

## IV. Rispetto psicologico (39–52)

39. **Mai punire. Mai colpevolizzare. Mai spaventare.** Il fallimento dell'utente non esiste: esiste la ricalibrazione.
40. **Le metriche di costanza perdonano**: nessun contatore che si azzera per un giorno storto.
41. **Mai progettare interfacce che inducano dipendenza.** Niente variable reward, niente feed infiniti, niente pull-to-refresh compulsivo.
42. **Le notifiche sono un patto di fiducia**: ognuna deve valere l'interruzione. Budget giornaliero, digest, silenzio notturno.
43. **L'automazione propone, l'utente dispone.** Mai un'azione automatica irreversibile senza consenso (lezione Motion, doc 05).
44. **Il tono è di un alleato competente**: mai genitore, mai poliziotto, mai cheerleader. (La guida di voce deriva da questo principio.)
45. **Celebrare con sobrietà**: il completamento merita un riconoscimento di 800 ms, non una festa che interrompe.
46. **Mai usare la vergogna come leva** — nemmeno implicitamente (rosso ovunque, statistiche di fallimento in evidenza).
47. **Mai confronto sociale pubblico.** Il benchmark dell'utente è il suo passato, non gli altri.
48. **Il rosso è riservato alla perdita di dati e alla sicurezza.** Il budget superato è ambra, con una proposta.
49. **Ogni segnale negativo include una via d'uscita concreta**: mai diagnosi senza terapia.
50. **Rispettare i momenti**: niente upsell nei momenti emotivi (obiettivo mancato, budget sforato, abitudine interrotta).
51. **Il fresh start è sempre disponibile**: ricominciare è un'azione di primo livello, senza costi né cerimonie.
52. **Progettare per l'uscita rapida**: l'utente entra, fa, esce. Il successo è la porta che si chiude in fretta.

## V. Design e qualità percepita (53–66)

53. **Design senza tempo**: nessun trend visivo che invecchierà; in dubbio, scegliere l'opzione più semplice e più duratura.
54. **Il contenuto è l'interfaccia**: il chrome serve il contenuto o sparisce.
55. **Un solo design system, nessuna eccezione per modulo.** Un componente nuovo nasce solo se la libreria non ha l'equivalente.
56. **Dark e Light nascono insieme**, mai una derivata dall'altra.
57. **Le animazioni comunicano relazioni spaziali e di stato — mai decorazione.** Tutte sotto 350 ms, tutte disattivabili.
58. **60 fps è il minimo, non l'obiettivo.** Un frame perso percepibile è un difetto di release.
59. **La tipografia è l'80% dell'interfaccia**: gerarchia chiara, contrasto pieno, mai testo sotto i minimi di leggibilità.
60. **Ogni pixel ha un proprietario**: niente elementi "di nessuno" (badge orfani, icone decorative, divider superflui).
61. **Migliorare l'esistente batte aggiungere il nuovo.** Prima di ogni feature: questa energia spesa sull'esistente renderebbe di più?
62. **L'aptica è linguaggio**: vocabolario coerente e parco; la vibrazione gratuita è rumore.
63. **La coerenza tra piattaforme è di significato, non di pixel**: iOS si sente iOS, Android si sente Android, OmniLife si riconosce in entrambi.
64. **Gli errori sono momenti di design di prima classe**: messaggio umano, causa comprensibile, azione riparatrice — mai codici, mai colpe.
65. **La qualità si giudica al confine**: stati limite (0 elementi, 10.000 elementi, testo lunghissimo, offline, font 200%) progettati, non subiti.
66. **Il "quasi giusto" è sbagliato.** Allineamenti, ritmi, ottiche: la cura invisibile è ciò che rende il prodotto premium.

## VI. Accessibilità e inclusione (67–76)

67. **L'accessibilità è un requisito di release, non un miglioramento futuro.** (WCAG 2.2 AA minimo, sempre.)
68. **Ogni funzione utilizzabile con screen reader, tastiera, switch e voce.** Nessuna funzione "solo touch".
69. **Ogni gesto ha un equivalente visibile.** Le swipe sono scorciatoie, mai l'unica via.
70. **Il layout regge il 200% di font scaling** senza perdita di funzioni.
71. **Mai informazione veicolata dal solo colore o dal solo movimento.**
72. **Target di tocco generosi ovunque** — anche nei widget, anche nelle notifiche.
73. **Progettare per la stanchezza**: l'utente delle 23:40 con il 5% di batteria è l'utente reale.
74. **Il linguaggio semplice è accessibilità**: frasi brevi, parole comuni, mai sigle non spiegate.
75. **Testare con persone reali con disabilità**, non solo con gli strumenti automatici.
76. **L'inclusione è anche economica**: il free tier degno e i prezzi regionali sono scelte di accessibilità.

## VII. Modularità e integrazione (77–86)

77. **Ogni modulo deve stare in piedi da solo** — utile anche se è l'unico attivo.
78. **Ogni modulo deve valere di più insieme agli altri**: se non contribuisce al grafo e alla Home, non è un modulo, è un'app estranea.
79. **Attivare un modulo è un invito, disattivarlo è un diritto**: mai penalizzare chi ne usa uno solo.
80. **Disattivare non cancella mai.**
81. **I moduli non si conoscono tra loro.** Comunicano per eventi e collegamenti; l'accoppiamento è il debito che non contraiamo.
82. **La Home è di tutti e di nessuno**: ogni modulo contribuisce, nessuno la possiede, l'utente la governa.
83. **Il vocabolario è unico attraverso i moduli**: stessa parola, stesso concetto, ovunque.
84. **Un nuovo modulo nasce solo se**: serve un job documentato, non è servibile da un modulo esistente, e regge da solo (77) e insieme (78).
85. **Le integrazioni esterne servono i moduli, non li sostituiscono** — e non creano mai dipendenza dalla rete (il calendario di sistema, la salute di sistema: fonti locali).
86. **Il contratto dei moduli è progettato come se fosse pubblico dal giorno 1**: la disciplina di oggi è il marketplace di domani.

## VIII. Prestazioni e affidabilità (87–94)

87. **La velocità è una feature; la lentezza è un bug** con la stessa priorità di un crash.
88. **Progettare per il dispositivo di 5 anni fa**: se vola lì, vola ovunque.
89. **Offline non è una modalità: è lo stato normale.** Ogni feature nasce offline; la rete la arricchisce.
90. **L'avvio è sacro**: sotto 1,5 s a freddo, sempre; ogni modulo paga il suo budget di avvio.
91. **La batteria dell'utente è sua**: background parsimonioso, sync opportunistica, zero polling.
92. **Nessuna operazione bloccante sopra i 50 ms percepiti** per le azioni quotidiane.
93. **Il degrado è progressivo, mai catastrofico**: se qualcosa non funziona, tutto il resto continua a funzionare.
94. **Testare i percorsi del disastro**: kill del processo, disco pieno, orologio sbagliato, 100k entità — a ogni release.

## IX. Business e crescita (95–103)

95. **Il cliente è l'utente.** Nessun terzo pagante potrà mai comprare priorità sulla sua esperienza.
96. **Mai monetizzare la paura** (perdita dati, sicurezza, lock-in). Si monetizza il valore aggiunto, mai la protezione del dovuto.
97. **Il paywall dice la verità**: cosa è incluso, cosa no, quanto costa, come si disdice — tutto prima del pagamento.
98. **Il free tier è un prodotto degno**, non una demo con il conto alla rovescia.
99. **Convertire per desiderio, mai per ricatto**: il momento dell'upsell è il momento di massimo valore percepito, mai di massima vulnerabilità.
100. **Disdire è facile quanto abbonarsi.**
101. **La crescita non compra ciò che il prodotto non mantiene**: nessuna promessa di marketing oltre la verità del prodotto.
102. **Ogni dark pattern documentato dai competitor è un nostro divieto esplicito.**
103. **I prezzi cambiano solo verso la chiarezza**: mai complicare l'offerta per nascondere un aumento.

## X. Processo e cultura (104–110)

104. **Ogni funzione dichiara il suo job e la sua metrica di successo prima di essere costruita** — e viene rimossa se non li dimostra.
105. **La rimozione è una feature.** Il prodotto migliora anche per sottrazione; ogni release può togliere qualcosa.
106. **Le decisioni si registrano** (Decision Log): una decisione non scritta non esiste; una decisione scritta può essere rivista con onestà.
107. **Il disaccordo si esprime con i principi**: "viola il 39" è un argomento; "non mi piace" no.
108. **Chi cita la Constitution ha ragione finché la Constitution non cambia** — anche contro il fondatore.
109. **Prima il problema, poi la soluzione**: nessuna feature entra in discussione senza il problema documentato che la origina.
110. **Questa lista è viva ma esigente**: aggiungere un principio richiede dimostrare che nessun principio esistente copre il caso; toglierne uno richiede il processo di emendamento della Constitution.

---

*Prossimo: [Feature Philosophy](09-feature-philosophy.md) — come questi principi decidono che cosa entra nel prodotto.*
