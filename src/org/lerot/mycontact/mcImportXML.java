package org.lerot.mycontact;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.lerot.mywidgets.jswLabel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class mcImportXML extends mcImports
{
	boolean test = true;
	jswLabel message;
	static String[] Nfields = { "sn", "fn", "mn", "title", "sufix" };
	static String[] ADRfields = { "pobox", "extaddr", "street", "city",
			"county", "postcode", "country" };

	public mcImportXML(String importfilename)
	{
		super(importfilename, "xml");
	}

	public LinkedHashMap<String, mcImportexception> importall(boolean test,
			jswLabel inmessage)
	{
		System.out.println(" import xml "+ importfilename);
		message = inmessage;
		if (message != null)
			message.setText(" importing xml " + importfilename);
		mappings = mcdb.topgui.currentcon.createMappings("import", "XML");
		if (!test)
		{
		//	initialiseImportDataTable(importfilename, importtype);
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		LinkedHashMap<String, mcImportexception> exceptions = new LinkedHashMap<String, mcImportexception>();
		mcAttribute anattribute = new mcAttribute(0);
		Vector<String> attkeylist = anattribute .dbloadAttributeKeyList();
		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(importfilename);
			// get the root element
			Element docEle = dom.getDocumentElement();

			// get a nodelist of elements
			NodeList nl = docEle.getElementsByTagName("contact");
			int k = 1;
			if (nl != null && nl.getLength() > 0)
			{
				for (int i = 0; i < nl.getLength(); i++)
				{
					Element el = (Element) nl.item(i);
					mcContact icontact = new mcContact();
					icontact.loadXML(el, i);
					//String ct = icontact.toXML(attkeylist);
					//System.out.println(" imported contact :" + ct);
					mcContact existingcontact = mcdb.selbox.getAllcontactlist()
							.FindbyTID(icontact.getTID());
					if (existingcontact == null)
					{
						System.out.println(" not found  contact :" + icontact);
					} else
					{
						//if(existingcontact.getID()==0)
						{
							System.out.println(" found "+existingcontact);
						}
						
						 if (icontact.matches(existingcontact))
						{
							System.out.println(" importing contact :"
							 +icontact+" no changes");
						} else
						{
							if (!test)
							{
								System.out.println(
										" changed contact  :" + icontact);
								int changes = existingcontact
										.updateContact(icontact);
								 if(changes>0)
								 {
								 existingcontact.updateContact();
								 System.out.println(
											" updated contact  :" + icontact);
								 }
							}
							k++;
						}
					}
				}
				System.out
						.println(" importing " + nl.getLength() + " Contacts");
			}

		} catch (ParserConfigurationException pce)
		{
			pce.printStackTrace();
		} catch (SAXException se)
		{
			se.printStackTrace();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}

		if (message != null)
		{
			message.setText("loaded  " + " contacts " + getCurrentposition());
			message.repaint();
		}
		return exceptions;
	}

}
