
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import model.Record;



import com.dstjacques.jhotkeys.JHotKeys;
import com.dstjacques.jhotkeys.JHotKeyListener;
import com.melloware.jintellitype.JIntellitype;
import com.sun.jna.platform.win32.User32;


public class Main implements Runnable {
	
	//Address - translator database
	static String url = "http://pl.bab.la/slownik/angielski-polski/";
	

	// TrayIcon menu elements
	public PopupMenu popup = new PopupMenu();
	public MenuItem addItem = new MenuItem("Add");
    public MenuItem startItem = new MenuItem("Start");
    
    // Window
	public static Ask_Window askWindow = new Ask_Window();
	public static Tip_Window tipWindow = new Tip_Window();
    
	//Engine Bruuum ....Bruum ;-)
    public Timer timer = new Timer( ); 
    public static Boolean is_run = false;
    
    
	
	public static void main(String[] args) {

		final Main main = new Main();
		SwingUtilities.invokeLater(main);
		DataBase base = new DataBase();

		setHotKeys();
		
	}
	
	
	// Main GUI elements 
	

	public static void setHotKeys()
	{
		JHotKeys hotkeys = new JHotKeys("../lib");
		try {
		    hotkeys.registerHotKey(0, JIntellitype.MOD_CONTROL, (int)'Q');
		    
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
				    "Could not load JIntellitype.dll from local file system");
		}
		
	    JHotKeyListener hotkeyListener = new JHotKeyListener(){
	         public void onHotKey(int id) {
	            if(id == 0)
	            {
	            	try {
	            		Selector foo = new Selector();
	            		String select = URLEncoder.encode(Selector.go(foo), "UTF-8");
						Extractor ext = new Extractor(url+select,null);

						//extracting data 
						ext.forbidden.add(".*?<.>.*?");
						ext.define("icon-chevron-right", "fb-like-wrapper");
						ArrayList<String> elements = ext.extract("class=\"muted-link\">.*?</a");
						
						
						 tipWindow.setBackground(new Color(0, 0, 0));
				         tipWindow.setOpacity(0.8f);
				         
						tipWindow.setInfo(elements,select);
						tipWindow.setVisible(true);

						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	         }
	      };

	      hotkeys.addHotKeyListener(hotkeyListener);
	}
	
	public void setMenu()
    {
        popup.add(addItem);
        popup.add(startItem);
        
        addItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Add_Window.go();
            }
        });
        
        startItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(!DataBase.Records.isEmpty())
            	{
            	startAsk();
            	}
            	else
            	{
            		//JOptionPane.showMessageDialog(Ask_Window., "You have to add cards");
            	}
            }
        });
    }
	
	public void setTray()
	{
		try{
			Image img = ImageIO.read(getClass().getResource("img/bu16.png"));
			
		final TrayIcon trayIcon =
                new TrayIcon(img, "Simpla Application");
        final SystemTray tray = SystemTray.getSystemTray();
            tray.add(trayIcon);

        trayIcon.setPopupMenu(popup);
        
        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              System.out.println("END");
              System.exit(0);
            }
        });
        
		} catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }
		catch (IOException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }
	}
	
	

	public void startAsk()
	{
		if(DataBase.Records.size() != 0)
		{
			if(is_run == false)
			{
			 askWindow.setBackground(new Color(0, 0, 0));
             askWindow.setOpacity(0.8f);
             askWindow.setVisible(true);
             
        	timer.schedule(new TimerTask() {
			
        		@Override
        		public void run() {
				System.out.println("ASK_WINDOW");
				askWindow.display.setText(DataBase.getRandom().getQuestion());
				askWindow.setVisible(true);
        		}
        		}, 0,5*60*1000);
        		is_run = true;
        		startItem.setLabel("Stop");
				}
				else
				{
					startItem.setLabel("Start");
					timer.cancel();
					timer = new Timer();
					is_run = false;
				}
		}
	}
	
	@Override
	public void run() {
	      setMenu();
	      setTray();
	      
	     // Start-up 
	      startAsk();
	}

}
