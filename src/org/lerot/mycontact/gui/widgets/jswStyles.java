package org.lerot.mycontact.gui.widgets;

import java.util.HashMap;
import java.util.Map;

public class jswStyles
{

	public Map<String, jswStyle> stylelist = new HashMap<String, jswStyle>();

	public jswStyles()
	{

	}

	public jswStyle getStyle(String name)
	{
		jswStyle astyle = null;
		if (stylelist.containsKey(name))
		{
			astyle = stylelist.get(name);
		}
		return astyle;
	}

	public jswStyle makeStyle(String name)
	{
		jswStyle newstyle = new jswStyle();
		stylelist.put(name, newstyle);
		return newstyle;
	}

}
