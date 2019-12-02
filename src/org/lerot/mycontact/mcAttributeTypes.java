package org.lerot.mycontact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

public class mcAttributeTypes extends mcDataObject
{

	public mcAttributeTypes()
	{
		super();
		
	}

	public Vector<String> getallAttributes()
	{
		ArrayList<Map<String, String>> rowlist = mcdb.topgui.attributetypes.doQuery("select DISTINCT(attributeKey) from attribute order by attributeLabel ");
		Vector<String> typelist = new Vector<String>();
		for (Map<String, String> row : rowlist)
		{
			if (row.containsKey("attributeKey"))
				typelist.add(row.get("attributeKey"));
		}
		return typelist;
	}

	

	HashMap<String, mcAttributeType> attributetypelist;

	public boolean containsKey(String key)
	{
		return attributetypelist.containsKey(key);
	}

	public mcAttributeType findType(String atttype)
	{
		if (attributetypelist.containsKey(atttype)) return attributetypelist
				.get(atttype);
		else
			return null;
	}

	public HashMap<String, mcAttributeType> getAttributeTypes()
	{
		return attributetypelist;
	}

	public void loadAttributeTypes()
	{
		ArrayList<Map<String, String>> rowlist = doQuery("select * from attribute order by displayOrder ");
		attributetypelist = new LinkedHashMap<String, mcAttributeType>();
		for (Map<String, String> row : rowlist)
		{
			mcAttributeType alabtype = new mcAttributeType();
			alabtype.load(row);
			alabtype.setDataType();
			String akey = alabtype.getKey();
			attributetypelist.put(akey, alabtype);
		}
		System.out.println( " loaded attributetypes "+attributetypelist.size());
	}
	
	

	public Set<Entry<String, mcAttributeType>> entrySet()
	{
		return attributetypelist.entrySet();
	}

}
