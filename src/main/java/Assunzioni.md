# Assunzioni

* Le versioni sono ordinate per data di rilascio
* Consideriamo un commit afferente ad una versione solo se "dataRilascioVersionePrecedente < dataCommit <= dataRilascioVersione"
* Versioni che non hanno commit vengono scartate: significa che rispetto alla versione precedente non sono state apportate modifiche, quindi queste versioni si comportano come la loro versione predecessore.
* In alcuni casi ci sono versioni rilasciate nella stessa data: in questi caso i commit che ricadono in una sola delle versioni ed una delle due viene scartata.
* Per un ticket consideriamo:
  * OpeningVersion la prima versione dopo la data di creazione del ticket
  * InjectedVersion la prima delle AffectedVersions riportate su Jira, se presenti
  * FixVersion la prima delle versioni dopo la data di risoluzione del ticket
* Un ticket viene scartato se:
  * Viene creato prima della prima Versione. Questi ticket li possiamo considerare come dei Bug trovati e subito risolti o che comunque non hanno un impatto significativo sullo studio
  * OpeningVersion o FixVersion null. Questi ticket sono stati risolti ma non è stata ancora rilasciata una versione successiva e quindi non possiamo avere le informazioni su quest'ultima
  * OpeningVersion > FixVersion. Questi ticket sono scartati perché significa che le informazioni di Jira per quel ticket sono inconsistenti. Non sapendo quali informazioni sono errate in Jira il ticket viene scartato
  * InjectedVersion >= OpeningVersion (Se presente Injected). Questi ticket sono scartati perché significa che le informazioni di Jira per quel ticket sono inconsistenti. Non sapendo quali informazioni sono errate in Jira il ticket viene scartato. Inoltre staimo considerando solo ticket che sono trovati in una versione e risolti nella versione successiva
* Qualora i ticket non abbiano injectedVersion, questa viene impostata con Proportion. Distinguiamo due ticket in termini di proportion:
  * Validi. Sono ticket che hanno injectedVersion e che possono essere usati per calcolare la P per gli altri ticket. Se un ticket ha Injected == Opening consideriamo il denominatore pari ad 1 per non avere denominatore nullo
  * Non Validi. Sono ticket per cui impostiamo l'injectedVersion con Proportion.
* Calcoliamo la P di proportion in questo modo. Prendiamo un Ticket che non ha InjectedVersion; consideriamo la lista dei Ticket precedenti per data che sono Validi. La P è calcolata come:
  * Cold Start se il numero di Ticket Validi precedenti è minore di una soglia di Threshold. ColdStart viene fatto prendendo la mediana del P medio degli aptri progetti
  * Media dei P dei ticket validi altrimenti. La base di questo è che supponiamo che il comportamento nella risoluzione di un Ticket sia mediamente simile al comportamento avuto nella risoluzione dei ticket precedenti
* Consideriamo le classi di una Versione come quelle dell'ultimo commit della versione: sono queste infatti che ci dicono lo stato del progetto a quella specifica versione
* Le metriche sono calcolate per singola versione e non cumulative tra versioni
* Se una classe ha zero commit in una versione significa che non viene modificata in quella versione e quindi l'unica misura non nulla sarà la size della classe
* Un commit è considerato risolutivo per un Ticket se valgono le seguenti condizioni:
  * Contiene l'identificativo del Ticket nel suo messaggio
  * Il Commit ha date che ricadono tra quelle della prima e dell'ultima versione: infatti consideriamo solo ticket che sono stati creati dopo la prima versione e quindi un commit che lo risolve può stare solo dopo, così come può stare solo prima dell'ultima versione //TODO
  * Il Commit deve avere data successiva alla creazione del ticket e data precedente alla risoluzione del Ticket: stiamo quindi assumendo che valgano le date presenti in Jira per quel Ticket e quindi la data del commit deve essere consistente
* Ci sono Ticket che non hanno dei Commit risolutivi: supponendo che un Ticket debba avere almeno un commit risolutivo, scartiamo questi ticket
* Se una classe modificata da un commit non era presente in una affected version non le consideriamo affected
* Ci possono essere versioni rilasciate nella stessa data: in questo caso i commit associati a queste versioni vengono associate tutte quante ad una versione mentre un'altra rimane senza commit
* Se un file viene cancellato tra due versioni, possiamo non considerare le righe cancellate: le classi che stiamo considerando infatti sono quelle dell'ultimo commit delle versione, quindi se una classe viene cancellata da un commit intermedio alla release non ci sarà tra le classi in elenco