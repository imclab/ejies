/*
	ej.nthru.js by Emmanuel Jourdan, Ircam - 05 2004
	Ce truc ne fait rien	 :-)
 
	$Revision: 1.3 $
	$Date: 2005/09/26 15:15:57 $
*/

// global code

var ejies = EjiesUtils();	// lien vers ejies-extension.js

var NbInlets = 1;
var NbOutlets = 1;
const MAXINOUT = 64;

if (jsarguments.length>1 && typeof jsarguments[1] == "number")
	NbInlets = Math.min(jsarguments[1],MAXINOUT);		// inlet (minimum 1, max 64)
if (jsarguments.length>2 && typeof jsarguments[2] == "number")
	NbOutlets = Math.min(jsarguments[2],MAXINOUT);	// outlet (minimum 1, max 64)
if (jsarguments.length>3)
	perror("extra arguments...");

inlets = NbInlets;
outlets = NbOutlets;

var v = new Array();
var sortie = new Array();
var MaxInOut = 0;
var MultiOut = 0;

// inlet assistance... c'est pas plus lisible...
for ( i=0 ; i < NbInlets ; i++) {
	setinletassist(i,"inlet " + i);
}
for ( i=0 ; i < NbOutlets ; i++) {
	setoutletassist(i,"outlet " + i);
}

// en fonction des arguments
mapping();

function mapping()
{
	MaxInOut = Math.max(NbInlets,NbOutlets);
	// routing different en fonction du rapport inlet/outlet
	if (NbInlets == NbOutlets) {
		// routing "normal" si NbInlets == NbOutlets
		for (i=0 ; i < MaxInOut ; i++) {
			sortie[i] = i;
			MultiOut = 0;
		}
	}
	else if (NbInlets < NbOutlets) {
		// nombre d'inlet plus petit que le nombre d'outlet
		for (i=0, j=0 ; i < MaxInOut ; i++) {
			sortie[i] = Math.min(j++,NbInlets - 1);
			MultiOut = 1; // choix des sorties diff�rents
		}
	}
	else if (NbInlets > NbOutlets) {
		// nombre d'inlets plus grand que le nombre d'oullet
		for (i=0, j=0 ; i < MaxInOut ; i++) {
			sortie[i] = Math.min(j++,NbOutlets - 1);
			MultiOut = 0;
		}
	}
}

// Prend tout dans la t�te et l'envoie � la sortie correspondante
// en fonction du mapping() et du rapport inlet/outlet
function anything()
{
	var a = arrayfromargs(messagename,arguments);
	v[inlet] = a ;
	// dupplication des outlets ?
	if (MultiOut == 1) {
		if (inlet < (NbInlets - 1)) {
			outlet(sortie[inlet], a);
		}
		else {
			// inversion des sorties (d'abord les sorties de droite, right to left order... Tutorial n�7:-))
			for (i = NbOutlets - 1 ; i >= inlet ; i--) { 
				outlet(i, a);
			}
		}
	}
	else {
		outlet(sortie[inlet], a);
	}
}
anything.immediate = 1;

function perror()
{
	ejies.scriptname = "ej.nthru.js";
	ejies.perror(arguments);
}
perror.local = 1;

// Pour la compilation automatique
// autowatch = 1;
// post("Compiled...\n");