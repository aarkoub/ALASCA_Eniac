package eniac.requestgenarator.interfaces;

//Copyright Jacques Malenfant, Sorbonne Universite.
//
//Jacques.Malenfant@lip6.fr
//
//This software is a computer program whose purpose is to provide a
//basic component programming model to program with components
//distributed applications in the Java programming language.
//
//This software is governed by the CeCILL-C license under French law and
//abiding by the rules of distribution of free software.  You can use,
//modify and/ or redistribute the software under the terms of the
//CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
//URL "http://www.cecill.info".
//
//As a counterpart to the access to the source code and  rights to copy,
//modify and redistribute granted by the license, users are provided only
//with a limited warranty  and the software's author,  the holder of the
//economic rights,  and the successive licensors  have only  limited
//liability. 
//
//In this respect, the user's attention is drawn to the risks associated
//with loading,  using,  modifying and/or developing or reproducing the
//software by the user in light of its specific status of free software,
//that may mean  that it is complicated to manipulate,  and  that  also
//therefore means  that it is reserved for developers  and  experienced
//professionals having in-depth computer knowledge. Users are therefore
//encouraged to load and test the software's suitability as regards their
//requirements in conditions enabling the security of their systems and/or 
//data to be ensured and,  more generally, to use and operate it in the 
//same conditions as regards security. 
//
//The fact that you are presently reading this means that you have had
//knowledge of the CeCILL-C license and that you accept its terms.

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Cette classe à été reprise de BCM, nous avons dû légèrement faire quelques ajouts pour nos besoins.
 */
public interface			RequestGeneratorManagementI
extends		OfferedI,
			RequiredI
{
	/**
	 * start the generation of the requests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>todo.</i>
	 */
	public void			startGeneration() throws Exception ;

	/**
	 * stop the generation of the requests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>todo.</i>
	 */
	public void			stopGeneration() throws Exception ;

	/**
	 * get the current value of the mean inter-arrival time used to generate
	 * requests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the current value of the mean inter-arrival time.
	 * @throws Exception		<i>todo.</i>
	 */
	public double		getMeanInterArrivalTime() throws Exception ;

	/**
	 * set the value of the mean inter-arrival time used to generate requests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	miat &gt; 0.0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param miat			new value for the mean inter-arrival time.
	 * @throws Exception		<i>todo.</i>
	 */
	public void			setMeanInterArrivalTime(double miat) throws Exception ;

	
	/**
	 * Cette méthode correspond à la demande de l'application au centre de calcul, d'héberger son application.
	 * En effet, le RequestGenerator envoit une demande d'hébergement au Controlleur d'admission via un port configuré dans le constructeur et y 
	 * envoit en même temps son port de notification lorsqu'il reçoit la réponse, le RequestGenerator connecte son port de soumission avec l'uri fournit
	 * par la réponse du Controlleur d'admission, ici nous faisont la connection avec un connecteur générer par Javassist, nous avons intentionnellement changé la 
	 * méthode soumission de requête afin d'utiliser Javassist.
	 * @return true si le controlleur d'admission accepte/ false sinon
	 * @throws Exception exception
	 */
	public boolean askAdmissionControler() throws Exception;

	/**
	 * Cette méthode demande au controlleur d'admission de libérer les ressources qui ont été alloué pour ce RequestGenerator, car
	 * celui-ci a terminé ses demandes de tâches.
	 * @throws Exception exception
	 */
	public void freeAdmissionControlerRessources() throws Exception;
}
