package org.lerot.mycontact.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lerot.mywidgets.jswButton;
import org.lerot.mywidgets.jswCheckbox;
import org.lerot.mywidgets.jswContainer;
import org.lerot.mywidgets.jswDropDownBox;
import org.lerot.mywidgets.jswHorizontalPanel;
import org.lerot.mywidgets.jswImage;
import org.lerot.mywidgets.jswLabel;
import org.lerot.mywidgets.jswPanel;
import org.lerot.mywidgets.jswStyle;
import org.lerot.mywidgets.jswStyles;
import org.lerot.mywidgets.jswTable;
import org.lerot.mywidgets.jswTextBox;
import org.lerot.mywidgets.jswTextField;
import org.lerot.mywidgets.jswVerticalPanel;
import org.lerot.mycontact.mcAddressDataType;
import org.lerot.mycontact.mcAttribute;
import org.lerot.mycontact.mcAttributeType;
import org.lerot.mycontact.mcAttributeTypes;
import org.lerot.mycontact.mcAttributes;
import org.lerot.mycontact.mcContact;
import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcGetContactException;
import org.lerot.mycontact.mcImportContact;
import org.lerot.mycontact.mcMappings;
//import org.lerot.mycontact.mcMember;
import org.lerot.mycontact.mcUtilities;
import org.lerot.mycontact.mcdb;
import org.lerot.mycontact.mcfield;
import org.lerot.mycontact.vcardContactReader;
import org.lerot.mycontact.gui.widgets.jswDropDownContactBox;

public class editPanel extends jswVerticalPanel implements ActionListener
{

	private static final long serialVersionUID = 1L;
	private static final int YES = 0;
	private static jswStyles tagtablestyles;
	private static jswStyles arraytablestyles;
	private static jswStyles tablestyles;
	private static jswStyles linktablestyles;
	private jswTextField atteditbox;
	private jswTextField[] attfieldeditbox = new jswTextField[10];
	private jswCheckbox[] tagcheckbox = new jswCheckbox[10];
	private mcAttribute edattribute;
	private String edit;
	private String editattributekey;
	private jswDropDownBox newlabel;
	private jswDropDownContactBox parentselect;
	private jswTextBox tideditbox;
	private jswLabel tagspanel;
	private jswTextField newtagpanel;
	private String vcarddirectory;
	private jswTextField atype;
	boolean addselector = false;

	private String edattributename;

	private jswDropDownBox linkselect;
	private jswDropDownBox groupselect;

	public editPanel()
	{
		vcarddirectory = mcdb.topgui.desktop;
		tagtablestyles = makeTagTableStyles();
		tablestyles = makeTableStyles();
		arraytablestyles = makeArrayTableStyles();
		linktablestyles = makeLinkTableStyles();
	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		mcContact selcontact = mcdb.selbox.getSelcontact();
		String action = evt.getActionCommand();
		if (action != null)
		{
			action = action.toUpperCase();

			if (action.equals("IMPORTVCARD"))
			{
				mcImportContact imcontact = importVcard();
				if (imcontact != null)
				{
					String imtid = imcontact.getAttributeValue("tid");
					int n = JOptionPane.showConfirmDialog(this,
							"Do you want to import this Vcard " + imtid,
							"Accept Import?", JOptionPane.YES_NO_OPTION);
					// System.out.println("reply =" + n);
					if (n == YES)
					{
						selcontact.updateFromVcardImport(imcontact);
					}
				}

			} else if (action.startsWith("NEWCONTACT"))
			{
				selcontact = mcContacts.createNewContact();
				mcdb.topgui.refreshView();
				mcdb.selbox.setSelcontact(selcontact);

			} else if (action.startsWith("IMPORT"))
			{
				JTextArea textArea = new JTextArea(6, 25);
				textArea.setText("");
				textArea.setEditable(true);

				int result = JOptionPane.showConfirmDialog(this, textArea,
						"Text Box and Text Area Example",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (result == 0)
				{
					String imaddr = textArea.getText();
					Map<String, String> addmap = mcAddressDataType
							.parse(imaddr);
					String addarray = mcUtilities
							.keyvaluesmaptoArrayString(addmap, "=");
					edattribute.getAttributevalue().setValue(addarray,"now");
					edattribute.dbupdateAttribute();
					edit = "";
					System.out.println("    update  " + editattributekey);
					System.out.println("    update to.. " + imaddr);

				}

			} else if (action.startsWith("VIEW:"))
			{
				String vstr = action.substring(5);
				selcontact = mcdb.selbox.FindbyID(vstr);
				mcdb.selbox.setSelcontact(selcontact);
				edit = "";
				mcdb.topgui.refreshView();

			} else if (action.startsWith("DISCONNECT:"))
			{
				String vstr = action.substring(11);
				selcontact.deleteAttributebyKey(vstr);
				System.out.println("removing " + vstr + " from " + selcontact);

			} else if (action.equals("ADDLINK"))
			{
				mcContact linkcontact = parentselect.getSelectedValue();
				String linktype = linkselect.getSelectedValue();
				System.out.println("adding " + selcontact + " to " + linktype
						+ " " + linkcontact);
				mcAttribute newatt = selcontact.createAttribute(linktype,
						linkcontact.getIDstr());
				newatt.getAttributevalue().setValue(linkcontact.getTID(),"now");
				newatt.dbupsertAttribute();

			} else if (action.startsWith("REFLECT:"))
			{
				String newdata = action.substring(8).toLowerCase();
				String[] data = newdata.split(":");
				mcContact linkcontact = mcdb.selbox.FindbyIDstr(data[2]);
				System.out.println(" link contact " + linkcontact + " " + data);
				String root = data[1];
				String qualifier = "";
				if (data.length > 2)
				{
					qualifier = data[2];
				} else
				{
					qualifier = selcontact.getIDstr();
				}
				if (root.equals("hasmember"))
					root = "memberof";
				else if (root.equals("memberof")) root = "hasmember";
				System.out.println("adding  link + to " + data[0] + " " + root
						+ " " + qualifier);
				mcAttribute newatt = selcontact.createAttribute(root, qualifier);
				newatt.getAttributevalue().setValue(linkcontact.getTID(),"now");
				newatt.dbupsertAttribute();
			} else if (action.startsWith("ADDGROUP"))
			{
				System.out.println("adding " + groupselect.getSelectedValue()
						+ " to " + selcontact);
				selcontact.addGroup(groupselect.getSelectedValue());
			} else if (action.startsWith("ADDTOGROUP"))
			{
				mcContact parent = parentselect.getSelectedValue();
				System.out.println("adding " + selcontact + " to " + parent);
			} else if (action.equals("CANCEL"))
			{
				edit = "";

			} else if (action.equals("EDITID"))
			{
				edit = "editid";
			} else if (action.startsWith("EDITATTRIBUTE:"))
			{
				edit = "editattribute";
				editattributekey = action.substring(14).toLowerCase();
				edattribute = selcontact.getAttributebyKey(editattributekey);
				if (edattribute == null) return;
			} else if (action.startsWith("REPLACE"))
			{
				JFileChooser chooser = new JFileChooser();
				File file = null;
				int returnValue = chooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION)
				{
					file = chooser.getSelectedFile();
				}
				System.out.println(" file selected " + file);
				jswImage newimage = new jswImage();
				newimage.importfile(file.getPath());
				String newattributevalue = newimage.getEncodedImage();
				selcontact.updateAttributebyKey(editattributekey,
						newattributevalue);
				System.out.println(" updated image " + editattributekey
						+ " for " + selcontact);
				edit = "";
			}

			else if (action.equals("UPDATEID"))
			{
				String newcontacttid = tideditbox.getText();
				selcontact.updateContactTID(newcontacttid);
				mcdb.selbox.refreshAll();
				mcdb.topgui.getContentPane().validate();
				edit = "";
			} else if (action.equals("UPDATELINKTOATTRIBUTE"))
			{
				String newattributequalifier = atteditbox.getText();
				edattribute.updateQualifier(newattributequalifier);
				edit = "";
				System.out.println("    update  " + editattributekey
						+ " update to.. " + newattributequalifier);
			} else if (action.equals("UPDATEATTRIBUTE"))
			{
				String newattributevalue = atteditbox.getText();
				String newattributequalifier = atype.getText();
				edattribute.setQualifier(newattributequalifier);
				edattribute.getAttributevalue().setValue(newattributevalue,"now");
				edattribute.dbupdateAttribute();
				edit = "";
				// System.out.println(" attribute "+edattribute.getRoot()+"
				// update to.. " + newattributevalue);
			} else if (action.equals("UPDATEARRAYATTRIBUTE"))
			{
				Map<String, String> valuelist = new LinkedHashMap<String, String>();
				for (jswTextField abox : attfieldeditbox)
				{
					if (abox == null) break;
					valuelist.put(abox.getTag(), abox.getText().trim());
				}
				edattribute.getAttributevalue().setValue(valuelist,"now");
				String newattributequalifier = atype.getText();
				edattribute.updateQualifier(newattributequalifier);
				edattribute.dbupdateAttribute();
				if (edattribute.getRoot().equals("name"))
				{
					if (selcontact.getTID().equals("new contact"))
					{
						String newtid = edattribute.getFormattedValue();
						mcAttribute tidattribute = selcontact
								.updateAttribute("tid", "", newtid);
						tidattribute.dbupdateAttribute();
						selcontact.setTID(newtid);
					}
				}

				edit = "";
			} else if (action.equals("DELETECONTACT"))
			{
				int n = JOptionPane.showConfirmDialog(this,
						"Do you want to delete this contact?",
						"DELETE CONTACT?", JOptionPane.YES_NO_OPTION);
				// System.out.println("reply =" + n);
				if (n == YES)
				{
					mcdb.selbox.getSelcontact().deleteContact();
					mcdb.selbox.refreshAll();
					edit = "";

				}
				edit = "editid";
			} else if (action.equals("DELETETAGS"))
			{
				Set<String> ataglist = new HashSet<String>();
				int k = 0;
				for (jswCheckbox atag : tagcheckbox)
				{
					if (atag != null)
					{
						String tag = atag.getTag();
						System.out.println(" TAG " + tag);
						if (atag.isSelected())
						{
							ataglist.add(tag);
							k++;
						}
					}
				}
				int n = JOptionPane.showConfirmDialog(this,
						"Do you want to delete these " + k + " Tags?",
						"DELETE TAGS?", JOptionPane.YES_NO_OPTION);
				// System.out.println("reply =" + n);
				if (n == YES)
				{
					edattribute.getAttributevalue().deleteTags(ataglist);
					edattribute.dbupdateAttribute();
					edit = "";
				}
				edit = "editattribute";

			} else if (action.equals("INSERTTAG"))
			{
				Set<String> ataglist = new HashSet<String>();
				// jswTextField atag = newtagpanel;
				String newtagvalue = newtagpanel.getText().trim();
				ataglist.add(newtagvalue);
				edattribute.getAttributevalue().insertTagValues(ataglist);
				edattribute.dbupdateAttribute();
				edit = "editattribute";
			} else if (action.equals("DELETEATTRIBUTE"))
			{

				int n = JOptionPane.showConfirmDialog(this,
						"Do you want to delete this attribute?",
						"DELETE ATTRIBUTE?", JOptionPane.YES_NO_OPTION);
				// System.out.println("reply =" + n);
				if (n == YES)
				{
					// mcdb.selbox.getSelcontact().deleteAttributebyKey(editattributekey);
					selcontact.removeAttributebyKey(edattribute.getKey());
					edattribute.dbdeleteAttribute();
					// selcontact.removeAttributebyKey(edattribute.getKey());
					edit = "";
				}
				edit = "editid";

			} else if (action == "CREATE NEW ATTRIBUTE")
			{
				String newattlabel = newlabel.getSelectedValue();
				mcAttribute newatt = selcontact.createAttribute(newattlabel,
						"");
				newatt.dbinsertAttribute();

			} else
				System.out.println("ep action1 " + action + " unrecognised ");
		}
		else
	    	System.out.println("ep action1 " + " is null ");

		makeEditPanel();

		// mcdb.topgui.getContentPane().validate();
	}

	public void clearEdit()
	{
		edit = "";
		editattributekey = "";
	}

	private mcImportContact importVcard()
	{
		String fname = vcarddirectory;
		File vfile = new File(fname);
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select Vcard");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("vcf",
				"vcf");
		fc.setFileFilter(filter);
		fc.setSelectedFile(vfile);
		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			// String vout = selcontact.toVcard();
			File fileToLoad = fc.getSelectedFile();
			String filepath = fileToLoad.getPath();

			try
			{
				mcImportContact nextcontact = null;
				vcarddirectory = fileToLoad.getParentFile().getCanonicalPath();
				mcMappings mappings = mcdb.topgui.currentcon
						.createMappings("import", "Vcard");
				vcardContactReader cr = new vcardContactReader(filepath,
						mappings);
				if (cr != null)
				{
					try
					{
						nextcontact = cr.getContact();

						System.out.println(" Loading.... " + nextcontact);

					} catch (mcGetContactException e)
					{
						System.out.println(
								" exception... " + cr.getExceptions().size());
					}

				}
				System.out.println(" returniung.with exceptions  "
						+ cr.getExceptions().size());
				return nextcontact;

			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}

	private jswStyles makeArrayTableStyles()
	{
		jswStyles tablestyles = jswStyles.clone("ArrayTableStyles",
				mcdb.tablestyles);
		jswStyle cellstyle = tablestyles.makeStyle("cell");
		cellstyle.putAttribute("backgroundColor", "#C0C0C0");
		cellstyle.putAttribute("foregroundColor", "GREEN");
		cellstyle.putAttribute("borderWidth", "1");
		cellstyle.putAttribute("borderColor", "white");
		cellstyle.setHorizontalAlign("LEFT");
		cellstyle.putAttribute("fontsize", "14");

		jswStyle cellcstyle = tablestyles.makeStyle("xcellcontent");
		// cellcstyle.putAttribute("backgroundColor", "transparent");
		cellcstyle.putAttribute("foregroundColor", "Red");
		cellcstyle.setHorizontalAlign("LEFT");
		cellcstyle.putAttribute("fontsize", "11");

		jswStyle col0style = tablestyles.makeStyle("col_0");
		col0style.putAttribute("fontStyle", Font.BOLD);
		col0style.setHorizontalAlign("RIGHT");
		col0style.putAttribute("minwidth", "true");

		jswStyle col1style = tablestyles.makeStyle("col_1");
		col1style.putAttribute("fontStyle", Font.BOLD);
		col1style.setHorizontalAlign("RIGHT");

		jswStyle tablestyle = tablestyles.makeStyle("table");
		tablestyle.putAttribute("backgroundColor", "White");
		tablestyle.putAttribute("foregroundColor", "Green");
		tablestyle.putAttribute("borderWidth", "2");
		tablestyle.putAttribute("borderColor", "blue");

		return tablestyles;
	}

	private jswVerticalPanel makeLinkedFromPanel(mcContact selcontact,
			String selector, String title)
	{

		jswVerticalPanel frame = new jswVerticalPanel();
		jswLabel memberheading = new jswLabel(title);
		frame.add(memberheading);

		jswTable memberpanel = new jswTable(selector, linktablestyles);

		if (selcontact != null)
		{
			mcAttributes getlinked = (new mcAttributes())
					.FindByAttributeValue(selector, selcontact.getTID());
			int row = 0;
			for (Entry<String, mcAttribute> anentry : getlinked.entrySet())
			{
				mcAttribute anattribute = anentry.getValue();
				int cid = anattribute.getCid();
				String qualifier = anattribute.getQualifier();
				mcContact linkcontact = mcdb.selbox.FindbyID(cid);
				String value = null;
				if (linkcontact != null)
					value = linkcontact.getTID().trim();
				else
					System.out.println(" Not found contact " + cid);

				if (!selcontact.hasAttributeByValue(value))
				{
					mcContact linkedcontact = mcdb.selbox.FindbyTID(value);
					if (linkedcontact != null)
					{
						jswLabel alabel = new jswLabel(value);
						memberpanel.addCell(alabel, row, 0);
						jswLabel aqual = new jswLabel(
								anattribute.getQualifier());
						memberpanel.addCell(aqual, row, 1);
						jswHorizontalPanel buttonpanel = new jswHorizontalPanel();

						jswButton viewcontact = new jswButton(this, "VIEW",
								"VIEW:" + linkedcontact.getIDstr());
						buttonpanel.add(viewcontact);
						jswButton disconnect = new jswButton(this, "REFLECT",
								"REFLECT:" + cid + ":" + selector + ":"
										+ qualifier);
						buttonpanel.add(disconnect);
						memberpanel.addCell(buttonpanel, row, 2);

					} else
					{
						if (value == null || value.equalsIgnoreCase("null"))
						{
							value = "New Linked Contact";
						}
						jswLabel alabel = new jswLabel(value);
						memberpanel.addCell(alabel, row, 0);
						jswLabel aqual = new jswLabel(
								anattribute.getQualifier());
						memberpanel.addCell(aqual, row, 1);
						jswHorizontalPanel buttonpanel = new jswHorizontalPanel();
						jswButton disconnect = new jswButton(this, "DELETE",
								"DELETE" + anattribute.getKey());
						buttonpanel.add(disconnect);
						memberpanel.addCell(buttonpanel, row, 2);
					}
					row++;
				}
			}
			if (row == 0)
			{
				return null;
			} else
			{
				frame.add(memberpanel);
				return frame;
			}
		} else
		{
			return null;
		}
	}

	private jswStyles makeLinkTableStyles()
	{
		jswStyles tablestyles = jswStyles.clone("ArrayTableStyles",
				mcdb.tablestyles);

		jswStyle cellstyle = tablestyles.makeStyle("cell");
		cellstyle.putAttribute("backgroundColor", "#C0C0C0");
		cellstyle.putAttribute("foregroundColor", "Blue");
		cellstyle.putAttribute("borderWidth", "1");
		cellstyle.putAttribute("borderColor", "white");
		cellstyle.setHorizontalAlign("LEFT");
		cellstyle.putAttribute("fontsize", "14");

		jswStyle hpstyle = tablestyles.makeStyle("xjswHorizontalPanel");
		hpstyle.putAttribute("backgroundColor", "#C0C0C0");
		hpstyle.putAttribute("cellbackgroundColor", "#C0C0C0");
		hpstyle.putAttribute("foregroundColor", "Green");
		hpstyle.putAttribute("borderWidth", "2");
		hpstyle.putAttribute("borderColor", "gray");
		hpstyle.putAttribute("cellBorderColor", "RED");
		hpstyle.putAttribute("cellBorderWidth", "10");

		jswStyle bpstyle = tablestyles.makeStyle("buttonpanel");
		bpstyle.putAttribute("backgroundColor", "RED");
		bpstyle.putAttribute("foregroundColor", "Green");
		bpstyle.putAttribute("borderWidth", "2");
		bpstyle.putAttribute("borderColor", "blue");

		jswStyle col0style = tablestyles.makeStyle("col_0");
		col0style.putAttribute("fontStyle", Font.BOLD);
		col0style.setHorizontalAlign("RIGHT");
		col0style.putAttribute("minwidth", "true");

		jswStyle col1style = tablestyles.makeStyle("col_1");
		col1style.putAttribute("fontStyle", Font.BOLD);
		col1style.setHorizontalAlign("LEFT");

		jswStyle col2style = tablestyles.makeStyle("col_2");
		col2style.putAttribute("horizontalAlignment", "RIGHT");
		col2style.putAttribute("minwidth", "true");

		return tablestyles;
	}

	private jswVerticalPanel makeLinkToPanel(mcContact selcontact,
			String selector, String title)
	{

		jswVerticalPanel frame = new jswVerticalPanel();
		jswLabel memberheading = new jswLabel(title);
		frame.add(memberheading);

		jswTable memberpanel = new jswTable(selector, linktablestyles);

		if (selcontact != null)
		{
			Map<String, mcAttribute> attributes = selcontact
					.getAttributesbyRoot(selector);
			if (attributes.size() < 1)
			{ return null; }

			int row = 0;

			for (Entry<String, mcAttribute> anentry : attributes.entrySet())
			{
				mcAttribute anattribute = anentry.getValue();
				// mcAttributeType attype = anentry.getValue();
				// if (anattribute.getDisplaygroup().contains("E"))
				if (anattribute.getRoot().equalsIgnoreCase(selector))
				{
					String value = anattribute.getValue();
					String qualifier = anattribute.getQualifier();
					String attributekey = anattribute.getKey();
					mcContact linkedcontact = mcdb.selbox.FindbyIDstr(qualifier);

					if (linkedcontact != null)
					{
						if (editattributekey.equalsIgnoreCase(attributekey))
						{
							jswLabel alabel = new jswLabel(value);
							memberpanel.addCell(alabel, row, 0);
							jswLabel aqlabel = new jswLabel(qualifier);
							memberpanel.addCell(aqlabel, row, 1);
							atteditbox = new jswTextField("qualifier");
							atteditbox.setText(qualifier);
							atteditbox.setEnabled(true);
							memberpanel.addCell(atteditbox, " FILLW ", row, 1);
							jswHorizontalPanel buttonpanel = new jswHorizontalPanel();
							jswButton viewcontact2 = new jswButton(this,
									"UPDATE", "UPDATELINKTOATTRIBUTE");
							buttonpanel.add(viewcontact2);
							jswButton viewcontact3 = new jswButton(this,
									"DELETE", "DELETEATTRIBUTE");
							buttonpanel.add(viewcontact3);
							memberpanel.addCell(buttonpanel, row, 2);
						} else
						{
							jswLabel alabel = new jswLabel(value);
							memberpanel.addCell(alabel, row, 0);
							jswLabel aqlabel = new jswLabel(qualifier);
							memberpanel.addCell(aqlabel, row, 1);
							jswHorizontalPanel buttonpanel = new jswHorizontalPanel();

							jswButton viewcontact2 = new jswButton(this, "VIEW",
									"VIEW:" + linkedcontact.getIDstr());
							buttonpanel.add(viewcontact2);
							jswButton disconnect = new jswButton(this,
									"EDIT ME",
									"EDITATTRIBUTE:" + anattribute.getKey());
							buttonpanel.add(disconnect);
							memberpanel.addCell(buttonpanel, row, 2);
						}
					} else
					{

						jswLabel alabel = new jswLabel(value);
						memberpanel.addCell(alabel, row, 0);
						alabel = new jswLabel(
								qualifier + " (not found linked contact )");
						memberpanel.addCell(alabel, row, 1);
						jswHorizontalPanel buttonpanel = new jswHorizontalPanel();
						jswButton disconnect = new jswButton(this, "DISCONNECT",
								"DISCONNECT:" + anattribute.getKey());
						buttonpanel.add(disconnect);
						memberpanel.addCell(buttonpanel, row, 2);
						buttonpanel.applyStyles(
								linktablestyles.getStyle("buttonpanel"));
					}
					row++;
				}
			}
			if (row == 0)
			{
				return null;
			} else
				frame.add(memberpanel);
			return frame;
		} else
		{
			return null;
		}

	}

	private jswStyles makeTableStyles()
	{
		jswStyles tablestyles = jswStyles.clone("TableStyles",
				mcdb.tablestyles);

		/*jswStyle tablestyle = tablestyles.makeStyle("table");
		// tablestyle.putAttribute("backgroundColor", "White");
		// tablestyle.putAttribute("foregroundColor", "Green");
		tablestyle.putAttribute("borderWidth", "2");
		tablestyle.putAttribute("borderColor", "blue");*/
		
		jswStyle tablestyle = tablestyles.makeStyle("table");
		tablestyle.putAttribute("backgroundColor", "#C0C0C0");
		tablestyle.putAttribute("foregroundColor", "Green");
		tablestyle.putAttribute("borderWidth", "2");
		tablestyle.putAttribute("borderColor", "blue");

		jswStyle jtablestyle = tablestyles.makeStyle("jswtable");
		// jtablestyle.putAttribute("backgroundColor", "White");
		// jtablestyle.putAttribute("foregroundColor", "Green");
		jtablestyle.putAttribute("borderWidth", "2");
		jtablestyle.putAttribute("borderColor", "blue");

		jswStyle bpstyle = tablestyles.makeStyle("buttonpanel");
		// jtablestyle.putAttribute("backgroundColor", "White");
		// jtablestyle.putAttribute("foregroundColor", "Green");
		bpstyle.putAttribute("borderWidth", "2");
		bpstyle.putAttribute("borderColor", "blue");

		jswStyle cellstyle = tablestyles.makeStyle("cell");
		// cellstyle.putAttribute("backgroundColor", "#C0C0C0");
		// cellstyle.putAttribute("foregroundColor", "GREEN");
		// cellstyle.putAttribute("borderWidth", "1");
		// cellstyle.putAttribute("borderColor", "white");
		cellstyle.setHorizontalAlign("LEFT");
		// cellstyle.putAttribute("fontsize", "14");

		jswStyle cellcstyle = tablestyles.makeStyle("xcellcontent");
		// cellcstyle.putAttribute("backgroundColor", "transparent");
		cellcstyle.putAttribute("foregroundColor", "Red");
		cellcstyle.setHorizontalAlign("LEFT");
		cellcstyle.putAttribute("fontsize", "11");

		jswStyle col0style = tablestyles.makeStyle("col_0");
		col0style.putAttribute("fontStyle", Font.BOLD);
		col0style.setHorizontalAlign("RIGHT");
		col0style.putAttribute("minwidth", "true");

		jswStyle col1style = tablestyles.makeStyle("col_1");
		col1style.putAttribute("fontStyle", Font.BOLD);
		col1style.setHorizontalAlign("LEFT");
		// col1style.putAttribute("backgroundColor", "White");
		col1style.putAttribute("width", 70);

		jswStyle col2style = tablestyles.makeStyle("col_2");
		col2style.putAttribute("horizontalAlignment", "RIGHT");
		// col2style.putAttribute("maxwidth", "true");
		col2style.putAttribute("FILLW", "true");
		col2style.putAttribute("foregroundColor", "Red");

		jswStyle col3style = tablestyles.makeStyle("col_3");
		col3style.putAttribute("horizontalAlignment", "RIGHT");
		col3style.putAttribute("minwidth", "true");

		return tablestyles;
	}

	public jswStyles makeTagTableStyles()
	{
		jswStyles tablestyles = jswStyles.clone("TagTableStyles",
				mcdb.tablestyles);
		
		jswStyle tablestyle = tablestyles.makeStyle("table");
		tablestyle.putAttribute("backgroundColor", "#C0C0C0");
		tablestyle.putAttribute("foregroundColor", "Green");
		tablestyle.putAttribute("borderWidth", "2");
		tablestyle.putAttribute("borderColor", "blue");
		
		jswStyle cellstyle = tablestyles.makeStyle("cell");
		cellstyle.putAttribute("backgroundColor", "#C0C0C0");
		cellstyle.putAttribute("foregroundColor", "Blue");
		cellstyle.putAttribute("borderWidth", "1");
		cellstyle.putAttribute("borderColor", "white");
		cellstyle.setHorizontalAlign("LEFT");
		cellstyle.putAttribute("fontsize", "14");

		jswStyle cellcstyle = tablestyles.makeStyle("cellcontent");
		// cellcstyle.putAttribute("backgroundColor", "xtransparent");
		cellcstyle.putAttribute("foregroundColor", "Blue");
		cellcstyle.setHorizontalAlign("LEFT");
		cellcstyle.putAttribute("fontsize", "11");

		jswStyle col0style = tablestyles.makeStyle("col_0");
		col0style.putAttribute("fontStyle", Font.BOLD);
		// col0style.putAttribute("backgroundColor", "Yellow");
		col0style.putAttribute("minwidth", 1);
		col0style.putAttribute("width", "50");

		jswStyle tabletyle = tablestyles.makeStyle("table");
		tabletyle.putAttribute("backgroundColor", "White");
		tabletyle.putAttribute("foregroundColor", "Green");
		tabletyle.putAttribute("borderWidth", "2");
		tabletyle.putAttribute("borderColor", "green");

		jswStyle col3style = tablestyles.makeStyle("col_1");
		col3style.putAttribute("horizontalAlignment", "RIGHT");
		col3style.putAttribute("FILLW", "true");

		return tablestyles;
	}

	public void makeEditPanel()
	{

		this.removeAll();
		jswHorizontalPanel newcontactbox = new jswHorizontalPanel("newcontact",
				false);
		add(newcontactbox);
		jswButton ncbutton = new jswButton(this, "New Contact", "NEWCONTACT");
		newcontactbox.add("MIDDLE", ncbutton);
		mcContact selcontact = mcdb.selbox.getSelcontact();
		setBackground(new Color(0, 0, 0, 0));
		jswHorizontalPanel idbox = new jswHorizontalPanel("idbox", false);
		add(idbox);
		jswLabel idpanel1 = new jswLabel(" ");
		idbox.add(idpanel1);
		if (selcontact != null)
			idpanel1.setText(selcontact.getIDstr());
		else
			idpanel1.setText(" no contact selected ");

		jswLabel idpanel2;
		jswLabel idpanel3;

		if (edit == "editid")
		{
			editattributekey = "";
			jswVerticalPanel tideditpanel = new jswVerticalPanel();
			tideditbox = new jswTextBox("");
			tideditpanel.add("FILLW", tideditbox);
			String tid = selcontact.getTID();
			if (tid.equalsIgnoreCase("new contact") || tid.isEmpty())
			{
				tid = selcontact.getName();
			}
			tideditbox.setText(tid);
			tideditbox.setEnabled(true);
			tagspanel = new jswLabel("tags");
			tideditpanel.add(tagspanel);
			idbox.add("FILLW", tideditpanel);
			tagspanel.setText(selcontact.getTags());
			jswButton idupdate = new jswButton(this, "UPDATE", "UPDATEID");
			idbox.add("RIGHT", idupdate);
			jswButton iddelete = new jswButton(this, "DELETE", "DELETECONTACT");
			idbox.add("RIGHT", iddelete);
			jswButton idcancel = new jswButton(this, "CANCEL", "CANCEL");
			idbox.add("RIGHT", idcancel);
		} else if (edit == "editattribute")
		{
			idpanel2 = new jswLabel(" ");
			idbox.add("  ", idpanel2);
			idpanel2.setText(mcdb.selbox.getSelcontact().getTID());
			// idpanel2.doStyling();
			idpanel3 = new jswLabel(" ");
			idbox.add(idpanel3);
			idpanel3.setText(mcdb.selbox.getSelcontact().getTags());
			jswButton idcancel = new jswButton(this, "CANCEL", "CANCEL");
			idbox.add("RIGHT", idcancel);
		} else
		{
			edit = "";
			editattributekey = "";
			idpanel2 = new jswLabel();
			idpanel2.applyStyles(mcdb.panelstyles.getStyle("largeLabel"));
			mcContact acontact = mcdb.selbox.getSelcontact();
			if(acontact != null)
			{
				String atid = acontact.getTID();
				idpanel2.setText(atid);
			}
			//String atid = acontact.getTID();
			//idpanel2 = new jswLabel(atid);
			idbox.add(idpanel2);
			// idpanel2.setText(mcdb.selbox.getSelcontact().getTID());

			idpanel3 = new jswLabel(" ");
			idbox.add(idpanel3);
			idpanel3.setText(mcdb.selbox.getSelcontact().getTags());
			jswButton idedit = new jswButton(this, "EDIT ME", "EDITID");
			idbox.add("RIGHT", idedit);
			jswButton imvcard = new jswButton(this, "VCARD", "IMPORTVCARD");
			idbox.add("RIGHT", imvcard);
		}

		jswTable attributepanel = new jswTable("attributes", tablestyles);
		attributepanel.setMarker("edittable");
		add(attributepanel);
		attributepanel.removeAll();
		mcdb.selbox.getSelcontact().fillContact();
		int row = 0;
		for (Entry<String, mcAttribute> anentry : mcdb.selbox.getSelcontact()
				.getAttributes().entrySet())
		{
			if (row > 40) continue;
			mcAttribute anattribute = anentry.getValue();
			String attributeroot = anattribute.getRoot();
			String attributekey = anattribute.getKey();
			String attributequalifier = anattribute.getQualifier();

			if (anattribute.isDisplaygroup("E"))
			{
				jswLabel alabel = new jswLabel(attributeroot);
				attributepanel.addCell(alabel, row, 0);
				if (edit == "editattribute")
				{
					if (editattributekey.equalsIgnoreCase(attributekey))
					{
						atype = new jswTextField("Qualifier?");
						atype.setText(attributequalifier);
						atype.setEnabled(true);
						attributepanel.addCell(atype, " FILLW ", row, 1);
						if (anattribute.isImage())
						{
							jswImage animage = new jswImage(
									anattribute.getValue());
							attributepanel.addCell(animage.DisplayImage(), row,
									2);
							jswPanel imagebox = new jswVerticalPanel();
							jswPanel buttonbox = new jswHorizontalPanel(
									"Editpanel buttonbox", false);
							jswButton idupdate = new jswButton(this, "REPLACE");
							buttonbox.add("RIGHT", idupdate);
							jswButton iddelete = new jswButton(this, "DELETE",
									"DELETEATTRIBUTE");
							buttonbox.add("RIGHT", iddelete);
							imagebox.add(buttonbox);
							jswLabel imagesize = new jswLabel(
									" size=" + anattribute.getValue().length());
							imagebox.add(imagesize);
							attributepanel.addCell(imagebox, row, 3);
						} else if (anattribute.isArray())
						{
							jswPanel buttonbox = new jswHorizontalPanel(
									" button box ", false);
							Map<mcfield, String> attarry = anattribute
									.getFieldValueMap();
							jswTable fieldlistbox = new jswTable("fieldtable",
									arraytablestyles);
							int frow = 0;
							for (Entry<mcfield, String> arow : attarry
									.entrySet())
							{
								mcfield rfield = arow.getKey();
								String fkey = rfield.getKey();
								String label = rfield.getLabel();
								String rvalue = arow.getValue();
								if (rvalue == null || rvalue.equals("null"))
									rvalue = " ";
								jswLabel keypanel = new jswLabel(label);
								attfieldeditbox[frow] = new jswTextField();
								attfieldeditbox[frow].setText(rvalue);
								attfieldeditbox[frow].setEnabled(true);
								attfieldeditbox[frow].setTag(fkey);
								fieldlistbox.addCell(keypanel, frow, 0);
								fieldlistbox.addCell(attfieldeditbox[frow],
										" FILLW ", frow, 1);
								frow++;
							}
							fieldlistbox.setEnabled(true);
							attributepanel.addCell(fieldlistbox, " FILLW ", row,
									2);
							jswButton idupdate = new jswButton(this, "UPDATE",
									"UPDATEARRAYATTRIBUTE");
							buttonbox.add("RIGHT", idupdate);

							jswButton iddelete = new jswButton(this, "DELETE",
									"DELETEATTRIBUTE");
							buttonbox.add("RIGHT", iddelete);
							if (anattribute.isType("address"))
							{
								jswButton idimport = new jswButton(this,
										"IMPORT", "IMPORTADDRESS");
								buttonbox.add("RIGHT", idimport);
							}
							attributepanel.addCell(buttonbox, row, 3);
						} else if (anattribute.isType("textlist"))
						{
							jswPanel buttonbox = new jswVerticalPanel();
							Set<String> attarry = anattribute.getTags();
							jswTable fieldlistbox = new jswTable("tagtable",
									tagtablestyles);
							int frow = 0;
							jswLabel keypanel = new jswLabel("new");

							fieldlistbox.addCell(keypanel, frow, 0);
							newtagpanel = new jswTextField();
							newtagpanel.setText(" ");
							newtagpanel.setEnabled(true);
							fieldlistbox.addCell(newtagpanel, " FILLW ", frow,
									1);
							frow++;
							for (String arow : attarry)
							{
								tagcheckbox[frow] = new jswCheckbox("");
								tagcheckbox[frow].setTag(arow);
								fieldlistbox.addCell(tagcheckbox[frow],
										" WIDTH=100 ", frow, 0);
								jswLabel keylabel = new jswLabel(arow);
								fieldlistbox.addCell(keylabel, frow, 1);
								frow++;
							}
							if (frow == 1)
							{
								jswLabel keypanel2 = new jswLabel("");
								fieldlistbox.addCell(keypanel2, frow, 1);
							}
							fieldlistbox.setEnabled(true);
							attributepanel.addCell(fieldlistbox, " FILLW ", row,
									2);
							jswButton idupdate = new jswButton(this, "ADD TAG",
									"INSERTTAG");
							buttonbox.add("RIGHT", idupdate);
							if (frow > 2)
							{
								jswButton iddelete = new jswButton(this,
										"DELETE SELECTED", "DELETETAGS");
								buttonbox.add("RIGHT", iddelete);
							}
							jswButton alldelete = new jswButton(this,
									"DELETE ALL", "DELETEATTRIBUTE");
							buttonbox.add("RIGHT", alldelete);
							attributepanel.addCell(buttonbox, row, 3);
						} else
						{
							String value = anattribute.getFormattedValue();
							atteditbox = new jswTextField("Box_" + value);
							atteditbox.setText(value);
							atteditbox.setEnabled(true);
							attributepanel.addCell(atteditbox, " FILLW ", row,
									2);
							jswPanel buttonbox = new jswHorizontalPanel(
									"button box", false);
							jswButton idupdate = new jswButton(this, "UPDATE",
									"UPDATEATTRIBUTE");
							buttonbox.add("RIGHT", idupdate);
							jswButton iddelete = new jswButton(this, "DELETE",
									"DELETEATTRIBUTE");
							buttonbox.add("RIGHT", iddelete);
							attributepanel.addCell(buttonbox, row, 3);
						}
					} else
					{
						jswLabel atype = new jswLabel();
						atype.setText(attributequalifier);
						attributepanel.addCell(atype, " FILLW ", row, 1);
						if (anattribute.isImage())
						{
							jswImage animage = new jswImage(
									anattribute.getValue());
							attributepanel.addCell(animage.DisplayImage(), row,
									2);
						} else
						{
							String value = anattribute.getFormattedValue();
							attributepanel.addCell(new jswLabel(value), row, 2);
						}
						jswPanel buttonbox = new jswVerticalPanel();
						jswButton idedit = new jswButton(this, "EDIT ME",
								"EDITATTRIBUTE:" + attributekey);
						buttonbox.add(idedit);
						// jswLabel imagesize = new jswLabel(
						// " size=" + anattribute.getValue().length());
						// buttonbox.setBackground(Color.pink);
						attributepanel.addCell(buttonbox, row, 3);
					}
				} else
				{
					jswLabel atype = new jswLabel();
					atype.setText(attributequalifier);
					// atype.setEnabled(false);
					attributepanel.addCell(atype, " FILLW ", row, 1);
					if (anattribute.isImage())
					{
						jswImage animage = new jswImage(anattribute.getValue());
						// animage.setHeight
						attributepanel.addCell(animage.DisplayImage(), row, 2);

					} else
					{
						String value = anattribute.getFormattedValue();
						jswLabel alabel2 = new jswLabel(value);
						attributepanel.addCell(alabel2, " FILLW ", row, 2);
					}
					jswPanel buttonbox = new jswVerticalPanel();
					jswButton idedit = new jswButton(this, "EDIT ME.",
							"EDITATTRIBUTE:" + attributekey);
					buttonbox.add(idedit);
					attributepanel.addCell(buttonbox, row, 3);
				}

				if (edit == "")
				{
					//jswLabel alab = new jswLabel();
					//alab.setText(attributequalifier);
					//attributepanel.addCell(alab, row, 1);
					jswButton idedit = new jswButton(this, "EDIT ME..",
							"EDITATTRIBUTE:" + attributekey);
					attributepanel.addCell(idedit, row, 3);
				} else if (edit == "editattribute")
				{

				}
				row++;
			}

		}
		if (edit == "")
		{
			jswHorizontalPanel newattributepanel = new jswHorizontalPanel();
			newlabel = new jswDropDownBox(null, "Select:","something");
			// newlabel.setPreferredSize(new Dimension(100, 24));
			ArrayList<String> varry = mcAttributeTypes.toList();
			newlabel.addList(varry);
			newattributepanel.add(" WIDTH=200 ", newlabel);
			jswPanel buttonbox = new jswHorizontalPanel();
			jswButton idupdate = new jswButton(this, "CREATE NEW ATTRIBUTE");
			buttonbox.add("RIGHT", idupdate);
			newattributepanel.add("RIGHT", buttonbox);
			add(" FILLW ", newattributepanel);
		}

		jswVerticalPanel reltionslist = makeLinkToPanel(selcontact, "related",
				"Related to");
		if (reltionslist != null) add(reltionslist);
		jswVerticalPanel memberstlist = makeLinkToPanel(selcontact, "hasmember",
				"Has Members");
		if (memberstlist != null) add(memberstlist);
		jswVerticalPanel orglist = makeLinkToPanel(selcontact, "memberof",
				"Member Of");
		if (orglist != null) add(orglist);

		jswVerticalPanel linkedcontactlist = makeLinkedFromPanel(selcontact,
				"hasmember", "ex-Org");
		if (linkedcontactlist != null) add(linkedcontactlist);
		jswVerticalPanel linkedmemberlist = makeLinkedFromPanel(selcontact,
				"memberof", "ex-Member");
		if (linkedmemberlist != null) add(linkedmemberlist);
		jswVerticalPanel linkedrelationlist = makeLinkedFromPanel(selcontact,
				"related", "ex-Related");
		if (linkedrelationlist != null) add(linkedrelationlist);

		if (edit == "")
		{

			jswHorizontalPanel newmemberpanel = new jswHorizontalPanel();
			newmemberpanel.applyStyles(mcdb.panelstyles, "borderstyle");

			linkselect = new jswDropDownBox(null, edattributename);
			Vector<String> llist = new Vector<String>();
			llist.add("memberof");
			llist.add("hasmember");
			llist.add("related");
			linkselect.addList(llist);
			newmemberpanel.add(linkselect);
			parentselect = new jswDropDownContactBox("Select Contact", true,
					false, 500);
			parentselect.addList(mcdb.selbox.getAllcontactlist()
					.makeOrderedContactsVector());
			newmemberpanel.add(parentselect);// paul to fix
			atteditbox = new jswTextField("attedit");
			atteditbox.setEnabled(true);
			newmemberpanel.add(atteditbox);
			jswPanel buttonbox = new jswHorizontalPanel();
			jswButton addmember = new jswButton(this, "ADD AS LINK", "ADDLINK");
			buttonbox.add("RIGHT", addmember);
			newmemberpanel.add("RIGHT", buttonbox);
			add(" FILLW ", newmemberpanel);

			jswHorizontalPanel groupmemberpanel = new jswHorizontalPanel();
			groupmemberpanel.applyStyles(mcdb.panelstyles, "borderstyle");

			groupselect = new jswDropDownBox(this, "Select Group");
			groupselect.addList(mcdb.selbox.getTaglist());
			groupmemberpanel.add(" WIDTH=300 ", groupselect);// paul to fix
			// atteditbox = new jswTextField();
			// atteditbox.setEnabled(true);
			// groupmemberpanel.add(atteditbox);
			jswPanel bbuttonbox = new jswHorizontalPanel();
			jswButton addgroup = new jswButton(this, "ADD GROUP AS MEMBERS",
					"ADDGROUP");
			bbuttonbox.add("RIGHT", addgroup);
			groupmemberpanel.add("RIGHT", bbuttonbox);
			add(" FILLW ", groupmemberpanel);
		}
		mcdb.topgui.getContentPane().validate();
	}

}
