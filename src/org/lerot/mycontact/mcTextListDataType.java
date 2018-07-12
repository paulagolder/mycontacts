package org.lerot.mycontact;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.lang3.StringEscapeUtils;

public class mcTextListDataType extends mcDataType
{

	public static String deleteTags(String att, Set<String> ataglist)
	{
		SortedSet<String> oldtags = getTags(att);
		for (String atag : ataglist)
		{
			oldtags.remove(atag);
		}
		return makeString(oldtags);
	}

	public static Set<String> getTags(mcAttribute tatt)
	{
		return getTags(tatt.getValue());
	}

	static SortedSet<String> getTags(String tags)
	{
		SortedSet<String> tokens = new TreeSet<String>();
		char sep =';';
		if(!tags.contains(";"))  sep=',';
         Vector<String> tokenarray = parseTagString(tags,sep);
		for (String token : tokenarray)
		{
			token = token.trim();
			if (!token.isEmpty() && !token.equals(";") && !token.equals("null")
					&& !tokens.contains(token))
			{
				tokens.add(token);
			}
		}
		return tokens;
	}

	public static String insertTags(String att, Set<String> ataglist)
	{
		Set<String> oldtags = getTags(att);
		for (String atag : ataglist)
		{
			if(atag.contains(";")) atag.replace(";",":");
			oldtags.add(atag);
		}
		return makeString(oldtags);
	}

	private static String makeSimpleString(Set<String> taglist)
	{
		String outtags = "";
		for (String atag : taglist)
		{
			if (!atag.isEmpty() && !atag.equalsIgnoreCase("null"))
			{
				outtags = outtags + atag + ", ";
			}
		}
		return outtags;
	}

	private static String makeString(Set<String> taglist)
	{
		String outtags = "";
		for (String atag : taglist)
		{
			outtags = outtags + atag + "; ";
		}
		return outtags;
	}

	

	public mcTextListDataType()
	{
		super("textlist", "textlist");
	}

	public int compareTo(String aarray, String barray)
	{
		SortedSet<String> aset = getTags(aarray); 
		SortedSet<String> bset = getTags(barray);
		for (final Iterator<String> it = bset.iterator(); it.hasNext();)
		{
			String btag = it.next();
			if (!aset.contains(btag)) { return 1; }
		}
		for (final Iterator<String> it = aset.iterator(); it.hasNext();)
		{
			String atag = it.next();
			if (!bset.contains(atag)) { return -1; }
		}
		return 0;
	}

	@Override
	public Vector<String> getFieldKeyList()
	{
		return null;
	}

	@Override
	public LinkedHashMap<String, mcfield> getFieldList()
	{
		return null;
	}

	@Override
	public Map<mcfield, String> getFieldValueMap(String value)
	{
		return null;
	}

	public String getFormattedValue(String value, String fmt)
	{
		Set<String> str = getTags(value);
		return makeString(str);
	}

	@Override
	public Map<String, String> getKeyValueMap(String value)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getTextListMap(String value)
	{
		Map<String, String> sortedmap = new LinkedHashMap<String, String>();
		SortedSet<String> tags = getTags(value);
		int k = 0;
		for (String atag : tags)
		{
			String akey = new Integer(k).toString();
			sortedmap.put(akey, atag);
		}
		return sortedmap;
	}

	@Override
	public String toVcardValue(String value)
	{
		SortedSet<String> taglist = getTags(value);
		String outlist = "";
		for(String tag:taglist)
		{
			//if(tag.contains(";")) tag.replace(";",":");// should not be necessary if data properly formated paul fix
			outlist += tag+";";
		}
		return outlist;
	}

	@Override
	public boolean matchesVcardValue(String avalue, String bvalue)
	{
		if (compareTo(bvalue, avalue) == 0)
			return true;
		else
			return false;
	}

	@Override
	public String toXML(String value)
	{
		SortedSet<String> taglist = getTags(value);
		String outxml = "<value>";
		String sep="";
		for (String atag : taglist)
		{
			String outtag = StringEscapeUtils.escapeXml(atag);
			if(outtag.contains(",")) outtag = "\""+outtag+"\"";
			outxml +=  sep+outtag;
			sep = ",";
		}
		outxml += "</value>";
		return outxml;
	}

	public boolean valueContained(String testvalue, String attvalue)
	{
		// attvalue = attvalue;
		SortedSet<String> taglist = getTags(attvalue);
		;

		if (taglist.contains(testvalue)) { return true; }
		return false;
	}
	
	public static Vector<String> parseTagString(String csv_string, char delimiter)
	{ 
		Vector<String> outarray = new Vector<String>();
		int l = csv_string.length();
		int i = 0;
		String token = "";
		int count = 0;
		boolean instring = false;
		char endquote = '"';
		while (i < l)
		{
			char c = csv_string.charAt(i);
			if (instring)
			{
				if (c == endquote)
					instring = false;
				else
					token = token + c;
			} else
			{
				if (c == delimiter)
				{
					outarray.add(count, token);
					token = "";
					count = count + 1;

				} else
				{
					if (c == '"')
					{
						instring = true;
						endquote = c;
					} else
						token = token + c;
				}
			}
			i++;
		}
		if (token != "")
		{
			outarray.add(count, token);
			token = "";
			count = count + 1;
		}
		return outarray;
	}

}
