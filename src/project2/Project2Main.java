package project2;

import aima.core.search.csp.MapCSP;
import aima.gui.applications.search.csp.CSPView;
import aima.gui.applications.search.csp.MapColoringApp;

/**
 * Created by jim on 10/10/15.
 */
public class Project2Main
{
	public static void main(String args[])
	{
		MapCSP theMap = new MapCSP();


		new MapColoringApp().startApplication();
	}
}
