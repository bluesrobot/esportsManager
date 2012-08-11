package org.dyndns.gametime.esportsManager;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import test.TestUtils;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.Menu;
import de.matthiasmann.twl.ProgressBar;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.Table;
import de.matthiasmann.twl.TableRowSelectionManager;
import de.matthiasmann.twl.ResizableFrame.ResizableAxis;
import de.matthiasmann.twl.Timer;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleTableModel;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

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
	private HomeScreen homescreen;
	private ResizableFrame frame;
	private Team playerteam;
	
	public Main() {		
		game = new Game();
		game.setCurrentTeam(Team.randomTeamFromCountry("us"));
		
		mainmenu = new MainMenu();
		playerteam = game.getCurrentTeam();
		homescreen = new HomeScreen(playerteam);
		
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
					switchTo(homescreen);
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
    
    class HomeScreen extends Widget implements Refreshable {
    	private ProgressBar prog_bar;
    	
    	private Button btn1;
    	private Button btn2;
    	private Button inc_spd;
    	private Button dec_spd;
    	private Button begin_day;
    	private Button pause_day;
    	
    	private Label team1;
    	private Label lbl_speed;
    	private Label lbl_date;
    	
    	private TeamTable teamtable;
    	
    	private float time = 0;
    	private int delay = 1000;
    	private int speed = 0;
    	private Timer timer;
    	
    	public HomeScreen(Team team){
    		/*-----------------------------
    		 * Progress bar
    		 ------------------------------*/
    		prog_bar = new ProgressBar();
    		
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
					playerteam = game.getCurrentTeam();
					//teamscreen = new TeamScreen(playerteam);
					switchTo(homescreen);
				}
    		});
    		
    		/*------------------
    		 * Begin day button
    		 -------------------*/
    		
    		begin_day = new Button();
    		begin_day.setTheme("button");
    		begin_day.setText("Begin Day [>");
    		begin_day.addCallback(new Runnable(){
				@Override
				public void run() {
					timer.start();					
				}
    			
    		});
    		
    		/*------------------
    		 * Pause day button
    		 -------------------*/
    		
    		pause_day = new Button();
    		pause_day.setTheme("button");
    		pause_day.setText("Pause Day ||");
    		pause_day.addCallback(new Runnable(){
				@Override
				public void run() {
					timer.stop();					
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
			team1.setText(game.getCurrentTeam().toString());
    		
    		add(prog_bar);
    		add(btn1);
    		add(btn2);
    		add(begin_day);
    		add(pause_day);
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
						timer.stop();
						prog_bar.setValue(time);
					}else{
						prog_bar.setValue(time);
					}
					
				}
			});
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
    		begin_day.adjustSize();
    		pause_day.adjustSize();
    		prog_bar.setSize(140, 35);
    		
    		/*-----------------------------
    		 * Set position of GUI objects
    		 ------------------------------*/
    		prog_bar.setPosition(700, 500); 
    		inc_spd.setPosition(prog_bar.getX() - 30, prog_bar.getY());
    		dec_spd.setPosition(prog_bar.getRight() + 10, prog_bar.getY());
    		btn2.setPosition(btn1.getRight() + 10, btn1.getY());
    		begin_day.setPosition(btn2.getRight() + 10, btn1.getY());
    		pause_day.setPosition(begin_day.getRight() + 10, btn1.getY());
    		lbl_speed.setPosition(prog_bar.getX() + (prog_bar.getWidth()/2) - 2, prog_bar.getY() + prog_bar.getHeight() + 10 );
        	lbl_date.setPosition(prog_bar.getX() + (prog_bar.getWidth()/2) - (lbl_date.getWidth()/2), prog_bar.getY() - 20 );
    		
    		team1.setPosition(0, btn1.getBottom() + 10);
    		teamtable.setPosition(0, team1.getBottom() + 10);
    		teamtable.setSize(600, 600);
    	}

		public void refresh() {
    		teamtable.setTeam(playerteam);
			team1.setText(playerteam.toString());
		}
		
		class TeamTable extends Widget{
	    	private final Table playerTable;
	    	public final SimpleTableModel tablemod;
	    	private final ScrollPane tablePane;

	    	public TeamTable(Team team){		
	    		tablePane = new ScrollPane();
	    		tablePane.setFixed(ScrollPane.Fixed.HORIZONTAL);

	    		String[] headers = {"First", "Last", "Gamertag", "Happy", "XP"};
	    		tablemod = new SimpleTableModel(headers);
	    		playerTable = new Table(tablemod);
	    		//playerTable.setDefaultSelectionManager();
	    		playerTable.setSelectionManager(new RclickTableRowSelectionManager());

	    		tablePane.setContent(playerTable);

	    		add(tablePane);

	    		for(Player p : team.players()){
	    			tablemod.addRow((Object[])p.tableForm());
	    		}
	    	}

	    	public void setTeam(Team t){
	    		if(tablemod.getNumRows() > 0){
	    			tablemod.deleteRows(0, tablemod.getNumRows());
	    		}
	    		for(Player p : t.players()){
	    			tablemod.addRow((Object[])p.tableForm());
	    		}
	    	}

	    	@Override
	    	protected void layout() {
	    		tablePane.setSize(getInnerWidth(), getInnerHeight());
	    	}

	    	class RclickTableRowSelectionManager extends TableRowSelectionManager {
	    		@Override
	    		public boolean handleMouseEvent(int row, int column, Event event) {
	    			// TODO Auto-generated method stub
	    			switch (event.getType()) {
	    			case MOUSE_BTNDOWN:
	    				if(event.getMouseButton() == Event.MOUSE_RBUTTON) {
	    					System.out.println("rbtn");
	    					Menu pop = createPlayerMenu(game.getCurrentTeam().players().get(row));
	    					//TODO: is there a better way to store the players to figure out who is clicked on?
	    					System.out.println(row + " " + column);
	    					System.out.println(game.getCurrentTeam().players().get(row).toString());
	    					pop.openPopupMenu(playerTable, event.getMouseX(), event.getMouseY());
	    				}
	    				break;
	    			default:
	    				break;
	    			}
	    			return super.handleMouseEvent(row, column, event);
	    		}

	    		private Menu createPlayerMenu(Player player) {
	    			Menu menu = new Menu();
	    			menu.add(player.firstname + " " + player.lastname, new Runnable(){
	    				@Override
	    				public void run() {
	    					// TODO Auto-generated method stub

	    				}});
	    			menu.add("Herp", new Runnable(){
	    				@Override
	    				public void run() {
	    					// TODO Auto-generated method stub
	    					System.out.println("menu");

	    				}
	    			});
	    			System.out.println("ok");
	    			return menu;
	    		}
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
		default:
			break;
		}
		return evt.isMouseEventNoWheel();
	}
}
