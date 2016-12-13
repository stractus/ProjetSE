package jus.poc.prodcons.v3;

import jus.poc.prodcons.Message;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class ProdCons implements Tampon {

	private int nbBuffer;
	private int caseDepot;
	private int caseConso;
	public MessageX[] buffer;
	public monSemaphore plein, vide;
	public monSemaphore mutexIn, mutexOut;
	
	public ProdCons(int taille) {
		// TODO Auto-generated constructor stub		
		nbBuffer = taille;
		buffer = new MessageX[taille];
		caseDepot = 0;
		caseConso = 0;
		plein = new monSemaphore(0);
		vide = new monSemaphore(taille);
		mutexIn = new monSemaphore(1);
		mutexOut = new monSemaphore(1);
	}

	@Override
	public int enAttente() {
		// TODO Auto-generated method stub
		int reste = (caseDepot - caseConso)%nbBuffer;
		if(reste == 0 || buffer[caseDepot] != null) reste = nbBuffer;
		return reste;
	}
	
	@Override
	public Message get(_Consommateur arg0) throws Exception, InterruptedException {
		// TODO Auto-generated method stub
		Message sortie = buffer[caseConso];
		System.out.println(Thread.currentThread().getName()+" recupere "+sortie);
		buffer[caseConso] = null;
		caseConso = (++caseConso)%nbBuffer;
		return sortie;
	}

	@Override
	public void put(_Producteur arg0, Message arg1) throws Exception, InterruptedException {
		// TODO Auto-generated method stub
		buffer[caseDepot] = (MessageX) arg1;
		System.out.println(Thread.currentThread().getName()+" depose "+arg1);
		caseDepot = (++caseDepot)%nbBuffer;
	}

	@Override
	public int taille() {
		// TODO Auto-generated method stub
		return nbBuffer;
	}

}
