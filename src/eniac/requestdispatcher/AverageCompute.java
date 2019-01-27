package eniac.requestdispatcher;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Ce composant permet d'obtenir la moyenne des temps d'éxécutions des requêtes par 
 * les AVMs rattachés au répartiteur de requête dont il est lié.
 * Afin d'avoir une moyenne pertinente nous calculons dans notre cas avec le filtre exponentiel.
 * 
 * @author lc-laptop
 *
 */

public class AverageCompute {
	/**
	 * permet de garder en mémoire les débuts de tâches
	 */
	private Map<String, Date> startTime;
	/**
	 * moyenne calculé à l'instant t
	 */
	private int average;
	/**
	 * Il s'agit du paramètre qu'on utilise pour le calcul du filtre exponentiel et doit être comprit entre 0 et 1
	 * (0.1 dans notre cas).
	 */
	public static final double ALPHA = 0.1;
	
	public AverageCompute() {
		startTime = new HashMap<>();
		average = 0;
	}
	
	/**
	 * Ajout en mémoire le temps d'arrivée d'une requête
	 * @param rq uri de la requête
	 * @param d date de début
	 */
	public void addStartTime(String rq, Date d) {
		startTime.put(rq, d);
	}
	
	/**
	 * Ajoute et calcul le filtre exponentielle en prenant comme donnée la requête qui vient de se terminer
	 * @param rq uri de la requête
	 * @param d date de fin
	 */
	public void addEndTime(String rq, Date d) {
		int avgraw = (int)(d.getTime() - startTime.remove(rq).getTime());
		average = (int)(ALPHA*avgraw + (1.0-ALPHA)*average);
	}
	
	
	/**
	 * Renvoi la moyenne calculé par le filtre exponentiel
	 * @return la moyenne à l'instant t
	 */
	public int getAverage() {
		return average;
	}
}
