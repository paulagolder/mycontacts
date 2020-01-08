package org.lerot.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;

public class jswPopUp
{
	public class ConfirmDialog extends Dialog
	{

		private static final long serialVersionUID = 1L;
		Button affirm, deny;

		public ConfirmDialog(Frame parent, String what, String Addyes,
				String Addno)
		{
			super(parent, "Confirmation Request", true);

			this.setLayout(new BorderLayout(10, 10));
			this.add("North", new Label(what, Label.CENTER));
			this.add("Center", new Label("Are you SURE?", Label.CENTER));

			Panel selector = new Panel();
			selector.setLayout(new GridLayout(1, 2));
			this.add("South", selector);

			affirm = new Button("Yes - " + Addyes);
			affirm.setActionCommand("yes");
			selector.add(affirm);

			deny = new Button("No - " + Addno);
			deny.setActionCommand("no");
			selector.add(deny);

			this.pack();

		}

		@Override
		public boolean action(Event e, Object arg)
		{
			if (e.target == affirm) System.exit(0);
			this.hide();
			this.dispose();
			return true;
		}

	}
}
