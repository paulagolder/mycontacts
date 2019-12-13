package org.lerot.mycontact.gui.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.lerot.mycontact.mcContact;
import org.lerot.mycontact.mcUtilities;
import org.lerot.mycontact.mcdb;

public class jswDropPane extends JPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DropTarget dropTarget;
	private DropTargetHandler dropTargetHandler;
	private Point dragPoint;

	private boolean dragOver = false;
	private BufferedImage target;

	private JLabel message;

	public jswDropPane()
	{
		try
		{
			target = ImageIO
					.read(new File("/home/paul/.mccontacts/mccontacts.png"));
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}

		setLayout(new GridBagLayout());
		message = new JLabel("Drop correspondance here");
		message.setFont(message.getFont().deriveFont(Font.BOLD, 24));
		add(message);

	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(400, 400);
	}

	protected DropTarget getMyDropTarget()
	{
		if (dropTarget == null)
		{
			dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE,
					null);
		}
		return dropTarget;
	}

	protected DropTargetHandler getDropTargetHandler()
	{
		if (dropTargetHandler == null)
		{
			dropTargetHandler = new DropTargetHandler();
		}
		return dropTargetHandler;
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		try
		{
			getMyDropTarget().addDropTargetListener(getDropTargetHandler());
		} catch (TooManyListenersException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void removeNotify()
	{
		super.removeNotify();
		getMyDropTarget().removeDropTargetListener(getDropTargetHandler());
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (dragOver)
		{
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setColor(new Color(0, 255, 0, 64));
			g2d.fill(new Rectangle(getWidth(), getHeight()));
			if (dragPoint != null && target != null)
			{
				int x = dragPoint.x - 12;
				int y = dragPoint.y - 12;
				g2d.drawImage(target, x, y, this);
			}
			g2d.dispose();
		}
	}

	protected void importFiles(final ArrayList<File> files)
	{
		Runnable run = new Runnable()
		{
			@Override
			public void run()
			{
				mcContact selcontact = mcdb.selbox.getSelcontact();

				message.setText("You dropped " + files.size() + " files");
				// List objects =
				// (List)transfer.getTransferData(DataFlavor.javaFileListFlavor);
				for (Object object : files)
				{
					File directory = new File(mcdb.docsfolder + File.separator
							+ selcontact.getID());
					if (!directory.exists())
					{
						directory.mkdir();
					}
					if (object instanceof File)
					{
						File source = (File) object;
						SimpleDateFormat sdf = new SimpleDateFormat(
								"YYYY-MM-dd");
						String date = sdf.format(source.lastModified());
						File dest = new File(mcdb.docsfolder + File.separator
								+ selcontact.getID() + File.separator
								+ source.getName());

						try
						{
							Files.copy(Paths.get(source.getAbsolutePath()),
									Paths.get(dest.getAbsolutePath()),
									StandardCopyOption.REPLACE_EXISTING);
							String ext = mcUtilities.getFileExtension(source);
							if ("eml".contentEquals(ext))
							{
								Properties props = System.getProperties();
								props.put("mail.host", "smtp.dummydomain.com");
								props.put("mail.transport.protocol", "smtp");
								Session mailSession = Session
										.getDefaultInstance(props, null);
								InputStream isource = new FileInputStream(
										source.getAbsolutePath());
								MimeMessage message = new MimeMessage(
										mailSession, isource);
								date = sdf.format(message.getSentDate());
							}
							selcontact.addCorrespondance(source.getName(), date,
									dest);

							// System.out.println("File copied from line 153
							// "+source.getAbsolutePath()+" to
							// "+dest.getAbsolutePath());

						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (MessagingException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
				mcdb.topgui.refreshView();
			}

		};
		SwingUtilities.invokeLater(run);
	};

	protected class DropTargetHandler implements DropTargetListener
	{

		protected void processDrag(DropTargetDragEvent dtde)
		{
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				dtde.acceptDrag(DnDConstants.ACTION_COPY);
			} else
			{
				dtde.rejectDrag();
			}
		}

		@Override
		public void dragEnter(DropTargetDragEvent dtde)
		{
			processDrag(dtde);
			SwingUtilities
					.invokeLater(new DragUpdate(true, dtde.getLocation()));
			repaint();
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde)
		{
			processDrag(dtde);
			SwingUtilities
					.invokeLater(new DragUpdate(true, dtde.getLocation()));
			repaint();
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde)
		{
		}

		@Override
		public void dragExit(DropTargetEvent dte)
		{
			SwingUtilities.invokeLater(new DragUpdate(false, null));
			repaint();
		}

		@Override
		public void drop(DropTargetDropEvent dtde)
		{

			SwingUtilities.invokeLater(new DragUpdate(false, null));

			Transferable transferable = dtde.getTransferable();
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				dtde.acceptDrop(dtde.getDropAction());
				try
				{
					ArrayList transferdata1 = (ArrayList) transferable
							.getTransferData(DataFlavor.javaFileListFlavor);
					if (transferdata1 != null && transferdata1.size() > 0)
					{
						importFiles(transferdata1);
						dtde.dropComplete(true);
					}

				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			} else
			{
				dtde.rejectDrop();
			}
		}
	}

	public class DragUpdate implements Runnable
	{

		private boolean dragOver;
		private Point dragPoint;

		public DragUpdate(boolean dragOver, Point dragPoint)
		{
			this.dragOver = dragOver;
			this.dragPoint = dragPoint;
		}

		@Override
		public void run()
		{
			jswDropPane.this.dragOver = dragOver;
			jswDropPane.this.dragPoint = dragPoint;
			jswDropPane.this.repaint();
		}
	}
}
