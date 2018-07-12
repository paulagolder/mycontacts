package org.lerot.mycontact.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lerot.mycontact.mcAttribute;
import org.lerot.mycontact.mcAttributes;
import org.lerot.mycontact.mcContact;
import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcDateDataType;
import org.lerot.mycontact.mcMappings;
import org.lerot.mycontact.mcUtilities;
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

public class exportBackupPanel extends jswVerticalPanel
		implements ActionListener
{

	private static final long serialVersionUID = 1L;
	JFileChooser fc;
	jswHorizontalPanel importbar;
	jswButton backupbutton;
	private jswVerticalPanel exportlog;
	File exportfile;
	String exporttype;
	jswDropDownBox exporttypebox;

	jswButton selbutton;
	jswTextField selectedfile;

	String extension = "csv";
	private String exportfilename;
	private jswLabel imptrace;
	private int crows, orows;
	private jswLabel countlabel;
	jswHorizontalPanel exportresult;
	private jswOptionset optionset;
	private mcContacts exportsource;
	private jswOption allcontacts;
	private jswOption browsecontacts;
	private jswOption selectedcontacts;
	private jswButton testbutton;
	private File fileToSave;

	public exportBackupPanel()
	{
		// this.setBackground(Color.green);
		jswHorizontalPanel header = new jswHorizontalPanel();
		jswLabel heading = new jswLabel(" Backing Up Contacts ");
		header.add(" FILLW ", heading);
		this.add(header);
		jswHorizontalPanel filterbar = new jswHorizontalPanel();
		optionset = new jswOptionset("source", false, this);
		allcontacts = optionset
				.addNewOption("All Contacts " + mcdb.selbox.countAll(), false);
		allcontacts.setTag("all");
		browsecontacts = optionset.addNewOption(
				"Browse Contacts " + mcdb.selbox.getBrowsecontactlist().size(),
				false);
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
		String bufilename = mcdb.topgui.budir + "/contacts_bu_"
				+ mcDateDataType.getNow("yyyyMMdd") + ".xml";
		// File file = new File(bufilename);

		selectedfile = new jswTextField(bufilename);
		selectedfile.setText(bufilename);
		selectedfile.setEnabled(true);
		System.out.println(bufilename);
		filebar.add(" LEFT WIDTH=200 ", selectedfile);
		exporttypebox = new jswDropDownBox("", false, false);
		Vector<String> varry = new Vector<String>();
		varry.add("XML");
		exporttypebox.addList(varry);
		filebar.add(" LEFT WIDTH=200 ", exporttypebox);

		this.add(filebar);
		importbar = new jswHorizontalPanel();
		backupbutton = new jswButton(this, "BackUp");
		importbar.add(" MIDDLE  ", backupbutton);
		backupbutton.setVisible(true);
		this.add(importbar);

		exportresult = new jswHorizontalPanel();
		countlabel = new jswLabel("freddy");
		exportresult.add(" middle ", countlabel);
		countlabel.setVisible(false);
		exportresult.setVisible(true);
		this.add(exportresult);

		exportlog = new jswVerticalPanel();
		imptrace = new jswLabel("");
		exportlog.add(imptrace);
		imptrace.setVisible(true);
		imptrace.setText(" Starting Backup ");
		exportlog.setVisible(false);
		this.add(" FILLH ", exportlog);
		importbar.setVisible(true);
		this.setVisible(true);
		refresh();
		this.refresh();

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		// need command forl sellect file and directory paul fix
		if (command == "Select")
		{
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Specify a file to save");
			String bufilename = selectedfile.getText();
			File file = new File(bufilename);
			fc.setCurrentDirectory(file.getParentFile());

			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Backup", "xml", "XML");
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
				if (extension.equalsIgnoreCase("xml"))
					exporttypebox.setSelected("XML");
				exporttypebox.repaint();
				selectedfile.setText(fileToSave.getPath());
				exportfile = fileToSave;
			} else
			{
				System.out.println("Open command cancelled by user.");
			}
		} else if (command == "BackUp")
		{

			Vector<String> attkeys = new Vector<String>();
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
				Vector<mcContact> sortedcontacts = exportsource
						.makeOrderedContactsVector();
				exportfilename = selectedfile.getText();
				new File(exportfilename);
				imptrace.setVisible(true);
				int outcount = backupXML(sortedcontacts, exportfilename);
				countlabel.setText(outcount + " records exported ");
				this.repaint();
			} catch (Exception e2)
			{

			}
			countlabel.setVisible(true);
			exportresult.setVisible(true);
			exportresult.repaint();
			countlabel.setVisible(true);
		} else
		{
			System.out.println(" not found command " + command);
		}
	}

	public int backupXML(Vector<mcContact> outlist, String exportfilename)
	{
		HashMap<String, Integer> taglist = new HashMap<String, Integer>();
		mcAttribute anattrinbute = new mcAttribute(0);
		Vector<String> attkeylist = anattrinbute.dbloadAttributeKeyList();
		PrintWriter printWriter;
		int k = 0;

		try
		{
			printWriter = new PrintWriter(exportfilename);
			printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			printWriter.println(
					"<?xml-stylesheet type='text/xsl' href='./Stylesheets/ContactsStyler.xsl' ?>");
			printWriter.println("<contacts>" + "\n");
			for (mcContact acontact : outlist)
			{
				k++;
				acontact.getTID();
				String nameatt = acontact.toXML(attkeylist);
				if (nameatt != null) printWriter.println(nameatt);

				addTags(taglist, acontact);
			}

			HashMap<String, Integer> sortedtags = mcUtilities
					.sortMapByValues(taglist);
			printWriter.println("<taglist>");
			for (Entry<String, Integer> anentry : sortedtags.entrySet())
			{
				String atag = anentry.getKey();
				int count = anentry.getValue();
				printWriter.println(
						"<tag key='" + atag + "' count='" + count + "'/>");
			}
			printWriter.println("</taglist>");

			// add attribute type, mappings out ? paul fix
			printWriter.println("</contacts>");
			printWriter.close();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return k;
	}

	private void addTags(Map<String, Integer> taglist, mcContact acontact)
	{
		Set<String> tags = acontact.getTags();
		if (tags != null)
		{
			for (String atag : tags)
			{
				if (taglist.containsKey(atag))
				{
					int count = taglist.get(atag);
					taglist.put(atag, count + 1);
				} else
					taglist.put(atag, 1);
			}
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
