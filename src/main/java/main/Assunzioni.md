# Assunzioni

* Supponiamo che il ticket di riferimento di ogni messaggio di Commit sia dato dal primo bookkeeper
* Se una classe modificata da un commit non era presente in una affected version non le consideriamo affected
* Ci possono essere versioni rilasciate nella stessa data: in questo caso i commit associati a queste versioni vengono associate tutte quante ad una versione mentre un'altra rimane senza commit
* Ci sono versioni che non hanno commit associati: in questo caso lo stato delle classi di una versione è lo stesso di quelle delle versioni precedenti. Possiamo fare due cose a tal proposito:
    * Escludere queste versioni. Notiamo che queste versioni molto spesso sono molto vicine temporalmente ad altre versioni, quindi non ci sono cambiamenti significativi
* Se una classe ha zero commit in una versione significa semplicemte che non viene modificata in quella versione e quindi l'unica misura non nulla sarà la size della classe