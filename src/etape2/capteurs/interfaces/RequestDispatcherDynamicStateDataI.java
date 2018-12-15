package etape2.capteurs.interfaces;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.interfaces.TimeStampingI;

public interface RequestDispatcherDynamicStateDataI extends		DataOfferedI.DataI,
DataRequiredI.DataI,
TimeStampingI{
	
	public double getAverage();

}
