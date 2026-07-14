# 13 · UX Constitution

> **La UX Constitution del prodotto — 312 regole.** Come la [Product Constitution](../product_bible/15-product-constitution.md) è il vincolo supremo del prodotto, questa è il vincolo supremo dell'esperienza: nessuna schermata, flusso o microinterazione futura può violarle senza un emendamento esplicito (stesso processo del Titolo X della Product Constitution). Ogni regola cita, dove pertinente, i principi (`P#`), articoli (`C-art. #`) e funzioni (`PREFISSO-###`) da cui deriva. Formato identificativo: `UX-C-###`.

## Titolo I — Tocchi e attrito (UX-C-001…025)

1. Nessun elemento creabile richiede più di 3 tocchi dall'intento al salvataggio (P13; RNF-U1).
2. La cattura di un pensiero qualsiasi richiede ≤ 3 secondi mediani (CAPT-001; P14).
3. Nessuna azione frequente (spunta, completamento) richiede più di 1 tocco.
4. Ogni default è scelto da noi in modo che l'utente debba solo confermare, mai comporre da zero.
5. Nessuna schermata esiste al solo scopo di far scegliere qualcosa che potremmo dedurre.
6. Il percorso più frequente di ogni flusso è sempre il più corto disponibile.
7. Nessuna funzione richiede la stessa informazione due volte nello stesso flusso.
8. Ogni azione ripetuta si velocizza nel tempo (chip di valori recenti, apprendimento del parser).
9. Nessuna conferma preventiva per azioni singole reversibili (UX-R-008).
10. Le azioni distruttive di massa (> 20 elementi) sono le uniche a richiedere conferma esplicita.
11. Ogni ricerca mostra risultati incrementali, mai un pulsante "cerca" da premere.
12. Ogni selettore (categoria, data, priorità) propone al massimo 3 opzioni visibili + "altro".
13. Nessuna schermata di attesa tra due passi che potrebbero essere un unico passo.
14. Ogni cattura fallita ha comunque un esito (Inbox), mai un vicolo cieco.
15. Ogni widget consente l'azione primaria senza aprire l'app.
16. Ogni notifica azionabile consente di agire senza aprire l'app.
17. Nessuna azione richiede di attraversare più di 3 livelli di navigazione (P32).
18. Le impostazioni sono un catalogo chiuso: nessuna nuova voce senza passare i 7 cancelli della Feature Philosophy.
19. Nessuna funzione nasconde un'azione comune dietro un menu di terzo livello.
20. Il pulsante di cattura è raggiungibile da ogni schermata con un solo tocco.
21. Ogni flusso guidato (onboarding, revisione) mostra il progresso e consente l'uscita in ogni momento.
22. Ogni form multi-campo ha solo i campi realmente necessari visibili di default; il resto è dietro "altri dettagli" opzionale.
23. Ogni lista supporta azioni rapide (swipe/menu) per le operazioni più comuni senza aprire il dettaglio.
24. Il tempo totale dell'onboarding non supera i 60 secondi mediani.
25. Nessuna funzione richiede di ricordare una sintassi o un comando per essere usata.

## Titolo II — Feedback e tempo (UX-C-026…050)

26. Ogni azione ha un feedback percepibile entro 50ms o mostra un caricamento esplicito oltre i 300ms.
27. Ogni feedback ha almeno un canale visivo, mai solo aptico o solo sonoro.
28. L'aptica ha esattamente 3 livelli (lieve/medio/allerta), mai di più, mai a ogni scroll.
29. Il feedback sonoro è sempre disattivato di default e mai obbligatorio per capire un esito.
30. Ogni microcopy di conferma è concreta ("Salvato", "Fatto"), mai generica ("OK", "Successo").
31. Ogni animazione dura al massimo 350ms.
32. Ogni animazione comunica una relazione spaziale o di stato, mai pura decorazione.
33. Ogni animazione ha una variante statica per "riduci movimento".
34. Le celebrazioni durano al massimo 800ms e sono sempre interrompibili con un tocco.
35. Nessuna notifica push contiene contenuti utente nel payload di sistema.
36. Il pannello di sincronizzazione è l'unico luogo dove la sync si "vede"; altrove è silenziosa.
37. Uno stato "in caricamento" non supera mai il budget di prestazioni della funzione sottostante.
38. Ogni skeleton screen riflette la forma reale del contenuto in arrivo, mai una forma generica.
39. Il tempo di attesa oltre 3 secondi mostra sempre un messaggio testuale, non solo un'icona.
40. Il tempo di attesa oltre 10 secondi (solo funzioni online) mostra una stima e consente di continuare in background.
41. Nessuna schermata mostra uno spinner a schermo pieno.
42. Ogni feedback distingue chiaramente tra "in corso", "riuscito" e "fallito".
43. I toast/snackbar appaiono sempre nella stessa posizione, mai in punti diversi per contesti diversi.
44. Un secondo snackbar sostituisce il primo eseguendo silenziosamente l'azione precedente in coda.
45. Il feedback di errore non usa mai colori allarmanti come primo segnale (l'ambra precede il rosso, riservato a perdita dati/sicurezza).
46. Ogni feedback ha un annuncio equivalente per screen reader (C-art. 99).
47. Il feedback di un'azione su un elemento in lista non richiede lo scroll per essere visto.
48. Ogni valore numerico che cambia (budget, progresso) si anima con una transizione leggibile, mai un salto secco.
49. Nessun conteggio o percentuale è mostrato senza un'unità o un contesto leggibile.
50. Ogni feedback rispetta la lingua e i formati locali (valuta, data, ora) dell'utente.

## Titolo III — Caricamento, stati e degradazione (UX-C-051…070)

51. Ogni schermata gestisce esplicitamente tutti gli stati del catalogo MUC/MFC: vuoto, vuoto filtrato, errore, offline, caricamento, sync, free/trial, archiviato, cestinato, condiviso, sola lettura, degradato.
52. Nessuno stato vuoto è una pagina bianca: sempre illustrazione + beneficio + azione.
53. Lo stato vuoto "mai usato" e lo stato "filtrato senza risultati" non condividono mai la stessa illustrazione.
54. Il fallimento di un sottosistema (es. indice ricerca) degrada solo la funzione coinvolta, mai l'intera app.
55. Ogni degradazione dichiara la propria natura temporanea e la propria auto-riparazione.
56. Il piano Free non blocca mai la lettura o l'export dei contenuti esistenti oltre soglia.
57. Il limite del piano Free si comunica prima che l'utente lo raggiunga, mai come sorpresa.
58. Nessuna icona "PRO"/corona persiste sulle funzioni Plus in uso quotidiano.
59. Lo stato Trial è visibile solo in Impostazioni > Abbonamento, mai nelle schermate d'uso.
60. Il completamento di un rientro da lungo offline collassa le notifiche pendenti in un digest, mai una raffica.
61. Ogni lista virtualizzata su grandi volumi non mostra rallentamenti percepibili oltre i budget dichiarati.
62. Uno stato di conflitto di sincronizzazione non è mai mostrato all'utente come scelta da fare.
63. Un elemento archiviato è visivamente distinto (attenuato) da uno attivo, sempre.
64. Un elemento nel cestino mostra sempre i giorni residui prima della cancellazione definitiva.
65. Un'entità condivisa (futuro) è sempre etichettata come tale, mai ambigua.
66. Un contenuto in sola lettura (evento esterno, dato salute) mostra sempre l'azione "apri nella fonte" al posto dei controlli di modifica.
67. Nessuna schermata mostra contemporaneamente più di uno stato di caricamento sovrapposto.
68. Un errore che impedisce il caricamento di una sezione lascia sempre visibile e operativo il resto della schermata.
69. Nessuno stato "degradato" dura oltre il tempo di auto-riparazione dichiarato senza un aggiornamento visibile.
70. Ogni stato ha un annuncio screen reader distinto (UX-R-021).

## Titolo IV — Errori e recupero (UX-C-071…095)

71. Ogni errore è scritto in linguaggio umano, mai codici tecnici nudi.
72. Ogni errore spiega, quando possibile, il perché in una frase.
73. Ogni errore offre un'azione riparatrice a un tocco.
74. Nessun errore usa un linguaggio che implichi colpa dell'utente.
75. Ogni errore recuperabile lo è per definizione: se non lo è, lo si dichiara onestamente (es. chiave di recupero persa).
76. Ogni retry automatico usa un backoff crescente, mai tentativi immediati a raffica.
77. Ogni funzione offre sempre anche un retry manuale, mai solo quello automatico.
78. Nessun errore di rete blocca funzioni che potrebbero operare offline.
79. I log di errore locali esistono sempre, indipendentemente dal consenso telemetria.
80. La trasmissione remota di log/errori richiede sempre consenso esplicito opt-in.
81. Nessun crash report contiene contenuti utente.
82. Ogni crash all'riapertura non mostra colpa né richiede spiegazioni all'utente: l'app riparte e basta.
83. Ogni scrittura interrotta (kill, crash) non produce mai uno stato ibrido visibile.
84. Ogni draft di testo sopravvive a un crash o a un cambio di navigazione imprevisto.
85. Un errore di validazione resta inline nel campo, mai un dialogo che interrompe il flusso.
86. Un campo obbligatorio mancante non blocca mai la chiusura della schermata: si salva l'ultimo stato valido.
87. Un errore di importazione consente sempre l'annullamento in blocco dopo l'anteprima.
88. Un errore di autenticazione non rivela mai se è l'email o la password ad essere sbagliata.
89. Dopo tentativi di login ripetuti falliti, il backoff cresce ma non blocca mai permanentemente l'utente legittimo.
90. Un errore di spazio disco esaurito non causa mai la perdita del contenuto in composizione.
91. Un errore di piattaforma esterna (calendario, salute) degrada solo quella fonte, mai l'intero modulo.
92. Ogni messaggio d'errore è tradotto e localizzato culturalmente, mai una traduzione letterale incomprensibile.
93. Nessun errore interrompe un'animazione in corso in modo brusco: la transizione verso lo stato di errore è comunque fluida.
94. Ogni errore ripetuto (3+ volte) nella stessa sessione propone un'azione alternativa (es. contatta supporto) invece di ripetere lo stesso messaggio.
95. Nessun errore è mostrato come popup bloccante se può essere mostrato inline senza perdita di informazione.

## Titolo V — Annullamento e reversibilità (UX-C-096…115)

96. Ogni azione distruttiva ha undo immediato via snackbar di 7 secondi.
97. L'undo ripristina lo stato esatto, inclusi posizione, collegamenti e ordine.
98. Un secondo undo in coda esegue silenziosamente il primo, mai li accumula in conflitto.
99. L'eliminazione definitiva è l'unica azione senza undo, e lo dichiara esplicitamente nel proprio dialogo.
100. Ogni cestino conserva gli elementi per 30 giorni prima della cancellazione automatica.
101. Il ripristino da cestino/archivio riporta l'elemento nella posizione logica originale.
102. Disattivare un modulo non elimina mai i suoi dati.
103. Riattivare un modulo restituisce uno stato identico a quello precedente alla disattivazione.
104. Ogni modifica di campo è tracciata in cronologia, consentendo di capire cosa è cambiato e quando.
105. Il ripristino di una versione precedente crea una nuova voce di cronologia, mai riscrive la storia.
106. Nessuna azione irreversibile è raggiungibile con un solo tocco accidentale (richiede conferma o gesto sostenuto).
107. Ogni operazione di importazione è annullabile in blocco prima della conferma finale.
108. Ogni operazione su più di 20 elementi richiede una conferma esplicita del numero coinvolto.
109. La cancellazione dell'account ha un periodo di attesa di sicurezza annullabile (72h) prima dell'esecuzione.
110. Ogni sospensione (pausa abitudine, pausa obiettivo) è reversibile con un tocco, senza penalità.
111. La disdetta di un abbonamento è raggiungibile in ≤ 2 tocchi e non richiede giustificazioni.
112. Ogni collegamento tra entità nel grafo può essere rimosso senza eliminare le entità collegate.
113. L'eliminazione di un'entità con collegamenti sospende i collegamenti, non li distrugge.
114. Ogni azione di massa mostra un'anteprima di ciò che sta per accadere prima dell'esecuzione.
115. Nessuna funzione richiede all'utente di ricordare manualmente uno stato precedente per tornare indietro: il sistema lo ricorda per lui.

## Titolo VI — Navigazione (UX-C-116…140)

116. La profondità massima di navigazione è 3 livelli.
117. La tab bar è sempre composta dalle stesse 4 destinazioni, indipendentemente dai moduli attivi.
118. Il back (gesture o tasto) naviga sempre alla schermata di provenienza reale, mai a una destinazione fittizia.
119. Chiudere un dettaglio riporta esattamente al punto di scroll e filtro della lista di provenienza.
120. Il back di sistema (edge swipe) non è mai intercettato per scopi diversi dalla navigazione.
121. Ogni cronologia di navigazione per tab è indipendente dalle altre tab.
122. Lo stato dell'app sopravvive a 30 minuti in background senza reset.
123. Un flusso modale interrotto (revisione, import) offre sempre di riprendere da dove si era.
124. Nessun link esterno o deep link porta a una schermata rotta se l'entità di destinazione non esiste più.
125. Ogni deep link verso un contenuto non più esistente reindirizza gentilmente alla lista pertinente.
126. Un bottom sheet non si impila mai sopra un altro bottom sheet.
127. Un dialogo è riservato esclusivamente alle azioni irreversibili o ai limiti di piano.
128. Nessuna funzione è raggiungibile solo tramite un percorso non scopribile (sempre almeno un'icona esplicita oltre al gesto).
129. Il FAB di cattura è sempre presente tranne che nei flussi modali a scopo unico.
130. Nessuna modale a scopo unico nasconde la via di uscita esplicita.
131. Ogni modulo è raggiungibile direttamente dall'hub Moduli, mai solo tramite un altro modulo.
132. La navigazione tra sotto-viste segmentate (es. Oggi/Prossimi) avviene con swipe orizzontale e tocco equivalente sui segmenti.
133. Nessuna schermata richiede di ricordare "dove ero prima" per orientarsi: il titolo e il breadcrumb impliciti lo dichiarano sempre.
134. La ricerca è raggiungibile con un solo tocco dalla tab bar, sempre.
135. Nessuna azione di navigazione richiede più tocchi della sua controparte diretta da un'altra schermata.
136. Ogni schermata ha un titolo che comunica immediatamente il proprio scopo (P25).
137. Nessuna icona di navigazione cambia significato tra un contesto e l'altro.
138. Il pulsante indietro non compare mai nelle destinazioni di primo livello (le 4 tab).
139. Nessuna azione di sistema (rotazione, multitasking) altera l'ordine o il contenuto di una lista aperta.
140. Ogni notifica/widget che apre l'app porta direttamente al contenuto pertinente, mai a una schermata intermedia superflua.

## Titolo VII — Gesti (UX-C-141…155)

141. Ogni gesto ha un equivalente a tocco esplicito e scopribile.
142. Il doppio tocco non è mai usato per un significato applicativo distinto dal tocco singolo.
143. Il triplo tocco non è mai intercettato dall'app (riservato alle funzioni di accessibilità di sistema).
144. Il long press attiva un menu contestuale solo dopo una soglia di immobilità che lo distingue dallo scroll.
145. Lo swipe di eliminazione usa sempre un colore neutro, mai rosso allarmante per default.
146. Il drag & drop ha sempre un'alternativa non gestuale (frecce, menu "sposta").
147. Il pinch/zoom è riservato ai contenuti visivi che lo richiedono naturalmente (immagini, grafici).
148. I grafici hanno sempre un equivalente testuale/tabellare raggiungibile senza gesti.
149. Lo scroll è sempre inerziale e mai intercettato per significati diversi dalla navigazione del contenuto.
150. Nessun gesto personalizzato inizia nella zona riservata al back-gesture di sistema.
151. Ogni gesto a soglia (swipe, drag) fornisce feedback continuo di avvicinamento alla soglia.
152. La pressione mantenuta (hold) per la registrazione vocale ha sempre un'alternativa tap-to-toggle.
153. Nessuna funzione critica dipende da un gesto a più dita come unico accesso.
154. Ogni gesto è documentato con la propria motivazione, mai aggiunto per moda.
155. Il conflitto tra gesti su assi diversi (swipe orizzontale vs scroll verticale) non produce mai un'attivazione involontaria.

## Titolo VIII — Contenuto, tono e microcopy (UX-C-156…180)

156. Il linguaggio dell'interfaccia è quotidiano, mai gergale o tecnico non spiegato.
157. Nessuna frase dell'interfaccia richiede una spiegazione esterna per essere compresa.
158. Il tono è quello di un alleato competente: mai paternalistico, mai colpevolizzante, mai eccessivamente entusiasta.
159. Nessun messaggio usa più di un punto esclamativo, e la maggior parte non ne usa nessuno.
160. Le microcopy di conferma sono concrete e specifiche, mai generiche.
161. Nessun messaggio implica che l'utente abbia fallito (streak, budget, abitudini).
162. Ogni proposta del sistema (ridimensionamento, suggerimento) usa un linguaggio di invito, mai di imposizione.
163. Le proiezioni finanziarie dichiarano sempre l'ipotesi su cui si basano.
164. Nessuna correlazione statistica è presentata come causalità.
165. Il copy è scritto o rivisto da chi cura la voce del prodotto per ogni release (P107).
166. Nessuna schermata usa sigle non spiegate all'utente.
167. I messaggi di errore non ripetono mai la stessa frase generica per situazioni diverse.
168. Ogni numero mostrato ha un'unità di misura o un contesto esplicito.
169. Le date relative ("ieri", "tra 2 giorni") sono usate quando aumentano la comprensione, mai quando la riducono.
170. Il linguaggio dei limiti di piano è sempre onesto su cosa è incluso e cosa no.
171. Nessuna microcopy usa urgenza artificiale ("ultime ore", "solo oggi") se non vera.
172. I testi legali (privacy, termini) non nascondono mai ciò che il marketing dichiara.
173. Ogni traduzione è localizzata culturalmente, non solo linguisticamente.
174. Il vocabolario è identico per lo stesso concetto attraverso tutti i moduli (P83).
175. Nessuna schermata usa metafore che non siano immediatamente comprensibili (test Franca, §12.8).
176. Le etichette dei pulsanti descrivono l'azione, mai un generico "OK"/"Continua" quando è possibile essere specifici.
177. Ogni celebrazione usa un linguaggio proporzionato all'evento, mai iperbolico.
178. Nessun messaggio confronta l'utente con altri utenti.
179. Ogni messaggio di ridimensionamento (abitudini) offre almeno 2 alternative concrete, mai solo "rinuncia".
180. Il microcopy di uno stato vuoto comunica sempre il beneficio della funzione, non la sua assenza.

## Titolo IX — Accessibilità (UX-C-181…205)

181. Ogni schermata è pienamente navigabile con screen reader.
182. Ogni elemento interattivo ha un'etichetta descrittiva e un ruolo corretto.
183. L'ordine di lettura per screen reader corrisponde all'ordine logico visivo.
184. Ogni cambio di stato ha un annuncio equivalente per screen reader.
185. Ogni funzione è utilizzabile con Switch Control in un numero ragionevole di passi.
186. Nessuna azione temporizzata esclude gli utenti più lenti nell'interazione.
187. I target di tocco sono sempre ≥ 44×44pt/48×48dp, inclusi widget e notifiche.
188. Il contrasto rispetta AA (4.5:1 testo normale, 3:1 testo grande/componenti) su ogni schermata.
189. Nessuna informazione è veicolata dal solo colore.
190. Nessuna informazione è veicolata dal solo movimento.
191. Ogni animazione rispetta "riduci movimento" con una variante statica.
192. Ogni layout regge il 200% di ingrandimento testo senza perdita di funzioni.
193. Il layout usa il reflow, mai il troncamento, per il testo ingrandito.
194. L'ordine di tabulazione per tastiera esterna coincide con l'ordine logico.
195. L'indicatore di focus per tastiera è sempre visibile.
196. Ogni gesto ha un'alternativa accessibile documentata.
197. Ogni grafico ha una descrizione testuale e una tabella dati equivalente.
198. Il linguaggio dell'interfaccia rispetta l'accessibilità cognitiva (frasi brevi, poche opzioni).
199. La cattura vocale è utilizzabile end-to-end senza alcun tocco.
200. Nessuna funzione richiede simultaneamente più gesti complessi (es. pinch + drag) senza alternativa a passo singolo.
201. I moduli sensibili offuscati (biometria) restano comprensibili per screen reader ("contenuto protetto, sblocca per vedere").
202. Ogni release supera un audit di accessibilità automatico e manuale prima della pubblicazione.
203. Gli utenti reali con disabilità sono coinvolti nei test almeno due volte l'anno.
204. Una responsabilità formale per l'accessibilità può bloccare una release.
205. Nessuna funzione della Functional Bible è esente dalla Accessibility Bible.

## Titolo X — Notifiche e attenzione (UX-C-206…225)

206. Ogni notifica deve valere l'interruzione che causa.
207. Le notifiche di sistema/insight rispettano un budget giornaliero configurabile.
208. I promemoria espliciti dell'utente non consumano il budget delle notifiche di sistema.
209. Le notifiche non urgenti si raggruppano in digest, mai una raffica.
210. Gli orari di silenzio (notte, focus di sistema) sono rispettati per default.
211. Una notifica ignorata 3 volte propone la propria disattivazione.
212. Ogni notifica è azionabile senza dover aprire l'app, dove tecnicamente possibile.
213. Nessuna notifica contiene contenuti sensibili in chiaro senza consenso esplicito dell'utente.
214. Nessuna notifica è di puro re-engagement emotivo ("ci manchi").
215. Nessuna notifica promuove upsell o marketing.
216. Ogni categoria di notifica è disattivabile singolarmente dal centro notifiche.
217. Un rientro da offline prolungato collassa le notifiche pendenti in un unico digest "mentre eri via".
218. Il permesso di notifiche è richiesto solo in contesto, mai al primo avvio.
219. Ogni notifica scaduta di significato (promemoria per un'ora passata) non viene mostrata a app riaperta.
220. Le notifiche multi-device si sincronizzano nell'azione: agire su una le rimuove dalle altre.
221. Nessuna notifica interrompe un flusso attivo dell'utente con un popup bloccante.
222. Il badge sull'icona app riflette solo elementi che richiedono attenzione reale dell'utente (es. Inbox), mai contatori di vanità.
223. Ogni notifica rispetta il fuso orario e l'ora legale corrente del dispositivo.
224. Le notifiche non duplicano mai lo stesso evento due volte.
225. Ogni notifica ha priorità dichiarata (promemoria utente / utile / informativa) che ne determina il trattamento nel budget.

## Titolo XI — Dati e fiducia (UX-C-226…250)

226. Ogni azione di creazione/modifica è confermata solo dopo persistenza locale reale.
227. Nessuna azione utente è mai bloccata in attesa della sincronizzazione.
228. La sincronizzazione multi-dispositivo converge sempre senza mostrare conflitti all'utente.
229. Ogni backup è verificato: un backup non testato non è mostrato come "sicuro".
230. Il ripristino su nuovo dispositivo rende l'app utilizzabile entro 2 minuti per i dati recenti.
231. L'export dei dati è raggiungibile in ogni momento, completo, in formati aperti.
232. Nessun limite di piano si applica all'export o al backup.
233. La cancellazione dell'account è verificabile e confermata via comunicazione esplicita.
234. Ogni permesso di sistema è richiesto con la spiegazione del beneficio, mai in blocco.
235. Il rifiuto di un permesso non blocca mai il resto dell'app.
236. Nessun dato sensibile appare in notifiche, widget o screenshot recenti senza consenso esplicito.
237. La pagina "I tuoi dati" è sempre raggiungibile e aggiornata.
238. Ogni modulo sensibile (Finanze, Salute) può essere protetto da sblocco biometrico separato.
239. Nessuna schermata mostra dati in chiaro con l'app bloccata.
240. Il registro dispositivi consente sempre la revoca remota.
241. La chiave di recupero è verificata al setup con un controllo attivo, non solo dichiarata.
242. Nessuna funzione richiede l'invio di contenuti utente a un server per operare (salvo le eccezioni online dichiarate).
243. Ogni telemetria è opt-in e visibile in un elenco comprensibile di cosa viene raccolto.
244. Nessuna ricerca è eseguita lato server.
245. I dati di salute di piattaforma non transitano mai sul nostro cloud.
246. Ogni cronologia di modifica è consultabile dall'utente per ogni entità.
247. Nessuna modifica ai dati avviene "in silenzio" senza tracciabilità in cronologia.
248. Il downgrade di piano non blocca mai la lettura dei dati eccedenti la nuova soglia.
249. Ogni informazione su "cosa sappiamo" è raggiungibile senza dover contattare il supporto.
250. Nessuna dark pattern induce l'utente a concedere più permessi di quelli necessari alla funzione richiesta.

## Titolo XII — Onboarding e scoperta (UX-C-251…270)

251. L'onboarding non richiede mai account, permessi o pagamento.
252. Il tempo mediano di onboarding non supera i 60 secondi.
253. La prima cattura dell'onboarding è reale, mai simulata.
254. Nessun tutorial a più schermate non azionabili.
255. La scoperta delle funzioni è contestuale, mai frontale.
256. Un solo suggerimento contestuale per sessione.
257. Ogni suggerimento ignorato non si ripete prima di 2 settimane.
258. Ogni modulo ha un micro-onboarding di massimo 3 schermate.
259. Ogni micro-onboarding di modulo termina con un elemento reale creato, mai un placeholder.
260. La richiesta di registrazione appare solo dopo un momento di valore percepito.
261. La richiesta di recensione appare solo dopo un momento di valore compiuto, mai nei primi 7 giorni.
262. Nessuna raccolta di dati di profilo non essenziali durante l'onboarding.
263. Il primo successo (prima cattura, prima spunta) riceve lo stesso trattamento di feedback di ogni successo successivo, mai una celebrazione sproporzionata.
264. Gli stati vuoti didattici usano dati d'esempio interattivi, mai screenshot statici.
265. La proposta del secondo modulo si basa su segnali d'uso reali, non su un timer arbitrario.
266. Nessuna notifica push è inviata durante l'onboarding stesso.
267. Il rituale della revisione settimanale è proposto, mai imposto, dopo la prima settimana d'uso.
268. Il digest e gli insight compaiono solo quando i dati li giustificano statisticamente.
269. Nessun paywall a schermo intero appare prima che il valore sia stato dimostrato.
270. La schermata di benvenuto comunica il beneficio in una sola frase.

## Titolo XIII — Business e monetizzazione nell'esperienza (UX-C-271…290)

271. Nessuna pubblicità è mai mostrata nell'esperienza.
272. Ogni limite di piano è comunicato prima del raggiungimento, mai come sorpresa.
273. Il paywall dichiara sempre contenuto, prezzo e modalità di disdetta prima del pagamento.
274. La disdetta è raggiungibile in ≤ 2 tocchi, senza percorsi di trattenimento.
275. Nessun upsell appare in un momento di vulnerabilità emotiva dell'utente (fallimento, sforamento budget).
275bis. Il trial è visibile solo in Impostazioni, mai nell'esperienza quotidiana.
276. Nessuna funzione di sicurezza o backup è mai dietro paywall.
277. Il downgrade di piano offre sempre una scelta esplicita e non forzata su quali moduli mantenere attivi.
278. Nessuna urgenza artificiale (countdown perenni) è usata per spingere la conversione.
279. Ogni promozione rispetta il limite di una offerta ricorrente l'anno.
280. Il confronto tra piani è sempre onesto e completo, mai selettivo per convenienza.
281. Nessuna icona commerciale (corona, badge Pro) appare ripetutamente nell'uso quotidiano del piano a pagamento.
282. Gli aumenti di prezzo non sono mai retroattivi sugli abbonati esistenti.
283. Ogni rimborso ragionevole è gestito con generosità, non con resistenza.
284. Nessun insight o suggerimento propone l'upgrade come contenuto del messaggio stesso.
285. Il marketplace (futuro) applica le stesse regole UX di questa Constitution a ogni plugin di terze parti.
286. Nessuna funzione del piano gratuito è resa artificialmente più lenta o scomoda per spingere l'upgrade.
287. La conversione avviene nel momento di massimo valore percepito (es. attivazione del 3° modulo), mai nel momento di massima frustrazione.
288. Ogni fattura e ricevuta è raggiungibile in un tocco dalle Impostazioni.
289. Nessuna dark pattern di "opt-out nascosto" è presente in nessun flusso commerciale.
290. Il valore del piano a pagamento è sempre spiegabile in una frase vera e verificabile dall'utente.

## Titolo XIV — Coerenza visiva e qualità (UX-C-291…312)

291. Un solo design system, nessuna eccezione visiva per modulo.
292. Ogni componente nuovo nasce solo se la libreria esistente non ha un equivalente.
293. Dark mode e light mode sono progettate insieme, mai una derivata dall'altra.
294. Ogni schermata comunica un solo concetto principale.
295. Ogni elemento visibile giustifica la propria presenza (nessun elemento "di nessuno").
296. La tipografia segue una scala fissa a 7 gradini con contrasto pieno.
297. Ogni icona appartiene a un solo set coerente, stroke uniforme.
298. Ogni stato limite (0 elementi, 10.000 elementi, testo lunghissimo) è progettato esplicitamente, mai subito.
299. Il layout non sposta mai un elemento sotto il dito dell'utente durante l'interazione.
300. La qualità percepita si verifica sul dispositivo di riferimento più datato, non sul flagship.
301. Nessuna release è pubblicata senza il superamento dei budget di prestazione dichiarati (RNF-P*).
302. Ogni flusso ha un budget di tocchi verificato in QA prima del rilascio.
303. Ogni nuova funzione della Functional Bible ha una scheda UX completa prima di entrare in sviluppo.
304. Nessuna schermata viola la profondità massima di 3 livelli.
305. Ogni test di usabilità con utenti reali precede il rilascio dei flussi core.
306. Il task success rate sui flussi core è verificato ≥ 90% prima del lancio.
307. Ogni deroga a questa Constitution è registrata nel Decision Log con motivazione esplicita.
308. Nessuna regola di questa Constitution è modificabile senza il processo di emendamento della Product Constitution (Titolo X).
309. Ogni membro del team può citare una regola per numero per bloccare una decisione in contrasto.
310. Le regole qui elencate si applicano retroattivamente a ogni funzione esistente al momento della loro introduzione.
311. Questa Constitution è verificata a ogni release quanto la Product Constitution.
312. In assenza di una regola applicabile, si applica il principio guida: *"Che cosa farebbe questa scelta al tempo e alla fiducia dell'utente?"* — e la risposta si registra come proposta di nuova regola.

---

*Prossimo: [Matrici](14-matrici.md)*
