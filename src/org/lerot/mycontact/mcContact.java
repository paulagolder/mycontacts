package org.lerot.mycontact;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ezvcard.VCard;
import ezvcard.property.StructuredName;
import ezvcard.property.VCardProperty;

public class mcContact extends mcDataObject implements Comparable<mcContact>
{

	mcAttributes attributes = null;
	private int CID = 0;
	private String tags =null;
	String TID = "new contact";
	Timestamp update = null;
	List<String> noprintlist = new ArrayList<String>();
	  
	String user = null;


	public mcContact()
	{
		super();
		attributes = new mcAttributes();
		noprintlist.add("kind");
		noprintlist.add("tid");
	}

	public mcContact(int id)
	{
		super();
		setCID(id);
		attributes = new mcAttributes();
	}

	public mcContact(mcImportContact impcontact)
	{
		setCID((new mcContacts()).getNewID());
		String fn = "";
		attributes = new mcAttributes();
		for (Entry<String, mcImportAttribute> arec : impcontact.getAttributes()
				.entrySet())
		{
			mcImportAttribute imatt = arec.getValue();
			String aroot = imatt.getRoot();
			String aqual = imatt.getQualifier();
			if (aroot.equalsIgnoreCase("tid"))
			{
				TID = imatt.getValue();
			}
			if (aroot.equalsIgnoreCase("name"))
			{
				fn = imatt.getValue();
			}
			mcAttribute newatt = createAttribute(aroot, aqual,
					imatt.getValue());
			attributes.put(newatt);
		}
		if ((TID == null || TID.isEmpty()) && !fn.isEmpty())
		{
			TID = fn;
			mcAttribute newatt = createAttribute("tid", "", fn);
			attributes.put(newatt);
		}
	}

	public void addMember(String vstr, mcContact selcontact)
	{
		createAttribute(vstr, "", selcontact.getTID());
	}

	@Override
	public int compareTo(mcContact othercontact)
	{
		return TID.compareTo(othercontact.TID);
	}

	public boolean containsValue(String testvalue)
	{
		String tid = getTID();
		if (mcUtilities.containedBy(testvalue, tid)) { return true; }
		for (Entry<String, mcAttribute> anat : attributes.entrySet())
		{
			mcAttribute anattribute = anat.getValue();
			if (anattribute.containsValue(testvalue)) { return true; }
		}
		return false;

	}

	public mcAttribute createAttribute(String attroot, String attqual)
	{
		String root = attroot;
		String qualifier = attqual;
		mcAttribute oldatt = getAttribute(attroot, qualifier);
		int k = 1;
		while (oldatt != null)
		{
			qualifier = "alt" + k;
			oldatt = getAttribute(root, qualifier);
			k++;
		}
		mcAttribute newatt = new mcAttribute(getCID(), root, qualifier);
		attributes.put(newatt);
		return newatt;
	}

	public mcAttribute createAttribute(String root, String qualifier,
			String value)
	{
		mcAttribute newatt = createAttribute(root, qualifier);
		newatt.setValue(value);
		newatt.setUpdate();
		return newatt;
	}

	private void createAttribute(String aroot, String aqual, String avalue,
			String updated)
	{
		mcAttribute newatt = createAttribute(aroot, aqual, avalue);
		newatt.setUpdate(updated);
	}

	public void deleteAttributebyKey(String attkey)
	{
		int id = this.getCID();
		String part[] = mcUtilities.parseLabel(attkey.toLowerCase());
		doDelete("attributeValues", " cid=" + id + " and  root  = '" + part[0]
				+ "' and qualifier ='" + part[1] + "' ;");
		System.out.println(
				" deleting attribute from  " + this.TID + " " + attkey);
		attributes.remove(attkey);
	}

	public void deleteContact()
	{
		int id = this.getCID();
		mcdb.selbox.remove(this);
		doDelete("attributeValues", " cid=" + id);
		System.out.println(" deleting contact  " + this.TID);
	}

	public void disconnectContact(mcContact selcontact)
	{

		// paul fix
		// doDelete("memberof", " parentid = " + this.CID + " and memberid = "
		// + selcontact.CID);
		// this.fillMembers();
		// selcontact.fillMembers();
	}

	public void fillContact()
	{
		int id = this.getCID();
		attributes = (new mcAttributes()).getAttributes(id);
		if (attributes == null)
		{
			System.out.println(" no attributes for contact " + id + " +TID");
		}

	}

	public Map<String, mcAttribute> filterAttributes(String filter)
	{
		Map<String, mcAttribute> result = new HashMap<String, mcAttribute>();
		for (Entry<String, mcAttribute> anat : attributes.entrySet())
		{
			mcAttribute anattribute = anat.getValue();
			if (filter == null)
			{
				result.put(anattribute.getKey(), anattribute);

			} else if (anattribute.getKey().contains(filter))
			{
				result.put(anattribute.getKey(), anattribute);
			}

		}
		return result;
	}

	public mcAttribute getAddress()
	{
		mcAttribute anattribute = getAttributebyKey("address");
		if (anattribute == null)
		{
			mcAttribute org = getAttributebyKey("org");
			if (org != null)
			{
				String orgtid = org.getValue();
				mcContact orgcontact = mcdb.selbox.getAllcontactlist()
						.FindbyTID(orgtid);
				if (orgcontact != null)
				{
					anattribute = orgcontact.getAttributebyKey("address");
				}
			}
		}
		return anattribute;
	}

	public mcAttribute getAttribute(String attroot, String qualifier)
	{

		mcAttribute foundatt = attributes.find(attroot, qualifier);
		return foundatt;
	}

	public mcAttribute getAttributebyKey(String atkey)
	{
		return attributes.find(atkey);
	}

	public mcAttributes getAttributes()
	{
		if (attributes == null || attributes.size() == 0)
		{
			attributes = (new mcAttributes()).getAttributes(this.getCID());
		}
		return attributes;
	}

	public Map<String, mcAttribute> getAttributesbyKey(String attkey)
	{
		Map<String, mcAttribute> result = new HashMap<String, mcAttribute>();
		for (Entry<String, mcAttribute> anat : attributes.entrySet())
		{
			mcAttribute anattribute = anat.getValue();
			if (attkey.equalsIgnoreCase(anattribute.getKey()))
			{
				result.put(anattribute.getKey(), anattribute);
			}
		}
		return result;
	}

	public Map<String, mcAttribute> getAttributesbyRoot(String attroot)
	{
		Map<String, mcAttribute> result = new HashMap<String, mcAttribute>();
		for (Entry<String, mcAttribute> anat : attributes.entrySet())
		{
			mcAttribute anattribute = anat.getValue();
			if (attroot.equalsIgnoreCase(anattribute.getRoot()))
			{
				result.put(anattribute.getKey(), anattribute);
			}
		}
		return result;
	}

	public String getAttributeValue(String root, String qual)
	{
		mcAttribute anatt = attributes.get(root + "/" + qual);
		if (anatt == null) return null;
		String attvalue = anatt.getValue();
		return attvalue;
	}

	public String getAttributeValuebyKey(String attkey)
	{
		String[] part = mcUtilities.parseLabel(attkey);
		return getAttributeValue(part[0], part[1]);
	}

	public Vector<String> getAttributeValues(String attkey)
	{
		Vector<String> result = new Vector<String>();
		int k = 0;
		for (Entry<String, mcAttribute> anat : attributes.entrySet())
		{
			mcAttribute anattribute = anat.getValue();
			if (attkey.equalsIgnoreCase(anattribute.getKey()))
			{
				result.add(anattribute.getFormattedValue());
				k++;
			} else if (attkey.equalsIgnoreCase(anattribute.getRoot()))
			{
				result.add(anattribute.getFormattedValue());
				k++;
			}
		}
		if (k > 0)
			return result;
		else
			return null;
	}

	public mcContact getContact(int id)
	{
		
		mcContact acontact = new mcContact();
		acontact.setID(id);
		if (acontact.getCID() > 0)
		{
			acontact.fillContact();
		    return acontact;
		}
		return null;
	}

	public static mcContact getContact(String idstr)
	{
		mcContact acontact = new mcContact();
		acontact.setID(Integer.valueOf(idstr));
		if (acontact.getCID() > 0)
		{
			acontact.fillContact();
		    return acontact;
		}
		return null;
	}

	public int getID()
	{
		return this.getCID();
	}

	public String getIDstr()
	{
           String ids = ("000000" + this.getCID());
           int l = ids.length();
		   return ids.substring(l-6);
	}
	
	public static String makeIDstr(String id)
	{
           String ids = ("000000" + id);
           int l = ids.length();
		   return ids.substring(l-6);
	}
	
	public String getSimpleIDstr()
	{
		
		return ""+this.getCID();
	}


	private String getlastupdate()
	{
		String lastupdate = null;
		for (Entry<String, mcAttribute> arec : attributes.entrySet())
		{
			mcAttribute imatt = arec.getValue();
			String updatedt = imatt.getUpdate();
			if (lastupdate == null) lastupdate = updatedt;
			if (lastupdate != null && updatedt != null)
			{
				if (lastupdate.compareTo(updatedt) < 0) lastupdate = updatedt;
			}
		}
		return lastupdate;
	}

	public String getName()
	{
		String name = TID;
		mcAttribute nameat = attributes.find("name");
		if (nameat != null) name = nameat.getFormattedValue();
		return name;
	}

	public String getEmail()
	{
		mcAttribute emailat = attributes.find("email");
		if(emailat != null)
	   	return emailat.getFormattedValue();
		else 
			return null;
	}
	public String getName(String fmt)
	{
		String name = TID;
		mcAttribute nameat = attributes.find("name");
		String fname=null;
		if(nameat!=null) 
		 fname = nameat.getFormattedValue(fmt).trim();
		if (fname != null && !fname.isEmpty()) name = fname;
		//else fname =  getAttributebyKey("email").getValue();
		return name;
	}

	public String getPosition()
	{
		mcAttribute posit = attributes.find("position");
		if (posit != null) return posit.getValue();
		return null;
	}

	public Set<String> getTagList()
	{
		mcAttribute tagat = getAttributebyKey("tags");
		if (tagat == null) return null;
		Set<String> tags = mcTagListDataType.getTags(tagat);
		return tags;
	}
	
	public String getTags()
	{
		return tags;
	}

	public String getTID()
	{
		//if(TID=="new contact" && getAttributebyKey("email")!= null) return getAttributebyKey("email").getValue();
		//else 
			return TID;
	}

	

	public Timestamp getUpdate()
	{
		return update;
	}

	public String getUser()
	{
		return user;
	}

	public boolean hasAttributeByValue(String value)
	{

		for (Entry<String, mcAttribute> anat : attributes.entrySet())
		{
			mcAttribute anattribute = anat.getValue();
			String testvalue = anattribute.getValue();
			if (value.equalsIgnoreCase(testvalue)) { return true; }
		}
		return false;
	}

	public boolean hasTag(String atag)
	{
		mcAttribute tagat = getAttributebyKey("tags");
		if (tagat == null) return false;
		mcDataType dt = tagat.getDataType();

		return dt.valueContained(tagat.getValue(), atag);

	}

	public void insertAttribute(String root)
	{
		mcAttribute newatt = new mcAttribute(getCID(), root);
		newatt.dbinsertAttribute();
	}

	public void insertAttribute(String newroot, String newqual)
	{
		mcAttribute newatt = new mcAttribute(getCID(), newroot);
		newatt.dbinsertAttribute();
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public int insertNewContact()
	{
		int nid = (new  mcContacts()).getNewID();
		// int nid = getID();
		PreparedStatement st;
		String query = "insert into attributeValues(cid, root,qualifier,value )  values(?, ?,?,?)";

		try
		{
			getConnection();
			st = con.prepareStatement(query);
			st.setInt(1, nid);
			st.setString(2, "tid");
			st.setString(3, "");
			st.setString(4, getTID());
			st.executeUpdate();
			st.close();
			String query2 = "insert into attributeValues(cid, root,qualifier,value )  values(?, ?,?,?)";

			st = con.prepareStatement(query2);
			st.setInt(1, nid);
			st.setString(2, "tags");
			st.setString(3, "");
			st.setString(4, "#newcontact;");
			st.executeUpdate();

			st.close();
			String query3 = "insert into attributeValues(cid, root,qualifier,value )  values(?, ?,?,?)";

			st = con.prepareStatement(query3);
			st.setInt(1, nid);
			st.setString(2, "name");
			st.setString(3, "");
			st.setString(4, getTID());
			st.executeUpdate();

			st.close();
			disconnect();
			return nid;

		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	public boolean isList()
	{
		if (tags.endsWith("list")) return true;
		return false;
	}

	void load(Map<String, String> inmap)
	{
		if (inmap.containsKey("cid")) setCID(Integer.valueOf(inmap.get("cid")));
		if (inmap.containsKey("tid")) TID = inmap.get("tid");
		// if (inmap.containsKey("name")) name = inmap.get("name");
		//if (inmap.containsKey("type")) kind = inmap.get("kind");
		if (inmap.containsKey("user")) user = inmap.get("user");
	}

	void load(ResultSet inmap)
	{
		try
		{
			setCID(inmap.getInt("cid"));
			TID = inmap.getString("tid");
			user = inmap.getString("user");
			update = inmap.getTimestamp("update");
		} catch (SQLException e)
		{

		}

	}

	public void loadXML(Element ct, int contactnumber)
	{
		//kind = ct.getAttribute("kind");
		//kind = ct.getAttribute("kind");
		TID = ct.getAttribute("id");
		String updatetext = ct.getAttribute("updated");
		try {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		    Date parsedDate = dateFormat.parse( updatetext);
		    update = new java.sql.Timestamp(parsedDate.getTime());
		} catch(Exception e) { 
			update=null;
		}
		setCID(contactnumber);
	
		NodeList nl = ct.getChildNodes();
		if (nl != null && nl.getLength() > 0)
		{
			for (int i = 0; i < nl.getLength(); i++)
			{
				if(nl.item(i).getNodeType() == Node.ELEMENT_NODE)
				{
				Node at = ( nl.item(i));			
				mcAttribute anatt = mcAttribute.attributefromXML(at);
				anatt.loadXML(at);
				attributes.put(anatt);
				}
			}
		}
	}

	public String makeBlockAddress(String string)
	{

		return string;
	}

	public String makeBlockAddress(String sep, boolean showuk)
	{
		String ostr = "";
		String fmt = "Title Fn Mn Sn ";
		String name = getName(fmt);
		if (!name.trim().isEmpty()) ostr += name + sep;
		String position = getPosition();
		if (position != null) ostr += position + sep;
		mcAttribute address = getAddress();

		if (address != null && !address.isEmpty())
		{
			ostr += ((mcAddressDataType) address.getDataType())
					.getBlockFormattedValue(address.getValue(), sep, showuk);
		} else
		{
			System.out.println(" no address for :" + this);
			// ostr = null;
		}

		return ostr;
	}

	public boolean matches(mcContact testcontact)
	{
		String reftid = getTID();
		String testtid = testcontact.getTID();
		if (!reftid.equalsIgnoreCase(testtid)) return false;
		String existingtype = getKind();
		String importedtype = testcontact.getKind();
		if (!existingtype.equalsIgnoreCase(importedtype)) return false;
		for (Entry<String, mcAttribute> entry : attributes.entrySet())
		{
			mcAttribute existingatt = entry.getValue();
			mcAttribute importedatt = testcontact.getAttributebyKey(existingatt.getKey());
			if (!existingatt.matches(importedatt)) return false;
		}
		return true;
	}

	public int mergeContact(mcContact fcontact)
	{
		int unmatched = 0;
		for (Entry<String, mcAttribute> anentry : fcontact.attributes
				.entrySet())
		{
			mcAttribute aniattribute = anentry.getValue();
			if (aniattribute != null)
			{
				if (!containsValue(aniattribute.getValue()))
				{
					updateAttributebyKey(aniattribute.getKey(),
							aniattribute.getValue());
				}
			}
		}
		return unmatched;
	}
	
	public int updateContact(mcContact fcontact)
	{
		//int unmatched = 0;
		//Timestamp updated = getUpdate();
		//Timestamp fupdated = fcontact.getUpdate();
		int updates=0;
		
		for (Entry<String, mcAttribute> anentry : fcontact.attributes
				.entrySet())
		{
			mcAttribute aniattribute = anentry.getValue();
			if (aniattribute != null)
			{
				//String anupdated = aniattribute.getUpdate();
				String root = aniattribute.getRoot();
				String qual = aniattribute.getQualifier();
				mcAttribute matchedatt = getAttribute(root, qual);
				if (matchedatt != null)
				{
					if (matchedatt.matches(aniattribute))
					{

					} else
					{
						String maupdate = matchedatt.getUpdate();
						String aniupdate = aniattribute.getUpdate();
						if(aniupdate == null && maupdate != null)
						{
							matchedatt.updateAttribute(aniattribute);
							updates++;
						}
						else if(maupdate.compareTo(aniupdate)<0)
						{
						matchedatt.updateAttribute(aniattribute);
						updates++;
						}
					}
				} else
				{
					putAttribute(aniattribute);
					updates ++;
				}
			}
		}
		return updates;
	}

	public void putAttribute(mcAttribute newatt)
	{
		attributes.put(newatt);

	}

	public mcAttributes refreshAttributes()
	{
		attributes = (new mcAttributes()).getAttributes(this.getCID());
		return attributes;
	}

	public void removeAttributebyKey(String key)
	{
		attributes.remove(key);

	}

	public void setAttributes(mcAttributes attributes)
	{
		this.attributes = attributes;
	}

	public void setID(int iD)
	{
		setCID(iD);
	}

	public void setTID(String tID)
	{
		TID = tID;
	}

	public void xsetKind(String group)
	{
		//this.kind = group;
	}

	public void setUpdate()
	{

		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(
				now.getTime());
		setUpdate(currentTimestamp);
	}

	public void setUpdate(Timestamp update)
	{
		this.update = update;
	}

	@Override
	public String toString()
	{
		String outstring = "contact :" + getCID() + " " + TID + "   ";
		return outstring;
	}

	public String toVcard()
	{
		mcMappings mappings = mcdb.topgui.currentcon.createMappings("export",
				"Vcard");
		Vector<String> attkeys = mappings.getKeys();
		return toVcard(attkeys, mappings,false);
	}

	public String toVcard(Vector<String> attkeys, mcMappings mappings, boolean owndrive)
	{
		mcContact acontact = this;
		mcAttributes attributes = acontact.getAttributes();
		String outcard = "";
		boolean isok = false;
		for (String attkey : attkeys)
		{
			mcAttribute anattribute = attributes.get(attkey);
			if (anattribute != null && anattribute.getFormattedValue() != null
					&& !anattribute.getFormattedValue().isEmpty()
					&& !attkey.equalsIgnoreCase("name"))
				isok = true;
			
		}

		if (isok)
		{
			
			// System.out.println(" Printing " + acontact + " "
			// + attributes.size());
			String nl = "\n";
			String cid = acontact.getTID();
       
			outcard += "BEGIN:VCARD" + nl;
			outcard += "VERSION:4.0" + nl;
			outcard += "PRODID:MYContacts.lerot/version " + mcdb.version + nl;
			String rev = acontact.getlastupdate();
			outcard += "REV:" + rev + nl;
			if (cid == null)
			{
				mcAttribute nameatt = acontact.getAttributebyKey("name");
				if (nameatt != null) cid = nameatt.getFormattedValue();
			}
			outcard += ("FN:" + cid.toLowerCase() + nl);
			for (String attroot : attkeys)
			{
				boolean nocategory = true;
				mcAttributes anattributes = attributes.findAllbyRoot(attroot);
				if(owndrive)
				{
					anattributes = attributes.findAllnoQualifier(attroot);
				}
				String foriegnlabel = mappings.get(attroot).toUpperCase();
				
				int replication = 1;
				if (foriegnlabel != null)
				{

					for (Entry<String, mcAttribute> entry : anattributes
							.entrySet())
					{
						mcAttribute anattribute = entry.getValue();
						if(owndrive)  // paul to fix with new vcardtype
						{
							if(foriegnlabel.contains("TYPE"))
								foriegnlabel ="MOBILE";
						}
						if (foriegnlabel.contains("ENCODING=b"))
						{

							String rawvalue = anattribute.getValue();
							String[] encodedlines = mcImageDataType
									.getVCardEncodedLines(foriegnlabel, rawvalue);
							for (String line : encodedlines)
							{
								if (line != null) outcard += (line) + nl;
							}

						} else
						{
							String vvalue = anattribute.getVcardValue();
							String vqual = anattribute.getQualifier();
							if (vqual != null && !vqual.isEmpty())
							{
								if (mcUtilities.isNumeric(vqual))
								{
								//	vqual = "" + replication;
									replication++;
								}
								vqual = ";TYPE=" + vqual.toUpperCase();
							}

							if (vvalue != null && !vvalue.isEmpty()
									&& !vvalue.equalsIgnoreCase("null"))
							{
								if (foriegnlabel.equalsIgnoreCase("categories"))
									nocategory = false;
								outcard += (foriegnlabel + vqual + ":" + vvalue
										+ nl);
							}
						}
					}
					if (foriegnlabel.equalsIgnoreCase("categories"))
					{
						if (nocategory)
						{
					//		String vvalue = "other";
					//		outcard += (foriegnlabel + ": " + vvalue + nl); paul to fix
						}
					}
				}
			}
			outcard += ("END:VCARD" + nl);
			outcard += ("");
		} else
		{
			System.out.println(
					"Not  Printing " + acontact + " " + attributes.size());
		}
		return outcard;
	}

	public String toXML(Vector<String> attkeys)
	{
		String xmlout = "<contact tid='" + StringEscapeUtils.escapeXml(getTID())
				+ "' kind='" + getKind() + "' CID='" + getID() + "'   >" + "\n";
		for (String attkey : attkeys)
		{
			mcAttributes sublist = attributes.findAllbyRoot(attkey);
			for (Entry<String, mcAttribute> anentry : sublist.entrySet())
			{
				mcAttribute anattribute = anentry.getValue();
				if(anattribute.isImage())
				{
					System.out.println(" is image");
					String xmlatt = anattribute.toXML();
					xmlout += xmlatt;
				}
				else if(noprintlist.contains(anattribute.getRoot()))
					;
				else
				{
				String xmlatt = anattribute.toXML();
				xmlout += xmlatt;
				}
			}
		}

		xmlout += "</contact>" + "\n";
		return xmlout;
	}
	
	private String getKind()
	{
		return "person";
	}

	public String toXCard(Vector<String> attkeys, mcMappings notusedhere)
	{
		String xmlout = "<contact id='" + StringEscapeUtils.escapeXml(getTID())
				+ "' type='" + getKind() + "'   >" + "\n";
		for (String attkey : attkeys)
		{
			mcAttribute anattribute = attributes.get(attkey);
			if (anattribute != null)
			{
				String xmlatt = anattribute.toXML();
				xmlout += xmlatt;
			}
		}

		xmlout += "</contact>" + "\n";
		return xmlout;
	}

	public mcAttribute updateAttribute(String attroot, String attqual, String newvalue)
	{
		mcAttribute anattribute = getAttribute(attroot, attqual);

		if (anattribute == null)
		{
			anattribute = createAttribute(attroot, attqual);
			attributes.put(anattribute);
		}
		anattribute.setValue(newvalue);
		return anattribute;
	}

	public void updateAttributebyKey(String editattributekey,
			String newattributevalue)
	{
		String[] part = mcUtilities.parseLabel(editattributekey);
		updateAttribute(part[0], part[1], newattributevalue);
	}

	public void updateContact()
	{
		for (Entry<String, mcAttribute> arec : attributes.entrySet())
		{

			mcAttribute imatt = arec.getValue();
			imatt.setID(this.getID());
			if (!imatt.getRoot().equalsIgnoreCase("prodid/")
					&& !imatt.getRoot().equalsIgnoreCase("version/"))
				imatt.dbupsertAttribute();
		}
	}

	public void updateContactTID(String text)
	{
		this.TID = text;
		updateTID(text);
		System.out.println(" updating " + this.TID);
	}

	public void updateContactKIND(String newtype)
	{
		PreparedStatement st;
		String query = "update attributeValues set 'value' = ? where cid= ? and root='kind'  and qualifier= '' ";
		System.out.println(" updating " + newtype + " " + getCID() + query);
		try
		{
			getConnection();
			st = con.prepareStatement(query);
			st.setString(1, newtype);
			st.setInt(2, getCID());
			st.executeUpdate();
			st.close();
            disconnect();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public boolean updateFromImport(mcImportContact impcontact)
	{
		String impupdate = impcontact.getimpupdate();
		// System.out.println( " impcontact update :"+impupdate );
		// System.out.println( " current contact update :"+ update );
		boolean isupdated = false;
		for (Entry<String, mcImportAttribute> arec : impcontact.getAttributes()
				.entrySet())
		{
			mcImportAttribute imatt = arec.getValue();
			String aroot = imatt.getRoot();
			String aqual = imatt.getQualifier();
			String value = imatt.getValue().trim();
		
			// System.out.println( " import attribute " +aroot+"/"+aqual);
			mcAttributes foundatts = attributes.findAllbyRoot(aroot);
			
			if (foundatts == null || foundatts.size()==0)
			{
				createAttribute(aroot, aqual, value, impupdate);
				isupdated = true;
				System.out.println(" create new attribute a " + aroot + "/ "
						+ aqual + "=" + value);
			} else
			{
				
				if (foundatts.matchesVcardValue(value))
				{
					
				} else
				{
					System.out.println(
							" values in " + getTID()+ " dont match in attributes  " + aroot);
					mcAttribute foundatt = attributes.find(aroot, aqual);
					if (foundatt != null)
					{
						String oldupdatedt = foundatt.getUpdate();
						if (oldupdatedt == null
								|| impupdate.compareTo(oldupdatedt) > 0)
						{
							foundatt.setValue(value);
							foundatt.setUpdate(impupdate);
							System.out.println(
									" newest value used " + value + " dt "
											+ oldupdatedt + " iu " + impupdate);
							isupdated = true;
						}
						else
						{
							System.out.println(
									" unchged  value " + value + " dt "
											+ oldupdatedt + " iu " + impupdate);
						}
					} else
					{
						createAttribute(aroot, aqual, value, impupdate);
						isupdated = true;
						System.out.println(" create new attribute b " + aroot
								+ "/ " + aqual + "=" + value);
					}
				}
			}
		}
		return isupdated;
	}

	public void updateTID(String newtid)
	{

		PreparedStatement st;
		String query = "update attributeValues set 'value' = ? where cid= ? and root='tid'  and qualifier= '' ";

		try
		{
			getConnection();
			st = con.prepareStatement(query);
			st.setString(1, newtid);
			st.setInt(2, getCID());
			st.executeUpdate();
			st.close();
			disconnect();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void setTags(String tags)
	{
		this.tags = tags;
	}

	public  Vector<mcCorrespondance> getCorrespondance()
	{
		Vector<mcCorrespondance> letterlist= new Vector<mcCorrespondance>();
		ArrayList<Map<String, String>> rowlist = doQuery(
				"select * from correspondance where cid ="+getCID()+" order by date");
		//System.out.println("letters found"+rowlist.size());
		for (Map<String, String> row : rowlist)
		{
			mcCorrespondance aletter = new mcCorrespondance(0);
			aletter.fill(row);

			letterlist.add(aletter);
		}
		return letterlist;
	}

	public void addCorrespondance(String name, String date,String status, File dest)
	{
			PreparedStatement st;
			String query = "insert into correspondance(cid, date,path,status, subject )  values(?, ?,?,?,?)";
//System.out.println(" adding "+name+" "+dest.toString());
			try
			{
				getConnection();
				st = con.prepareStatement(query);
				st.setInt(1, getCID());
				st.setString(2, date);
				st.setString(3, dest.getPath());
				st.setString(4, status);
				st.setString(5, name);
				st.execute();
				st.close();
			
				disconnect();
			

			} catch (SQLException e)
			{
				e.printStackTrace();
			}
			
		}

	public void deleteTag(String tag)
	{
		
			String query = "update attributeValues set value = replace(value, ?, '')  where CID = ? and root LIKE 'tags%' ";
			PreparedStatement st;
			try
			{
				System.out.println("query =" +query+" "+ tag + " "+getCID());
				getConnection();
				st = con.prepareStatement(query);
				st.setString(1, "#" + tag + ";");
				st.setInt(2, getCID());
				st.executeUpdate();
				st.close();
				disconnect();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}

	}

	public void importVcard(VCard vcard)
	{
		System.out.println(" loading vcard ");
		if(vcard.getEmails().size()>0)
		{
			mcAttribute att =createAttribute("email", "vcard");
			att.setValue(vcard.getEmails().get(0).toString());
		}
		if(vcard.getStructuredName() != null)
		{
			StructuredName st =vcard.getStructuredName();
		
			String value = "{";
			if (st.getFamily()!=null) value += "Sn:"+st.getFamily()+";";
			if (st.getGiven()!=null) value += "Fn:"+st.getGiven()+";";
			value += "}";
			mcAttribute att =createAttribute("name", "vcard");
			att.setValue(value);
		}
		if(vcard.getAddresses() != null)
		{
			 ezvcard.property.Address ad = vcard.getAddresses().get(0);
			String value = "{";
			if (ad.getStreetAddress()!=null) value += "street:"+ad.getStreetAddress()+";";
			if (ad.getLocality()!=null) value += "city:"+ad.getLocality()+";";
			if (ad.getPostalCode()!=null) value += "postcode:"+ad.getPostalCode()+";";
			if (ad.getRegion()!=null) value += "county:"+ad.getRegion()+";";
			if (ad.getCountry()!=null) value += "country:"+ad.getCountry()+";";
			value += "}";
			mcAttribute att =createAttribute("address", "vcard");
			att.setValue(value);
			//selcontact.addAttribute()
		}
		updateContact();
	}

	public int getCID()
	{
		return CID;
	}

	public void setCID(int cID)
	{
		CID = cID;
	}

	public void addGroup(String selected)
	{
		mcContacts selcons = mcdb.selbox.searchTag(selected);
		for (Entry<String, mcContact> anentry : selcons.entrySet())
		{
			mcContact acontact = anentry .getValue();
			mcAttribute newatt = this.createAttribute("member", acontact.getIDstr(),
					acontact.getTID());
			newatt.dbupsertAttribute();
		}
		
	}

	public mcContacts getMembers(String selector)
	{
        mcContacts list = new mcContacts();
	
			Map<String, mcAttribute> attributes = this
					.getAttributesbyRoot(selector);
			if (attributes.size() < 1)
			{ return null; }


			for (Entry<String, mcAttribute> anentry : attributes.entrySet())
			{


				mcAttribute anattribute = anentry.getValue();
				String value = anattribute.getValue();
				
				mcContact linkedcontact = mcdb.selbox.FindbyTID(value);

				if (linkedcontact != null)
				{
                   list.put(linkedcontact);
                   
				}
			}		
		return list;
	}

	
		
	}



	

	

