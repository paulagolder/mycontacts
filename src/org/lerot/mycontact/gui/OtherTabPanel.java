package org.lerot.mycontact.gui;

import org.lerot.mycontact.gui.widgets.jswTabbedPanel;
import org.lerot.mycontact.gui.widgets.jswVerticalLayout;

public class OtherTabPanel extends jswTabbedPanel
{

	private static final long serialVersionUID = 1L;
	private dbEditPanel dbpanel;
	private manageTagsPanel manageTagspanel2;
	private mergeContactPanel mergeContactPanel3;
	private deleteContactPanel deleteContactPanel3;

	public OtherTabPanel()
	{

		setLayout(new jswVerticalLayout());
		//attedpanel1 = new attributeEditPanel();
		//addTab("Edit Attributes", attedpanel1);
		dbpanel = new dbEditPanel();
		addTab("Edit Databases", dbpanel);
		manageTagspanel2 = new manageTagsPanel();
		addTab("Manage Tags", manageTagspanel2);
		mergeContactPanel3 = new mergeContactPanel();
		addTab("Merge Contacts", mergeContactPanel3);
		deleteContactPanel3 = new deleteContactPanel();
		addTab("Delete Contacts", deleteContactPanel3);
		
		// tabbedPane.addChangeListener(this);

	}

	public void showOtherPanel()
	{
		//attedpanel1.refresh();
		manageTagspanel2.refresh();
		mergeContactPanel3.refresh();
		deleteContactPanel3.initialise();

	}

}
