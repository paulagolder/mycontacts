package org.lerot.mycontact;

import java.util.Comparator;
import java.util.TreeSet;

public class mcContactSet extends TreeSet<mcContact>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	class ContactComp implements Comparator<mcContact>
	{
		@Override
		public int compare(mcContact e1, mcContact e2)
		{
			return e1.getTID().compareTo(e2.getTID());
		}
	}

	public mcContactSet()
	{

		// this.comparator(new ContactComp());
	}

}
