# 15 · Product Constitution

> **Il documento supremo del prodotto. 215 articoli.**
>
> Natura: la Constitution non è una guida — è un vincolo. Nessun obiettivo di business, nessuna metrica, nessuna scadenza, nessun cliente e nessun fondatore può giustificarne la violazione. Chi rifiuta un compito citando un articolo ha ragione per definizione, finché l'articolo non viene emendato con il processo del Titolo X.
>
> Interpretazione: in caso di dubbio tra due letture, prevale quella più protettiva per l'utente. In caso di conflitto tra articoli, prevale l'articolo del Titolo con numero minore (i dati prima del design, il design prima del business).

---

## Titolo I — Dati e proprietà (art. 1–25)

1. I dati dell'utente appartengono all'utente. Sempre, tutti, senza eccezioni.
2. Mai progettare un sistema in cui noi — o chiunque altro — possiamo leggere i contenuti dell'utente.
3. Mai perdere un dato confermato all'utente. Questa è la regola zero: ogni altra proprietà del sistema le è subordinata.
4. Mai raccogliere un dato che non serve a una funzione visibile all'utente.
5. Mai raccogliere dati "perché potrebbero servire".
6. Ogni dato raccolto deve essere elencabile, spiegabile e mostrabile all'utente in linguaggio semplice.
7. L'export completo dei dati è un diritto permanente: tutti i dati, formati aperti, in ogni momento, gratuitamente.
8. Mai degradare, nascondere o rallentare l'export per trattenere l'utente.
9. La cancellazione richiesta dall'utente è definitiva, completa e verificabile — inclusi i backup, nei tempi dichiarati.
10. Mai vendere, affittare, condividere o barattare dati dell'utente. Con nessuno. Per nessuna cifra.
11. Mai usare i dati dell'utente per addestrare modelli senza consenso esplicito, specifico e revocabile — e mai contenuti fuori dal dispositivo in ogni caso.
12. Mai trasferire dati a terzi se non per fornire il servizio richiesto, con contratti che vincolano il terzo alle nostre stesse regole.
13. Le cancellazioni accidentali devono essere reversibili per almeno 30 giorni.
14. Ogni operazione distruttiva richiede o reversibilità o conferma forte — mai nessuna delle due.
15. Il dispositivo dell'utente è la fonte primaria di verità; il cloud è un servizio al dispositivo.
16. Mai richiedere la rete per accedere ai propri dati.
17. Mai tenere in ostaggio i dati per un pagamento: alla scadenza di un abbonamento, i dati restano leggibili ed esportabili per sempre.
18. I backup esistono, sono automatici, sono cifrati e sono testati. Un backup non testato non è un backup.
19. Il ripristino completo su nuovo dispositivo è un diritto e deve essere semplice.
20. Mai formati proprietari come unico formato: ogni formato di export è documentato pubblicamente.
21. I metadati sono dati: godono delle stesse protezioni dei contenuti, nella massima misura tecnica possibile.
22. Mai log, crash report o diagnostica che contengano contenuti dell'utente.
23. La telemetria è opt-in, anonima, aggregabile, elencata pubblicamente — e la sua assenza non degrada il prodotto.
24. Se il servizio dovesse chiudere: 12 mesi di preavviso, strumenti di export potenziati fino all'ultimo giorno, e rilascio della documentazione necessaria a leggere i formati.
25. Nessun articolo di questo Titolo è derogabile per richiesta commerciale, governativa non dovuta per legge, o emergenza aziendale.

## Titolo II — Privacy e sicurezza (art. 26–50)

26. La privacy si implementa in architettura, non si dichiara in policy.
27. La crittografia end-to-end dei contenuti è irrinunciabile e non retrocedibile.
28. Mai una backdoor. Nemmeno "amministrativa". Nemmeno "temporanea". Nemmeno "per il bene dell'utente".
29. Mai crittografia inventata in casa: solo primitive standard, implementazioni auditate, design rivisto da terzi.
30. La sicurezza non è mai una feature premium: il piano gratuito ha la stessa crittografia, gli stessi backup, la stessa protezione.
31. La biometria protegge l'accesso locale; i dati biometrici non lasciano mai l'hardware di sistema e non li tocchiamo mai.
32. Ogni permesso di sistema è chiesto nel momento del bisogno, con la spiegazione del beneficio, mai preventivamente.
33. Il prodotto funziona — ridotto ma degno — anche quando ogni permesso è negato.
34. Mai accesso alla rubrica, al microfono, alla posizione o ad altri sensori senza una funzione esplicitamente richiesta dall'utente che li usa davanti ai suoi occhi.
35. Mai tracciamento pubblicitario, mai identificatori di advertising, mai SDK di ad-tech nel binario.
36. Mai fingerprinting degli utenti.
37. Le schermate sensibili offrono protezione da sguardi e screenshot, a scelta dell'utente.
38. Ogni violazione o incidente di sicurezza è comunicato agli utenti coinvolti con tempestività, chiarezza e senza minimizzazioni — oltre gli obblighi di legge, non al loro minimo.
39. Il whitepaper di sicurezza è pubblico, aggiornato e scritto per essere capito.
40. I ricercatori di sicurezza in buona fede sono alleati: canale di disclosure sempre aperto, mai ritorsioni.
41. Ogni nuova feature passa una revisione privacy prima del rilascio; le feature ad alto rischio, una valutazione d'impatto formale.
42. La conformità normativa (GDPR e equivalenti) è il pavimento, mai il soffitto.
43. Mai spostare dati in giurisdizioni che indebolirebbero le protezioni promesse.
44. I sub-fornitori rispettano contrattualmente questi standard o non sono nostri fornitori.
45. Il principio di minimizzazione governa ogni progettazione: il dato migliore è quello che non serve raccogliere.
46. Mai correlare account e comportamenti tra utenti per profilazione.
47. L'anonimato aggregato nei report pubblici deve essere robusto: nessun individuo ricostruibile, mai.
48. Le chiavi dell'utente sono dell'utente: la perdita delle credenziali e della chiave di recupero comporta la perdita dei dati, e questo trade-off è dichiarato con onestà brutale prima che accada.
49. Ogni sistema di condivisione futura (famiglia, coppia) condivide il minimo scelto esplicitamente, mai "tutto per comodità".
50. In ogni conflitto tra sicurezza e convenienza, vince la sicurezza; tra sicurezza e usabilità, si riprogetta finché non vincono entrambe.

## Titolo III — Rispetto psicologico e attenzione (art. 51–80)

51. Mai progettare interfacce che inducano dipendenza.
52. Mai ricompense variabili, loop compulsivi, feed infiniti o meccaniche da slot machine.
53. Mai usare la vergogna, la colpa o la paura come leve di engagement o di vendita.
54. Mai punire l'utente per un giorno storto: nessun contatore che si azzera, nessun progresso che si "perde".
55. Mai confronto sociale pubblico: classifiche, streak altrui, prestazioni comparate non esistono in questo prodotto.
56. Il tempo dell'utente è suo: il prodotto è progettato per sessioni brevi e uscite rapide.
57. Il tempo speso in app non è mai un obiettivo aziendale, di team o personale.
58. Ogni notifica deve valere l'interruzione che causa; in dubbio, non si manda.
59. Le notifiche hanno un budget quotidiano rispettato dal sistema, non dalla buona volontà.
60. Mai notifiche di puro re-engagement ("ci manchi!") prive di contenuto utile.
61. Mai due funzioni per lo stesso scopo: la duplicazione è confusione.
62. Il silenzio notturno e i momenti di focus dell'utente sono sacri per default.
63. Una notifica ignorata ripetutamente propone la propria disattivazione: rispettiamo anche i no non detti.
64. Mai interrompere un'azione dell'utente con richieste nostre (recensioni, upsell, novità): i nostri messaggi aspettano il momento giusto.
65. Le richieste di recensione avvengono solo dopo momenti di valore compiuto, con frequenza minima e rinuncia permanente possibile.
66. La psicologia comportamentale si usa solo a favore di obiettivi che l'utente ha dichiarato: mai per obiettivi nostri che l'utente non condivide.
67. L'automazione propone; l'utente dispone. Mai azioni automatiche irreversibili senza consenso.
68. Le celebrazioni sono sobrie, brevi e mai bloccanti.
69. Il tono del prodotto è quello di un alleato competente: mai paternalistico, mai colpevolizzante, mai infantilizzante.
70. Mai dark pattern. L'elenco dei dark pattern noti è mantenuto e ogni sua voce è un divieto specifico.
71. Mai opt-out nascosti, caselle pre-spuntate a nostro favore, o percorsi di rinuncia più lunghi dei percorsi di adesione.
72. Annullare, rinunciare, disattivare e uscire sono sempre facili quanto le azioni opposte.
73. Il rosso e i segnali d'allarme sono riservati a perdita di dati e sicurezza: mai per spingere comportamenti.
74. Ogni segnale negativo mostrato all'utente include una via d'uscita concreta e gentile.
75. Ricominciare da capo è sempre possibile, gratuito e privo di giudizio.
76. Mai sfruttare momenti di vulnerabilità emotiva (fallimenti, sforamenti, ricadute) per vendere o trattenere.
77. Il benessere dichiarato dal prodotto non può essere contraddetto dalle sue meccaniche: coerenza tra promessa e incentivi.
78. Gli utenti minorenni, ove presenti, godono di protezioni rafforzate e di zero monetizzazione comportamentale.
79. L'accessibilità cognitiva è rispetto psicologico: linguaggio semplice, opzioni limitate, mai sovraccarico.
80. Se una meccanica aumenta una metrica ma mette ansia, la meccanica è sbagliata — e la metrica pure.

## Titolo IV — Semplicità, design e accessibilità (art. 81–115)

81. Mai sacrificare la semplicità per aggiungere funzionalità.
82. Ogni schermata comunica un solo concetto principale.
83. Mai introdurre complessità non giustificata da un job documentato.
84. Ogni funzione deve avere un motivo scritto per esistere prima di esistere.
85. Ogni funzione deve essere scopribile nel contesto in cui serve.
86. La rivelazione progressiva è la regola: il semplice davanti, il potente dietro, il superfluo da nessuna parte.
87. I default sono decisioni nostre, prese bene: mai scaricare le scelte sull'utente per non decidere.
88. Ogni impostazione aggiunta è una sconfitta parziale del design; le impostazioni si contano e si contingentano.
89. Mai gerarchie di navigazione oltre tre livelli.
90. Mai più di tre opzioni in una scelta proposta dal prodotto.
91. Il gergo è vietato; le sigle non spiegate sono vietate; il linguaggio è quotidiano.
92. Mai testo che un utente di seconda media non capirebbe, salvo termini tecnici richiesti dal dominio e allora spiegati.
93. Il design è senza tempo: mai inseguire trend visivi; in dubbio, la scelta più semplice e duratura.
94. Un solo design system, nessuna eccezione per modulo, nessun componente duplicato.
95. Dark mode e light mode nascono insieme e sono pari in qualità.
96. Le animazioni comunicano struttura e stato; mai decorazione fine a sé stessa; sempre disattivabili.
97. Mai informazione trasmessa dal solo colore o dal solo movimento.
98. L'accessibilità (WCAG 2.2 AA minimo) è requisito di release: ciò che non è accessibile non si rilascia.
99. Ogni funzione è utilizzabile con screen reader, tastiera, controlli alternativi e comandi vocali.
100. Ogni gesto ha un equivalente visibile e raggiungibile.
101. Il layout regge l'ingrandimento del testo al 200% senza perdita di funzioni.
102. I target di tocco rispettano le dimensioni minime ovunque, widget e notifiche inclusi.
103. Mai stati vuoti muti: ogni schermata vuota insegna e invita.
104. Gli errori parlano da umani: che cosa è successo, perché, che cosa fare — mai codici nudi, mai colpe all'utente.
105. I casi limite (zero dati, dati enormi, testi lunghi, offline, batterie scariche) sono progettati, non subiti.
106. La coerenza tra piattaforme è di significato: nativa nel comportamento, riconoscibile nell'identità.
107. Ogni parola dell'interfaccia è scritta o rivista da chi cura la voce del prodotto: il copy è design.
108. La localizzazione è culturale, non solo linguistica: formati, esempi, convenzioni.
109. Mai screenshot di marketing che mostrino un prodotto migliore di quello vero.
110. Il carico visivo si misura: ogni elemento sullo schermo giustifica la propria presenza.
111. Le viste dense esistono solo come scelta esplicita di utenti esperti, mai come default.
112. La qualità percepita si giudica sul dispositivo medio di cinque anni fa, non sul flagship del designer.
113. Mai spostare elementi dell'interfaccia sotto il dito dell'utente (layout shift durante l'interazione).
114. La familiarità è un patrimonio: i cambiamenti di interfaccia si fanno per il bene dell'utente, si annunciano, e mai per il solo gusto del nuovo.
115. Se una schermata richiede un tutorial, la schermata è sbagliata.

## Titolo V — Affidabilità e prestazioni (art. 116–135)

116. La lentezza è un bug con la stessa dignità di un crash.
117. L'avvio dell'app è sacro: il budget di avvio si difende a ogni release.
118. Ogni azione quotidiana risponde entro 50 millisecondi percepiti o è considerata difettosa.
119. Mai un'animazione che scatta, su nessun dispositivo supportato.
120. Offline non è una modalità: è lo stato normale di progetto. Ogni funzione nasce offline.
121. La sincronizzazione riconcilia da sola: mai chiedere all'utente quale versione tenere.
122. Il degrado è sempre parziale: il guasto di un componente non ferma mai il resto.
123. L'app resta pienamente utilizzabile durante qualsiasi indisponibilità dei nostri server.
124. La batteria e i dati mobili dell'utente sono risorse sue: consumo minimo dimostrato, mai polling, mai spreco.
125. La memoria occupata è un costo per l'utente: i budget per modulo si rispettano.
126. Le dimensioni dell'app si sorvegliano: ogni megabyte si giustifica.
127. Mai rilasciare senza che i percorsi del disastro (kill, disco pieno, migrazione interrotta) siano testati.
128. I dispositivi vecchi si supportano finché una quota significativa di utenti li usa; l'abbandono di un OS si annuncia con largo anticipo.
129. Ogni release può essere ritirata: rollout graduale e interruttori di emergenza sempre.
130. Mai esperimenti A/B che degradino sicurezza, prestazioni o accessibilità per un gruppo.
131. Il monitoraggio della qualità è continuo e i suoi allarmi hanno precedenza sul lavoro pianificato.
132. Un bug che perde dati ferma la linea: tutto il team, finché non è risolto e capito.
133. Le post-mortem sono senza colpa, scritte e pubbliche all'interno: si processa il sistema, mai la persona.
134. Mai due volte lo stesso incidente per la stessa causa non corretta.
135. La promessa di prestazioni vale per il novantesimo percentile, non per la media.

## Titolo VI — Onestà e comunicazione (art. 136–155)

136. Mai mentire. Nemmeno per omissione. Nemmeno nel marketing. Nemmeno nelle note di rilascio.
137. Mai promettere una feature con date che non sappiamo mantenere; la roadmap pubblica distingue intenzioni da impegni.
138. I limiti del prodotto si dichiarano: ciò che non facciamo, ciò che non facciamo ancora, ciò che non faremo mai.
139. I prezzi sono visibili prima di ogni impegno; il costo totale è chiaro; le condizioni stanno in un paragrafo leggibile.
140. Mai testo legale usato per nascondere ciò che il testo di marketing tace.
141. Le recensioni negative ricevono risposte oneste, mai template difensivi.
142. Gli errori nostri si ammettono per primi, si correggono in fretta e si compensano quando c'è danno.
143. Mai comprare recensioni, follower, classifiche o testimonianze.
144. Mai denigrare i concorrenti: i confronti sono fattuali, verificabili e rispettosi.
145. Mai statistiche gonfiate, grafici ingannevoli o numeri senza contesto nelle nostre comunicazioni.
146. La scienza si cita solo quando regge: mai "studi dimostrano" senza studi che dimostrino.
147. Mai urgenza artificiale: countdown finti, scorte finte, offerte "ultime ore" perenni.
148. Ogni promessa pubblica ha un proprietario interno e una verifica periodica.
149. Il report annuale di trasparenza dice come stiamo mantenendo le promesse — incluse quelle mantenute male.
150. Le richieste governative di dati si gestiscono con il massimo rigore legale e si rendicontano pubblicamente nei limiti di legge (e per architettura, c'è poco da consegnare: anche questo si dichiara).
151. Il supporto dice la verità anche quando è scomoda ("è un nostro bug, non abbiamo ancora la soluzione").
152. Mai fingere che una rimozione sia un miglioramento: si spiega il perché vero.
153. La comunicazione di crisi è rapida, fattuale e guidata dall'interesse dell'utente, non dell'immagine.
154. Ogni parola pubblica rispetta il tono del prodotto: alleato competente, mai venditore d'assalto.
155. Se non possiamo dirlo con orgoglio, non dobbiamo farlo.

## Titolo VII — Business e monetizzazione (art. 156–180)

156. Il cliente è l'utente. Nessun terzo potrà mai comprare priorità sulla sua esperienza.
157. Mai pubblicità nel prodotto. Di nessun tipo. Nemmeno "native". Nemmeno "rispettose".
158. Mai vendere dati (ribadito qui perché le pressioni arriveranno da questo Titolo).
159. Mai monetizzare la paura: sicurezza, backup, protezione e integrità dei dati non hanno prezzo di listino.
160. Il piano gratuito è genuinamente utile per sempre: mai degradarlo retroattivamente per spingere l'upgrade.
161. Mai spostare nel piano a pagamento funzioni che erano gratuite (grandfathering sempre).
162. Il paywall dice tutta la verità: contenuto, prezzo, rinnovo, disdetta — prima del pagamento.
163. Disdire è facile quanto abbonarsi: stessi tocchi, stessa evidenza, zero colpevolizzazione.
164. Mai trial che si converte in pagamento senza un avviso chiaro e tempestivo prima dell'addebito.
165. I rimborsi ragionevoli si concedono con generosità: un utente rimborsato bene vale più di un pagamento trattenuto male.
166. Gli aumenti di prezzo non sono mai retroattivi e si annunciano con almeno 60 giorni di anticipo.
167. Mai offerte che puniscono la fedeltà (prezzi migliori solo ai nuovi, per sempre).
168. Mai upsell nei momenti di vulnerabilità o fallimento dell'utente.
169. Mai più di un'offerta promozionale ricorrente l'anno: la scarsità è vera o non è.
170. La conversione si guadagna per desiderio, mai per ricatto, frustrazione indotta o limiti artificiosi crudeli.
171. Il valore del piano a pagamento deve essere spiegabile in una frase vera.
172. Mai modelli di ricavo che creino conflitto d'interesse con il benessere dell'utente (engagement-based, data-based, ads-based).
173. Nessun cliente enterprise o partner può ottenere accesso a dati o deroghe alla Constitution: il contratto che lo chiedesse si rifiuta.
174. Nel B2B2C chi paga non vede: l'azienda compra il benefit, mai la sorveglianza.
175. Il marketplace eredita integralmente questa Constitution: ogni plugin la rispetta o non esiste.
176. Il revenue share con gli sviluppatori è generoso per progetto: l'ecosistema è un'alleanza, non una rendita.
177. Mai acquisizioni o investitori che richiedano di violare questo documento; il term sheet che lo chiede si declina.
178. La crescita dei ricavi non giustifica mai il debito reputazionale: il brand è il compounding più lento e più prezioso.
179. I numeri comunicati agli investitori sono gli stessi che guardiamo noi.
180. Se il modello di business dovesse cambiare, cambia con un emendamento pubblico a questo Titolo — mai in silenzio.

## Titolo VIII — Modularità ed ecosistema (art. 181–195)

181. Ogni modulo deve essere utile da solo e più utile insieme agli altri.
182. Mai penalizzare chi usa un solo modulo.
183. Attivare un modulo è un invito; disattivarlo è un diritto; disattivare non cancella mai nulla.
184. I moduli non dipendono l'uno dall'altro: comunicano per contratti, mai per scorciatoie.
185. Nessun modulo può violare i budget di prestazioni, superficie o attenzione: l'ecosistema ha regole di condominio.
186. Il vocabolario è unico attraverso i moduli: stessa parola, stesso concetto, ovunque.
187. Un nuovo modulo nasce solo per un job documentato che nessun modulo esistente può servire.
188. Le integrazioni esterne arricchiscono i moduli senza creare dipendenza dalla rete o da terzi.
189. I plugin di terze parti girano in sandbox con permessi espliciti, revocabili e comprensibili.
190. La review del marketplace protegge gli utenti prima degli sviluppatori, e gli sviluppatori onesti prima di noi.
191. Mai plugin che raccolgano dati oltre i permessi dichiarati: la violazione comporta rimozione e comunicazione agli utenti coinvolti.
192. Gli sviluppatori dell'ecosistema ricevono: documentazione vera, regole stabili, review rapide, trattamento equo e ricorso in caso di disaccordo.
193. Mai competere slealmente con i plugin dell'ecosistema usando dati che solo noi vediamo (non ne vediamo, per architettura — e resti così).
194. Le API pubbliche si versionano e si deprecano con preavvisi lunghi: chi costruisce su di noi merita stabilità.
195. L'ecosistema cresce alla velocità della qualità, mai alla velocità del numero.

## Titolo IX — Team e processo (art. 196–210)

196. Chi cita questa Constitution ha ragione finché la Constitution non cambia — anche contro il fondatore.
197. Nessuno sarà mai penalizzato per aver difeso un articolo di questo documento.
198. Le decisioni importanti si registrano nel Decision Log: una decisione non scritta non esiste.
199. Il disaccordo si argomenta con principi e dati, mai con gerarchia o anzianità.
200. Ogni funzione dichiara la sua metrica di successo prima di essere costruita e affronta il suo giudizio alla data stabilita.
201. La rimozione è una feature: ogni trimestre si cerca attivamente che cosa togliere.
202. Mai rilasciare ciò che non rispetta la Definition of Done (prestazioni, accessibilità, offline, sicurezza): la data si sposta, la qualità no.
203. In ogni revisione di prodotto qualcuno ha il ruolo formale di avvocato della semplicità.
204. I budget (tocchi, avvio, memoria, notifiche, superficie) sono legge interna: superarli richiede emendamento, non entusiasmo.
205. Il debito tecnico si dichiara, si registra e si ripaga: mai debito contratto in silenzio.
206. Chiunque nel team può fermare una release per un rischio di perdita dati o di violazione della Constitution.
207. Le post-mortem cercano cause di sistema, mai colpevoli.
208. Il ritmo è sostenibile per progetto: l'emergenza cronica è un fallimento di pianificazione da correggere, non uno stile di lavoro.
209. Ogni nuovo membro del team legge questa Bible prima del primo commit, e il suo primo compito è farne le pulci.
210. La cultura si giudica da come trattiamo l'utente più fragile, il collega più junior e lo sviluppatore di plugin più piccolo.

## Titolo X — Emendamenti e supremazia (art. 211–215)

211. Questa Constitution prevale su ogni altro documento, obiettivo, contratto interno o consuetudine.
212. Un emendamento richiede: proposta scritta con motivazione, discussione aperta a tutto il team, periodo di riflessione di almeno 14 giorni, approvazione del CEO e del custode del prodotto, registrazione nel Decision Log con le ragioni e i voti contrari.
213. Gli articoli 1–3, 10, 27, 28, 51, 53, 136, 156–158 sono **super-protetti**: il loro emendamento richiede inoltre l'annuncio pubblico agli utenti prima dell'entrata in vigore. (Se non abbiamo il coraggio di dirlo agli utenti, non abbiamo il diritto di farlo.)
214. Nessun emendamento può avere effetto retroattivo peggiorativo su dati, prezzi o diritti già acquisiti dagli utenti.
215. In assenza di regola applicabile, si decide chiedendosi: *"Che cosa vorrebbe l'utente se sapesse tutto quello che sappiamo noi?"* — e si registra la risposta come proposta di nuovo articolo.

---

**Ratifica.** La Constitution entra in vigore con la versione 1.0 della Product Bible. Ogni membro presente e futuro del team la riceve, la legge e la può invocare dal primo giorno.

*La Bible si chiude qui. L'esecuzione comincia adesso.*
