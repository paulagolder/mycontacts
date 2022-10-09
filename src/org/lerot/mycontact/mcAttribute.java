package org.lerot.mycontact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JComponent;

import org.lerot.mywidgets.jswImage;
import org.lerot.mywidgets.jswLabel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class mcAttribute extends mcDataObject
{

	public static mcAttribute attributefromXML(Node anode)
	{
		// Element element = (Element) nodes.item(i);
		Element at = (Element) anode;
		//String  = at.getNodeName();
		String attroot = at.getAttribute("key");
		String qualifier = at.getAttribute("qualifier");
		String typename = at.getAttribute("type");	 
		String update = at.getAttribute("updated");
		/*try {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		    Date parsedDate = dateFormat.parse( updatetext);
		    update = new java.sql.Timestamp(parsedDate.getTime());
		} catch(Exception e) { 
			update=null;
		}*/
		//String update = at.getAttribute("updated");
		mcAttribute newatt = new mcAttribute(attroot, qualifier, typename,update, anode);
		return newatt;
	}

	public void dbDeleteAttribute(Integer acid, String aroot,
			String aqual)
	{
		PreparedStatement st;
		String query = "delete from attributeValues where cid= ?  && root = ? && qualifier = ? ";
		try
		{
			getConnection();
			st = con.prepareStatement(query);
			st.setInt(1, acid);
			st.setString(2, aroot);
			st.setString(3, aqual);
			st.executeUpdate();
			st.close();
			disconnect();

		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		System.out.println(" attribute deleted ");
	}

	public mcAttributeType attributetype = null;
	mcAttributeValue attributevalue = null;
	private Integer cid = null;
	private String displaygroup = "";
	int displayOrder = 0;
	private String qualifier = null;
	private String root = null;
	private String update = null;

	public mcAttribute(int nid)
	{
		super();
		cid = nid;
	}

	public mcAttribute(int k, String string)
	{
		super();
		new mcAttribute(k, root, "");
	}

	public mcAttribute(int nid, String akey, String attqual)
	{
		super();
		cid = nid;
		root = akey;
		qualifier = attqual;
		setType();
		setValue("");
	}

	public mcAttribute(int nid, String attroot, String attqual,
			String attupdate)
	{
		super();
		cid = nid;
		root = attroot;
		qualifier = attqual;
		update = attupdate;
		setType();
		setValue("");
	}


	public mcAttribute(String attroot, String aqualifier, String typename,String updated,Node anode)
	{
		super();
		cid = -1;
		root = attroot;
		qualifier = aqualifier;
		update = updated;
		attributetype = mcAttributeTypes.findType(root);
		//attributetype = mcAttributeTypes.findType(typename);
		//mcDataType datatype = attributetype.dt;
		this.loadXML(anode);
	}

	public boolean containsValue(String testvalue)
	{
		if (attributevalue.isNull()) return false;
		String value = attributevalue.stringvalue;
		mcDataType type = getType();
		return type.valueContained(testvalue, value);
	}

	public void dbdeleteAttribute()
	{
		PreparedStatement st;
		String query = "delete from attributeValues where cid= ?  and root = ? and qualifier = ? ";
		try
		{
			getConnection();
			st = con.prepareStatement(query);
			st.setInt(1, cid);
			st.setString(2, getRoot());
			st.setString(3, getQualifier());
			st.executeUpdate();
			st.close();
			disconnect();

		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		System.out.println(" attribute deleted ");

	}

	public void dbinsertAttribute()
	{
		PreparedStatement st;
		String query = "insert into attributeValues(cid,root, qualifier,value, update_dt)  values( ?, ?,?, ?, ?)";
		try
		{
			getConnection();
			st = con.prepareStatement(query);
			st.setInt(1, cid);
			st.setString(2, getRoot());
			st.setString(3, getQualifier());
			st.setString(4, getValue());
			st.setString(5, getDateTime());
			st.executeUpdate();
			st.close();
			disconnect();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public Vector<String> dbloadAttributeKeyList()
	{
		ArrayList<Map<String, String>> rowlist = doQuery(
				"select * from attribute order by displayOrder ");
		Vector<String> attributelist = new Vector<String>();
		for (Map<String, String> row : rowlist)
		{
			mcAttribute alabtype = new mcAttribute(0);
			String attkey = row.get("attributeKey");

			attributelist.add(attkey);
		}
		return attributelist;
	}

	public void dbupdateAttribute()
	{
		//(new mcDataObject()).setConnection(mcdb.topgui.currentcon);
		PreparedStatement st;
		String query = "update attributeValues set 'value' = ? , 'update_dt'= ? where cid= ? and root=?  and qualifier= ? ";
		try
		{
			getConnection();
			st = con.prepareStatement(query);
			st.setString(1, getValue());
			st.setString(2, getDateTime());
			st.setInt(3, cid);
			st.setString(4, getRoot());
			st.setString(5, getQualifier());
			st.executeUpdate();
			st.close();
			disconnect();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	
	}

	public void dbupsertAttribute()
	{
		try
		{
			PreparedStatement st;
			String query = " UPDATE attributeValues "
					+ " SET   value = ? , update_dt = ? "
					+ " WHERE   cid = ?  and root = ? and qualifier = ?  ";
			getConnection();
			st = con.prepareStatement(query);
			st.setString(1, getValue());
			String update = getUpdate();
			if (update == null || update.isEmpty())
			{
				update = getDateTime();
			}
			st.setString(2, update);
			st.setInt(3, cid);
			st.setString(4, getRoot());
			st.setString(5, getQualifier());
			st.executeUpdate();	
			int rescount = st.getUpdateCount();
			st.close();
			disconnect();
			if (rescount == 0)
			{
				dbinsertAttribute();
			}
	
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void deleteTags(Set<String> ataglist)
	{
		String newtagvalues = mcTagListDataType.deleteTags(this.getValue(),
				ataglist);
		setValue(newtagvalues);
		// updateAttributeValue();
	}

	public Integer getCid()
	{
		return cid;
	}

	public mcDataType getDataType()
	{
		return attributetype.getDatatype();
	}

	public boolean isDisplaygroup(String dg)
	{
		if(displaygroup.contains(dg)) return true;
		else return false;
	}
	
	public String getDisplaygroup()
	{
		return displaygroup;
	}

	public Vector<String> getFieldKeyList()
	{
		if (attributetype == null) return null;
		return attributetype.getFieldKeyList();
	}

	public LinkedHashMap<String, mcfield> getFieldList()
	{
		return attributetype.getFieldList();
	}

	public Map<mcfield, String> getFieldValueMap()
	{
		return attributetype.getFieldValueMap(attributevalue.getValue());
	}

	public String getFormattedValue()
	{
		return getFormattedValue(null);
	}

	public String getFormattedValue(String fmt)
	{
		if (attributevalue == null) return " *empty* ";
		return attributetype.getFormattedValue(attributevalue, fmt);
	}

	public JComponent getImage(int targetheight)
	{
		jswLabel img = null;

		img = new jswImage(getValue()).DisplayImage();

		return img;
	}

	public String getKey()
	{
		String attkey = root;
		if (qualifier != null && !qualifier.isEmpty())
			attkey += "/" + qualifier;
		return attkey;
	}

	public Map<String, String> getMap()
	{
		if (attributevalue == null) return null;

		return attributetype.getKeyValueMap(attributevalue.getValue());

	}

	public int getOrder()
	{

		return displayOrder;

	}

	public String getQualifier()
	{
		return qualifier;
	}

	public String getRoot()
	{
		return root;
	}

	public Set<String> getTags()
	{

		return attributetype.getTags(this.getValue());
	}

	public mcDataType getType()
	{
		 if(attributetype == null) return new mcTextDataType();
		return attributetype.getDatatype();
	}
	
	public String getDataTypeKey()
	{
		return attributetype.getDatatype().getTypekey();
	}


	String getUpdate()
	{
		return update;
	}

	public String getValue()
	{
		return attributevalue.getValue();
	}

	public String getVcardValue()
	{
		if (attributevalue == null) return "";
		String value = attributetype.getVcardValue(attributevalue);
		if (value == null)
			return "";
		else
			return value.trim();
	}

	public String getVcardValue(String attvalue)
	{
		if (attvalue == null) return "";
		return attributetype.dt.toVcardValue(attvalue);
	}

	public void insertValues(Set<String> ataglist)
	{
		String newtagvalues = mcTagListDataType.insertTags(this.getValue(),
				ataglist);
		setValue(newtagvalues);
	}

	public boolean isArray()
	{
		return attributetype.isArray();
	}

	public boolean isEmpty()
	{
		String val = getValue();
		if (val == null) return true;
		return (val.isEmpty());
	}

	public boolean isImage()
	{
		if (attributetype != null)
			return attributetype.isImage();
		else
			return false;
	}

	public boolean isType(String string)
	{
		return attributetype.isType(string);
	}

	protected void load(ResultSet inmap)
	{
		try
		{
			String strvalue = "*";
			cid = inmap.getInt("cid");
			strvalue = inmap.getString("value");
			if (strvalue == null || strvalue.isEmpty())
				setValue("null");
			else
				setValue(strvalue.trim());
			
			
			try
			{
				setUpdate(inmap.getString("update_dt"));

			} catch (Exception e)
			{
				System.out.println(" not worked " + getUpdate() + " " + e);
			}

		} catch (SQLException e)
		{

		}
	}

	public void loadXML(Node anode)
	{
		Element at = (Element) anode;		
		if (attributetype == null )
		{
			 System.out.println(" problem with type in xml import :" );
		}
		
		if (attributetype != null && attributetype.dt.isArrayType())
		{
			NodeList nl = at.getElementsByTagName("field");
			if (nl != null && nl.getLength() > 0)
			{
				String outline = " { ";
				for (int i = 0; i < nl.getLength(); i++)
				{
					Element fld = (Element) nl.item(i);
					String key = fld.getAttribute("key");
					String value = fld.getAttribute("value");
					if (value.contains("'"))
					{
						outline = outline + key + ":\"" + value + "\" , ";
					} else
						outline = outline + key + ":\'" + value + "\' , ";
				}
				outline += " }";
				setValue(outline);
			}
		} else
		{
			String value = at.getAttribute("value");
			setValue(value);
		}
	}

	public boolean matches(mcAttribute existingatt)
	{
		if (existingatt == null) return false;
		if (!getKey().equalsIgnoreCase(existingatt.getKey())) return false;
		if (!getType().equals(existingatt.getType())) return false;
		//String avalue =  existingatt.getValue()
		String bvalue = existingatt.getValue();
		return matchesValue(bvalue);
		// return true;
	}

	public boolean matchesValue(String testValue)
	{
		String avalue = getValue();

		if (testValue == null && avalue == null) return true;
		if (testValue != null && avalue == null) return false;
		if (testValue == null && avalue != null) return false;

		boolean matches = attributetype.dt.matchesVcardValue(avalue, testValue);
		if (!matches)
		{
			// System.out.println(" error matching attributes "+this.toString()+
			// " for " + avalue + " with " + testValue);
			return false;
		}
		return true;
	}

	public boolean matchesVcardValue(String testValue)
	{
		String avalue = getValue();

		if (testValue == null && avalue == null) return true;
		if (testValue != null && avalue == null) return false;
		if (testValue == null && avalue != null) return false;

		boolean matches = attributetype.dt.matchesVcardValue(avalue, testValue);
		if (!matches)
		{
			// System.out.println(" error matching attributes "+this.toString()+
			// " for " + avalue + " with " + testValue);
			return false;
		}
		return true;
	}

	private void parseKey(String akey)
	{
		String[] parts = mcUtilities.parseLabel(akey);
		root = parts[0];
		if (root == null || root.isEmpty())
			System.out.println(" problem with 3 " + akey + " in " + this);

		qualifier = parts[1];
	}

	private boolean qualifierEquals(String testvalue)
	{
		String qual = getQualifier();
		String ntestvalue = mcUtilities.normalisename(testvalue);
		if (mcUtilities.containedBy(ntestvalue, qual))
			return true;
		else if (mcUtilities.containedBy(qual, ntestvalue))
			return true;
		else
			return false;
	}

	void setCid(Integer cid)
	{
		this.cid = cid;
	}

	void setDisplaygroup(String displaygroup)
	{
		this.displaygroup = displaygroup;
	}

	public void setID(int newid)
	{
		cid = newid;

	}

	void setKey(String atttkey)
	{
		parseKey(atttkey);
	}

	public void setQualifier(String qualifier)
	{
		this.qualifier = qualifier;
	}

	private void setType()
	{
		String attkey = getKey();

		attributetype = mcAttributeTypes.findType(attkey);
		if (attributetype == null)
			attributetype = mcAttributeTypes.findType(root);
		if (attributetype == null)
		{
			System.out.println(
					" not found type in mcattribute " + getKey() + "?" + root);
			attributetype = mcAttributeTypes.findType("default");
		}
		displayOrder = attributetype.getOrder();
		displaygroup = attributetype.getDisplaygroup();
	}

	public void setUpdate()
	{

		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(
				now.getTime());
		setUpdate(currentTimestamp.toString());

	}

	void setUpdate(String update)
	{
		this.update = update;
	}

	public void setValue(Map<String, String> valuelist)
	{
		String arrayvaluestring = attributetype.arrayToArrayString(valuelist);
		attributevalue = new mcAttributeValue(this, arrayvaluestring);

	}
	
	public void setValue(Set<String> tokenlist)
	{
			String tokenstring = attributetype.arrayToString(tokenlist);
			attributevalue = new mcAttributeValue(this, tokenstring);
	}

	public void setValue(String strvalue)
	{
		String newvalue = strvalue.trim();
		if (!newvalue.isEmpty())
		{
			mcDataType attype = getType();
			if (attype.isType("phone") || attype.isType("cellphone"))
			{
				newvalue = mcUtilities.normalisephonenumber(newvalue);
			} else if (attype.isType("address"))
			{
				// fix this
				if (!mcUtilities.isArray(newvalue))
					newvalue = mcUtilities.tidyValue(newvalue);

			} else if (attype.isType("name"))
			{
				if (!mcUtilities.isArray(newvalue))
					newvalue = attype.tidyValue(newvalue);
			} else if (attype.isType("text"))
			{
				newvalue = newvalue.trim();
			} else if (attype.isType("image"))
			{
				;
			} else if (attype.isType("textlist"))
			{
				newvalue = newvalue.trim();
			}else if (attype.isType("taglist"))
			{
				newvalue = newvalue.trim();
			} else if (attype.isType("cumulativetext"))
			{
				System.out.println(" updating cumtextlist for " + this);
				String oldvalue = getValue();
				newvalue = oldvalue + ";"
						+ mcUtilities.formatCumulativeText(newvalue);
			}
		}

		attributevalue = new mcAttributeValue(this, newvalue);
	}
	
	public void addTag(String strvalue)
	{
		String newvalue = strvalue.trim();
		if (!newvalue.isEmpty())
		{
			mcDataType attype = getType();
			 if (attype.isType("taglist"))
			{
                 Set<String> oldtags = getTags();
                 if(oldtags == null)
                 {
                	 
                 }
                 oldtags.add(newvalue);
                 setValue(oldtags);
			}
			 else if (attype.isType("textlist"))
				{
	                 Set<String> oldtags = getTags();
	                 if(oldtags == null)
	                 {
	                	 oldtags = new TreeSet<String> ();
	                 }
	                 oldtags.add(newvalue);
	                 setValue(oldtags); 
				}else if (attype.isType("cumulativetext"))
			{
				System.out.println(" updating cumtextlist for " + this);
				String oldvalue = getValue();
				newvalue = oldvalue + ";"
						+ mcUtilities.formatCumulativeText(newvalue);
				attributevalue = new mcAttributeValue(this, newvalue);
			}
			else
			{
				setValue(strvalue);
			}
		}

		
	}
	
	

	public void setValue(Vector<String> valuelist)
	{
		String arrayvaluestring = attributetype.arrayToString(valuelist);
		attributevalue = new mcAttributeValue(this, arrayvaluestring);
	}

	@Override
	public String toString()
	{
		return getKey() + "(" + attributetype.getAttributeLabel() + ")" + ":"
				+ attributevalue.stringvalue + "  ";
	}

	public String toXCard()
	{
		if (attributevalue == null) return "";
		String outxml = "";

		outxml = " <" + getRoot() + " >";
		String parameters = "";
		if (getQualifier() != null)
		{
			parameters += "<type><text>" + getQualifier() + "</text></type>\n";
		}
		parameters += "<update><date>" + getUpdate() + "</date></update>\n";
		outxml += "<parameters>" + parameters + "</parameters >\n";

		outxml += attributetype.getXMLValue(attributevalue) + "\n";

		outxml += " </" + getRoot() + ">" + "\n";

		return outxml;
	}

	public String toXML()
	{
		if (attributevalue == null) return "";
		String outxml = "";
		outxml = " <" + getRoot() + "  ";
		if (getQualifier() != null && !getQualifier().isEmpty())
		{
			outxml += " qualifier=\'" + getQualifier() + "\' ";
		}
		outxml += " datatype=\'" + getDataTypeKey() + "\' ";
		
		if (getUpdate() != null && !getUpdate().isEmpty())
		{
			outxml += " update=\'" + getUpdate() + "\' ";
		}
		outxml += " > \n";
		outxml += attributetype.getXMLValue(attributevalue) + "\n";// paul to fix one extra cr
		outxml += " </" + getRoot() + ">" + "\n";
		return outxml;
	}

	public void updateAttribute(mcAttribute aniattribute)
	{
		String value = aniattribute.getValue();
		String update = aniattribute.getUpdate();
		setValue(value);
		setUpdate(update);
	}

	public void updateQualifier(String newqualifier)
	{
		String rt = getRoot();
		String oq = getQualifier();
		String nk = rt + "/" + newqualifier;
		if (!nk.equals(getKey()))
		{
			try
			{
				getConnection();
				PreparedStatement st;
				String query = " UPDATE attributeValues "
						+ " SET   qualifier= ?   "
						+ " WHERE   cid = ?  and root= ? and qualifier= ? ";
				st = con.prepareStatement(query);
				st.setString(1, newqualifier);
				st.setInt(2, cid);
				st.setString(3, rt);
				st.setString(4, oq);
				st.executeUpdate();
				st.close();
				disconnect();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		setQualifier(newqualifier);

	}

	

}
