# Assunzioni
* Supponiamo che il ticket di riferimento di ogni messaggio di Commit sia dato dal primo bookkeeper
* Se una classe modificata da un commit non era presente in una affected version non le consideriamo affected
* Ci possono essere versioni rilasciate nella stessa data: in questo caso i commit associati a queste versioni vengono associate tutte quante ad una versione mentre un'altra rimane senza commit
* Ci sono versioni che non hanno commit associati: in questo caso lo stato delle classi di una versione è lo stesso di quelle delle versioni precedenti. Possiamo fare due cose a tal proposito:
  * Escludere queste versioni. Notiamo che queste versioni molto spesso sono molto vicine temporalmente ad altre versioni, quindi non ci sono cambiamenti significativi
* Se una classe ha zero commit in una versione significa semplicemte che non viene modificata in quella versione e quindi l'unica misura non nulla sarà la size della classe
* Supponiamo che non ossano esserci dei ticket con data di creazione precedente alla prima release: questi ticket possiamo considerarli come ticket interni i cui bug associati sono stati individuati e subito risolti
* Ci sono ticket che non hanno commit associati: noi stiamo considerando solo ticket che sono chiusi e sono stati risolti. Supponendo che ticket risolti abbiano almeno un commit associato, possiamo considerare questi ticket come sbagliati e quindi possiamo rimuoverli 7
* Se un file viene cancellato tra due versioni, possiamo non considerare le righe cancellate: le classi che stiamo considerando infatti sono quelle dell'ultimo commit delle versione, quindi se una classe viene cancellata da un commit intermedio alla release non ci sarà tra le classi in elenco
* Possiamo restringere l'analisi alle classi che sono dell'ultimo commit di una release: queste infatti ci danno lo stato del progetto prima del rilascio successivo