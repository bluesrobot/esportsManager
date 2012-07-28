package org.dyndns.gametime.esportsManager;

import java.util.ArrayList;

import org.dyndns.gametime.esportsManager.Game.DailyQueue;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import test.TestUtils;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Container;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.FPSCounter;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.Menu;
import de.matthiasmann.twl.ProgressBar;
import de.matthiasmann.twl.RadialPopupMenu;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.ResizableFrame.ResizableAxis;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.TabbedPane;
import de.matthiasmann.twl.Table;
import de.matthiasmann.twl.Timer;
import de.matthiasmann.twl.ToggleButton;
import de.matthiasmann.twl.WheelWidget;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleChangableListModel;
import de.matthiasmann.twl.model.SimpleTableModel;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.Renderer;
import de.matthiasmann.twl.renderer.Texture;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLDynamicImage;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.GridImage;
import de.matthiasmann.twl.theme.ThemeManager;
import java.util.Calendar;

public class Main extends Widget{
	
	public static void main(String[] args) {
		try {
			Display.setDisplayMode(new DisplayMode(1600, 900));
			Display.create();
			Display.setTitle("TWL Game UI Demo");
			Display.setVSyncEnabled(true);

			LWJGLRenderer renderer = new LWJGLRenderer();
			Main main = new Main();
			GUI gui = new GUI(main, renderer);
    		
			ThemeManager theme = ThemeManager.createThemeManager(
					Main.class.getResource("/theme/simple.xml"), renderer);
			gui.applyTheme(theme);
			
			while(!Display.isCloseRequested() && !main.quit) {
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				gui.update();
				Display.update();
				TestUtils.reduceInputLag();
			}

			gui.destroy();
			theme.destroy();
		} catch (Exception ex) {
			//ex.printStackTrace();
			TestUtils.showErrMsg(ex);
		}
		Display.destroy();
	}

	private Game game;
	public boolean quit;
	private MainMenu mainmenu;
	private TeamScreen teamscreen;
	private ResizableFrame frame;
	private Team playerteam;
	
	public Main() {		
		game = new Game();
		game.setCurrentTeam(Team.randomTeamFromCountry("us"));
		
		mainmenu = new MainMenu();
		try {
			playerteam = game.getCurrentTeam();
			teamscreen = new TeamScreen(playerteam);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		frame = new ResizableFrame();
		frame.setResizableAxis(ResizableAxis.NONE);
		frame.add(mainmenu);
		
		add(frame);
	}

    void switchTo(Widget widget){
    	frame.removeAllChildren();
    	((Refreshable) widget).refresh();
    	frame.add(widget);
    }
    
    class MainMenu extends Widget implements Refreshable {
    	private Label lbl1;
    	private Label lbl2;
    	private Button btn1;
    	
    	public MainMenu(){
    		
    		lbl1 = new Label();
    		lbl1.setText("Main Menu");
    		
    		lbl2 = new Label();
    		lbl2.setText(game.toString());
    		
    		btn1 = new Button();
    		btn1.setTheme("button");
    		btn1.setText("Start Game");
    		btn1.addCallback(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					switchTo(teamscreen);
				}
    			
    		});

    		add(lbl1);
    		add(lbl2);
    		add(btn1);

    	}
    	
    	@Override
    	public void layout(){
    		lbl1.adjustSize();
    		lbl2.adjustSize();
    		btn1.adjustSize();
    	
    		btn1.setPosition(lbl1.getRight() + 10, lbl1.getY());
    		lbl2.setPosition(lbl1.getX(), lbl1.getBottom() + 10);
    	}

    	public void refresh(){
    		lbl2.setText(game.toString());
    	}

    }
    
    class TeamScreen extends Widget implements Refreshable {
    	private ProgressBar prog_bar;
    	
    	private Button btn1;
    	private Button btn2;
    	private Button inc_spd;
    	private Button dec_spd;
    	
    	private Label team1;
    	private Label lbl_speed;
    	private Label lbl_date;
    	
    	private TeamTable teamtable;
    	
    	private float time = 0;
    	private int delay = 1000;
    	private int speed = 0;
    	private Timer timer;
    	
    	public TeamScreen(Team team){
    		/*-----------------------------
    		 * Progress bar
    		 ------------------------------*/
    		prog_bar = new ProgressBar();
    		prog_bar.setSize(100, 20);
    		
    		/*-----------------------------
    		 * Label for progress bar speed
    		 ------------------------------*/
    		lbl_speed = new Label();
    		lbl_speed.setText( Integer.toString(speed) );
    		
    		/*-----------------------------
    		 * Label for current calendar date
    		 ------------------------------*/
    		lbl_date = new Label();
    		lbl_date.setText( game.calendar.toString() );
    		
    		/*-----------------------------
    		 * Button 1
    		 ------------------------------*/
    		btn1 = new Button();
    		btn1.setTheme("button");
    		btn1.setText("Main Menu");
    		btn1.addCallback(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					switchTo(mainmenu);
				}
    		});
    		
    		/*-----------------------------
    		 * Button 2
    		 ------------------------------*/
    		btn2 = new Button();
    		btn2.setTheme("button");
    		btn2.setText("New Team");
    		btn2.addCallback(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					game.setCurrentTeam(Team.randomTeamFromCountry("us"));
					try {
						playerteam = game.getCurrentTeam();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//teamscreen = new TeamScreen(playerteam);
					switchTo(teamscreen);
				}
    		});
    		
    		/*-----------------------------
    		 * Increase progress bar speed ( + )
    		 ------------------------------*/
    		inc_spd = new Button();
    		inc_spd.setTheme("button");
    		inc_spd.setText("+");
    		inc_spd.addCallback(new Runnable(){
				@Override
				public void run() {
					if( delay > 100 ){
						speed++;
						delay -= 100;
						timer.setDelay(delay);
			    		lbl_speed.setText( Integer.toString(speed) );
					}
				}
    			
    		});	
    		
    		/*-----------------------------
    		 * Decrease progress bar speed ( - )
    		 ------------------------------*/
    		dec_spd = new Button();
    		dec_spd.setTheme("button");
    		dec_spd.setText("-");
    		dec_spd.addCallback(new Runnable(){
				@Override
				public void run() {
					if( delay <= 1900 ){
						speed--;
						delay += 100;
						timer.setDelay(delay);
			    		lbl_speed.setText( Integer.toString(speed) );
					}
				}
    		});		
    		
    		team1 = new Label();
    		teamtable = new TeamTable(playerteam);
    		try {
				team1.setText(game.getCurrentTeam().toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		add(prog_bar);
    		add(btn1);
    		add(btn2);
    		add(inc_spd);
    		add(dec_spd);
    		add(lbl_speed);
    		add(lbl_date);
    		add(team1);
    		add(teamtable);
    	}
    	
		/*-----------------------------
		 * Timer for progress bar after GUI
		 ------------------------------*/
    	@Override
    	protected void afterAddToGUI(GUI gui){
    	timer = new Timer(gui);
		timer.setDelay(delay);
		timer.setCallback(new Runnable(){
			@Override
			public void run() {
				time += .01f;
				System.out.println(time);
				if( time >= 1 ){
					game.calendar.addDay();
		    		lbl_date.setText( game.calendar.toString() );
					time = 0;
				}
				prog_bar.setValue(time);
			}
		});
		timer.start();	
		timer.setContinuous(true);

    	}
    	
    	@Override
    	public void layout(){
    		/*-----------------------------
    		 * Adjust buttons to text size
    		 ------------------------------*/
    		btn1.adjustSize();
    		btn2.adjustSize();
    		team1.adjustSize();
    		inc_spd.adjustSize();
    		dec_spd.adjustSize();
    		prog_bar.adjustSize();
    		
    		/*-----------------------------
    		 * Set position of GUI objects
    		 ------------------------------*/
    		prog_bar.setPosition(700, 500); 
    		inc_spd.setPosition(prog_bar.getX() - 30, prog_bar.getY());
    		dec_spd.setPosition(prog_bar.getRight() + 10, prog_bar.getY());
    		btn2.setPosition(btn1.getRight() + 10, btn1.getY());
    		lbl_speed.setPosition(prog_bar.getX() + (prog_bar.getWidth()/2) - 2, prog_bar.getY() + prog_bar.getHeight() + 10 );
        	lbl_date.setPosition(prog_bar.getX() + (prog_bar.getWidth()/2) - (lbl_date.getWidth()/2), prog_bar.getY() - 20 );
    		
    		team1.setPosition(0, btn1.getBottom() + 10);
    		teamtable.setPosition(0, team1.getBottom() + 10);
    		teamtable.setSize(600, 600);
    	}

		public void refresh() {
    		teamtable.setTeam(playerteam);
    		try {
				team1.setText(playerteam.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
	
	@Override
	protected void layout() {
		super.layout();
		
        frame.setSize(getInnerWidth(), getInnerHeight());
	}

	@Override
	protected boolean handleEvent(Event evt) {
		if(super.handleEvent(evt)) {
			return true;
		}
		switch (evt.getType()) {
		case KEY_PRESSED:
			switch (evt.getKeyCode()) {
			case Event.KEY_ESCAPE:
				quit = true;
				return true;
			}
			break;
		}
		return evt.isMouseEventNoWheel();
	}
}
