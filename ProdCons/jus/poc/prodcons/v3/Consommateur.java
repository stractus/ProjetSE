package jus.poc.prodcons.v3;

import java.util.LinkedList;
import java.util.List;

import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons._Consommateur;

public class Consommateur extends Acteur implements _Consommateur {

	private int nbMessagesTraites;
	private ProdCons tampon;
	private List<MessageX> messagesLus;
	private boolean etat = false;
	private Aleatoire alea;
	
	public Consommateur(Observateur observateur, int moyenneTempsDeTraitement, int deviationTempsDeTraitement, ProdCons tp)
			throws ControlException {
		super(typeConsommateur, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);
		// TODO Auto-generated constructor stub
		tampon = tp;
		nbMessagesTraites = 0;
		messagesLus = new LinkedList<MessageX>();
		alea = new Aleatoire(moyenneTempsDeTraitement, deviationTempsDeTraitement);
	}
	
	public List<MessageX> getConsommes(){return messagesLus;}
	
	public void arret(){etat = false;}
	
	@Override
	public void run(){
		etat = true;
		MessageX reception;
		while(etat){
			try {
				tampon.plein.P();
				tampon.mutexOut.P();
				reception = (MessageX)tampon.get(this);
				if(reception == null){
					arret();
				}
				else{
					observateur.retraitMessage(this, reception);
					sleep(alea.next()*50);
					messagesLus.add(reception);
					observateur.consommationMessage(this, reception, moyenneTempsDeTraitement);
					System.out.println(reception+" traite");
					nbMessagesTraites++;
				}
				tampon.mutexOut.V();
				tampon.vide.V();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(getName()+" fini son execution");
	}

	@Override
	//nombre de messages que le consommateur a deja traite
	public int nombreDeMessages() {
		// TODO Auto-generated method stub
		return nbMessagesTraites;
	}

}
