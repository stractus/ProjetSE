package jus.poc.prodcons.v3;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons.v3.MessageX;

public class Consommateur extends Acteur implements _Consommateur {

	private int nbMessagesTraites;
	private ProdCons tampon;
	private List<MessageX> messagesLus;
	private int tempsDeTraitement;
	private boolean etat = false;
	private Semaphore plein;
	private Semaphore vide;
	public Semaphore mutex;
	
	public Consommateur(Observateur observateur, int moyenneTempsDeTraitement, int deviationTempsDeTraitement, ProdCons tp)
			throws ControlException {
		super(typeConsommateur, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);
		// TODO Auto-generated constructor stub
		tampon = tp;
		nbMessagesTraites = 0;
		messagesLus = new LinkedList<MessageX>();
		tempsDeTraitement = moyenneTempsDeTraitement;
		vide = tp.vide;
		plein = tp.plein;
		mutex = tp.mutexConso;
	}
	
	public List<MessageX> getConsommes(){return messagesLus;}
	
	public void arret(){etat = false;}
	
	@Override
	public void run(){
		etat = true;
		while(etat){
			try {
				plein.acquire();
				mutex.acquire();
				messagesLus.add((MessageX)tampon.get(this));
				observateur.retraitMessage(this, messagesLus.get(messagesLus.size()-1));
				mutex.release();
				vide.release();
				observateur.consommationMessage(this, messagesLus.get(messagesLus.size()-1), tempsDeTraitement);
				nbMessagesTraites++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	//nombre de messages que le consommateur a deja traite
	public int nombreDeMessages() {
		// TODO Auto-generated method stub
		return nbMessagesTraites;
	}

}
