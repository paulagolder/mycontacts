package org.lerot.mycontact.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcMappings;
import org.lerot.mycontact.mcdb;
import org.lerot.mycontact.gui.widgets.jswButton;
import org.lerot.mycontact.gui.widgets.jswCheckbox;
import org.lerot.mycontact.gui.widgets.jswDropDownBox;
import org.lerot.mycontact.gui.widgets.jswHorizontalPanel;
import org.lerot.mycontact.gui.widgets.jswLabel;
import org.lerot.mycontact.gui.widgets.jswOption;
import org.lerot.mycontact.gui.widgets.jswOptionset;
import org.lerot.mycontact.gui.widgets.jswPanel;
import org.lerot.mycontact.gui.widgets.jswTextField;
import org.lerot.mycontact.gui.widgets.jswVerticalPanel;

public class exportPanel extends jswVerticalPanel implements ActionListener
{

	private static final long serialVersionUID = 1L;
	JFileChooser fc;
	jswHorizontalPanel importbar;
	jswButton importbutton;
	private jswVerticalPanel exportlog;
	File exportfile;
	String exporttype;
	jswDropDownBox exporttypebox;

	jswButton selbutton;
	jswTextField selectedfile;

	String extension = "csv";
	private String exportfilename;
	private jswVerticalPanel selectors;
	private jswVerticalPanel options;
	private jswLabel imptrace;
	private jswCheckbox[] checkbox;
	private jswCheckbox[] option;
	private int crows,orows;
	private jswLabel countlabel;
	jswHorizontalPanel exportresult;
	private jswOptionset optionset;
	private mcContacts exportsource;
	private jswOption allcontacts;
	private jswOption browsecontacts;
	private jswOption selectedcontacts;
	private jswButton testbutton;

	public exportPanel()
	{
		// this.setBackground(Color.green);
		jswHorizontalPanel header = new jswHorizontalPanel();
		jswLabel heading = new jswLabel(" Exporting Data ");
		header.add(" FILLW ", heading);
		this.add(header);
		jswHorizontalPanel filterbar = new jswHorizontalPanel();
		optionset = new jswOptionset("source", false, this);
		allcontacts = optionset.addNewOption(
				"All Contacts " + mcdb.selbox.countAll(), false);
		allcontacts.setTag("all");
		browsecontacts = optionset.addNewOption("Browse Contacts "
				+ mcdb.selbox.getBrowsecontactlist().size(), false);
		browsecontacts.setTag("browse");
		selectedcontacts = optionset.addNewOption("Selected Contacts "
				+ mcdb.selbox.getSelectedcontactlist().size(), false);
		selectedcontacts.setTag("selected");
		filterbar.add(" LEFT ", allcontacts);
		filterbar.add(" MIDDLE ", browsecontacts);
		browsecontacts.setSelected(true);
		filterbar.add(" RIGHT ", selectedcontacts);
		this.add(filterbar);
		jswHorizontalPanel filebar = new jswHorizontalPanel();
		selbutton = new jswButton(this, "Select");
		filebar.add(" LEFT ", selbutton);
		selectedfile = new jswTextField();
		selectedfile.setText("Select Export File >");
		selectedfile.setEnabled(true);
		filebar.add(" LEFT WIDTH=200 ", selectedfile);
		exporttypebox = new jswDropDownBox("", false, false);
		Vector<String> varry = new Vector<String>();
		varry.add("CSV");
		varry.add("gOutlookExport");
		varry.add("gGoogleExport");
		varry.add("Vcard");
		varry.add("Ldif");
		varry.add("Ical");
		varry.add("XML");
		exporttypebox.addList(varry);
		filebar.add(" LEFT WIDTH=200 ", exporttypebox);
		checkbox = new jswCheckbox[20];
		for (int i = 0; i < 20; i++)
		{
			checkbox[i] = new jswCheckbox("*");
		}
		option = new jswCheckbox[20];
		for (int i = 0; i < 20; i++)
		{
			option[i] = new jswCheckbox("*");
		}
		jswButton savebutton = new jswButton(this, "Save");
		filebar.add(" RIGHT ", savebutton);
		this.add(filebar);
		importbar = new jswHorizontalPanel();
		importbutton = new jswButton(this, "Export File");
		importbar.add(" MIDDLE  ", importbutton);
		importbutton.setVisible(false);
		importbutton.setVisible(false);
		testbutton = new jswButton(this, "Davtest");
		importbar.add(" RIGHT  ", testbutton);
		testbutton.setVisible(true);
		this.add(importbar);
	
		exportresult = new jswHorizontalPanel();
		countlabel = new jswLabel("freddy");
		exportresult.add(" middle ", countlabel);
		countlabel.setVisible(false);
		exportresult.setVisible(true);
		this.add(exportresult);
		jswHorizontalPanel optionpanel = new jswHorizontalPanel();
		selectors = new jswVerticalPanel();
		selectors.setVisible(false);
		optionpanel.add(" FILLH ", selectors);
		options = new jswVerticalPanel();
		options.setVisible(false);
		optionpanel.add(" MIDDLE ", options);
	
		this.add(" FILLH ",optionpanel);
		
		exportlog = new jswVerticalPanel();
		imptrace = new jswLabel("");
		exportlog.add(imptrace);
		imptrace.setVisible(true);
		imptrace.setText(" Starting Export");
		exportlog.setVisible(false);
		this.add(" FILLH ", exportlog);
		importbar.setVisible(true);

		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if (command == "Select")
		{
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Specify a file to save");
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Contacts", "csv", "vcf", "ldif", "kdif", "ics", "xml");
			fc.setFileFilter(filter);
			int returnVal = fc.showSaveDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File fileToSave = fc.getSelectedFile();
				extension = "";
				int i = fileToSave.getName().lastIndexOf('.');
				if (i > 0)
				{
					extension = fileToSave.getName().substring(i + 1);
				}
				if (extension.equalsIgnoreCase("csv"))
					exporttypebox.setSelected("CSV");
				else if (extension.equalsIgnoreCase("vcf"))
					exporttypebox.setSelected("Vcard");
				else if (extension.equalsIgnoreCase("ics"))
					exporttypebox.setSelected("Ical");
				else if (extension.equalsIgnoreCase("ldif")
						|| extension.equalsIgnoreCase("kdif"))
					exporttypebox.setSelected("Ldif");
				else if (extension.equalsIgnoreCase("xml"))
					exporttypebox.setSelected("XML");
				exporttypebox.repaint();
				selectedfile.setText(fileToSave.getName());
				exportfile = fileToSave;
			} else
			{
				System.out.println("Open command cancelled by user.");
			}
		} else if (command == "Save")
		{
			exportfilename = exportfile.getPath();
			exporttype = exporttypebox.getSelectedValue();
			
			System.out.println(" et=" + exporttype); 
			
			new File(exportfilename);
			importbutton.setVisible(true);
			countlabel.setVisible(false);
			selectors.removeAll();
			selectors.setVisible(true);
			options.removeAll();
			options.setVisible(true);
			mcMappings mappings = mcdb.topgui.currentcon
					.createMappings("export", exporttype);
			if (exporttype.equalsIgnoreCase("ical"))
			{
				checkbox[0] = new jswCheckbox("Birthday");
				selectors.add(" INDENT=20 ", checkbox[0]);
				checkbox[1] = new jswCheckbox("Anniversary");
				selectors.add(" INDENT=20 ", checkbox[1]);
				crows = 2;
			} else if (exporttype.equalsIgnoreCase("csv"))
			{
				System.out.println(" et=" + exporttype);
				crows = 0;
				for (Entry<String, String> entry : mappings.entrySet())
				{
					String localstring = entry.getKey();
					checkbox[crows] = new jswCheckbox(localstring);
					selectors.add(" INDENT=20 ", checkbox[crows]);
					checkbox[crows].setVisible(true);
					checkbox[crows].setSelected(true);
					crows++;
				}
			} else if (exporttype.equalsIgnoreCase("vcard"))
			{
				System.out.println(" et=" + exporttype);
				crows = 0;
				for (Entry<String, String> entry : mappings.entrySet())
				{
					String localstring = entry.getKey();
					checkbox[crows] = new jswCheckbox(localstring);
					selectors.add(" INDENT=20 ", checkbox[crows]);
					checkbox[crows].setVisible(true);
					checkbox[crows].setSelected(true);
					crows++;
				}
				
				option[0] = new jswCheckbox("owncloud");		
				options.add(" INDENT=20 ", option[0]);
				option[0].setVisible(true);
				option[0].setSelected(false);
				orows = 1;
			}
			else
			{
				crows = 0;
				for (Entry<String, String> entry : mappings.entrySet())
				{
					String localstring = entry.getKey();
					checkbox[crows] = new jswCheckbox(localstring);
					selectors.add(" INDENT=20 ", checkbox[crows]);
					checkbox[crows].setVisible(true);
					checkbox[crows].setSelected(true);
					crows++;
				}
				if (exporttype.equalsIgnoreCase("xml"))
				{
					checkbox[crows] = new jswCheckbox("Tag List");
					selectors.add(" INDENT=20 ", checkbox[crows]);
					checkbox[crows].setVisible(true);
					checkbox[crows].setSelected(true);
					crows++;
				}
			}
			selectors.setVisible(true);
			selectors.repaint();

		} else if (command == "Export File")
		{

			Vector<String> attkeys = new Vector<String>();
			for (int i = 0; i < crows; i++)
			{
				if (checkbox[i].isSelected())
				{
					String localstring = checkbox[i].getLabel();
					attkeys.add(localstring.toLowerCase());
				}
			}
			Vector<String> optlist= new Vector<String>();
			for (int i = 0; i < orows; i++)
			{
				if (option[i].isSelected())
				{
					String localstring = option[i].getLabel();
					// System.out.println(" mappings selected =" + localstring);
					optlist.add(localstring.toLowerCase());
				}
			}
			selectors.setVisible(true);
			selectors.repaint();
			String selection = optionset.getSelectedTag();
			System.out.println(selection);
			if (selection.equals("all"))
				exportsource = mcdb.selbox.getAllcontactlist();
			else if (selection.equals("selected"))
			{
				exportsource = mcdb.selbox.getSelectedcontactlist();
			} else
				exportsource = mcdb.selbox.getBrowsecontactlist();
			try
			{
				new File(exportfilename);
				imptrace.setVisible(true);
				int outcount = exportsource.savecontacts(exportfilename,
						exporttype, attkeys, optlist);
				countlabel.setText(outcount + " records exported ");
				this.repaint();
			} catch (Exception e2)
			{

			}
			countlabel.setVisible(true);
			exportresult.setVisible(true);
			exportresult.repaint();
			countlabel.setVisible(true);
		} else if (command == "Davtest")
		{
			// webdav dav = new webdav();
			// dav.getfile();
		}

	}

	public void refresh()
	{
		int ncontacts = mcdb.selbox.countAll();
		int nsearchcontacts = mcdb.selbox.getSelectedcontactlist().size();
		int nbrowsecontacts = mcdb.selbox.getBrowsecontactlist().size();
		allcontacts.setText("All Contacts (" + ncontacts + ")");
		browsecontacts.setText("Browse Contacts (" + nbrowsecontacts + ")");
		selectedcontacts.setText("Selected Contacts (" + nsearchcontacts + ")");
	}
}
