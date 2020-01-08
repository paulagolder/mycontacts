package org.lerot.mycontact.gui;

import org.lerot.gui.widgets.jswTabbedPanel;
import org.lerot.gui.widgets.jswVerticalLayout;

public class importTabPanel extends jswTabbedPanel
{

	private static final long serialVersionUID = 1L;
	private importPanel importactionpanel;

	public importTabPanel()
	{
		setLayout(new jswVerticalLayout());
		importactionpanel = new importPanel();
		// panel1.showImportPanel();
		addTab("Import", importactionpanel);
		importactionpanel.setVisible(true);
		importSetupPanel panel2 = new importSetupPanel();
		addTab("Import Setup", panel2);
		ImportEditPanel panel3 = new ImportEditPanel();
		panel3.setVisible(true);
		addTab("Edit Imports", panel3);
		setSelectedComponent(panel2);
	}

	public void showImportPanel()
	{
		importactionpanel.showImportPanel();
	}

}
