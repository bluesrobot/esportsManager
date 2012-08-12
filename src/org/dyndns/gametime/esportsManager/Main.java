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
import de.matthiasmann.twl.TableBase;
import de.matthiasmann.twl.TableRowSelectionManager;
import de.matthiasmann.twl.ResizableFrame.ResizableAxis;
import de.matthiasmann.twl.TableBase.CellWidgetCreator;
import de.matthiasmann.twl.TextWidget;
import de.matthiasmann.twl.ThemeInfo;
import de.matthiasmann.twl.Timer;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleTableModel;
import de.matthiasmann.twl.model.TableModel;
import de.matthiasmann.twl.model.TreeTableNode;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

public class Main extends Widget{

	private Game game;
	public boolean quit;
	private MainMenu mainmenu;
	private HomeScreen homescreen;
	private ResizableFrame frame;
	private Team playerteam;

	public Main() {		
		game = new Game();
		game.setCurrentTeam(Team.randomTeam());

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
					game.setCurrentTeam(Team.randomTeam());
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
			teamtable.setSize(600, 600);
			teamtable.setPosition(getWidth() - teamtable.getWidth() - 10, 10);
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

				String[] headers = {"Player"};
				tablemod = new SimpleTableModel(headers);
				playerTable = new Table(tablemod);
				playerTable.setSelectionManager(new RclickTableRowSelectionManager());
				playerTable.setVaribleRowHeight(true);
				playerTable.registerCellRenderer(Player.class, new PlayerCellRenderer());

				tablePane.setContent(playerTable);

				add(tablePane);

				for(Player p : team.players()){
					tablemod.addRow(p);
				}
			}

			public void setTeam(Team t){
				if(tablemod.getNumRows() > 0){
					tablemod.deleteRows(0, tablemod.getNumRows());
				}
				for(Player p : t.players()){
					tablemod.addRow(p);
				}
			}

			@Override
			protected void layout() {
				tablePane.setSize(getInnerWidth(), getInnerHeight());
			}

			class PlayerCellRenderer implements TableBase.CellRenderer{
				PlayerCell pc;

				public PlayerCellRenderer(){
					pc = new PlayerCell();
				}

				@Override
				public String getTheme() {
					// TODO Auto-generated method stub
					return "";
				}

				@Override
				public void applyTheme(ThemeInfo info) {
					// TODO Auto-generated method stub
				}

				@Override
				public int getColumnSpan() {
					return 1;
				}

				@Override
				public int getPreferredHeight() {
					return 50;
				}

				@Override
				public Widget getCellRenderWidget(int x, int y, int w,int h, boolean isSelected) {
					pc.setPosition(x, y);
					pc.setSize(w, h);
					return pc;
				}

				@Override
				public void setCellData(int row, int column, Object data) {
					pc.setPlayer((Player) data);
				}
			}

			/*--------------------------------------
			 * Controls the layout of each cell item.
			 -------------------------------------*/
			class PlayerCell extends Widget{
				Label name;
				Label stats;

				public PlayerCell(){
					name = new Label();
					stats = new Label();
					add(name);
					add(stats);
				}

				public void setPlayer(Player p){
					name.setText(p.getNameWithGamertag());
					stats.setText("XP: " + p.getExp() + " Happy: " + p.getHappy() + " Viewers: " + p.getViewers());
				}

				@Override
				protected void layout(){
					//name.setPosition(0, 0);
					//System.out.println(name.getX());
					//setSize(50, 100);
					name.adjustSize();
					stats.adjustSize();
					name.setPosition(getX() + 10, getY() + 10);
					//System.out.println(this.toString() + " W=" + getWidth() + " H=" + getHeight() + " X=" + getX() + " Y=" + getY() + " NX=" + name.getX() + " NY=" + name.getY());
					stats.setPosition(getRight() - stats.getWidth() - 10, getBottom() - stats.getHeight() - 10);
				}
			}

			class RclickTableRowSelectionManager extends TableRowSelectionManager {
				@Override
				public boolean handleMouseEvent(int row, int column, Event event) {
					switch (event.getType()) {
					case MOUSE_BTNDOWN:
						if(event.getMouseButton() == Event.MOUSE_RBUTTON) {
							System.out.println("rbtn");
							if(row < game.getCurrentTeam().players().size()){
								Menu pop = createPlayerMenu(game.getCurrentTeam().players().get(row));
								//TODO: is there a better way to store the players to figure out who is clicked on?
								System.out.println(row + " " + column);
								System.out.println(game.getCurrentTeam().players().get(row).toString());
								pop.openPopupMenu(playerTable, event.getMouseX(), event.getMouseY());
							}
						}
						break;
					default:
						break;
					}
					return super.handleMouseEvent(row, column, event);
				}

				private Menu createPlayerMenu(final Player player) {
					Menu menu = new Menu();
					menu.add(player.getFullname(), new Runnable(){
						@Override
						public void run() {
							switchTo(new PlayerScreen(player));
						}});
					System.out.println("ok");
					return menu;
				}
			}
		}
	}

	class PlayerScreen extends Widget implements Refreshable{
		
		private Label name;
		private Label stats;
		private Button btn_back;
		
		
		public PlayerScreen(Player p){
			name = new Label();
			name.setText(p.getNameWithGamertag());
			
			stats = new Label();
			stats.setText("from " + p.getCity() + ", " + p.getCountryName() + "\nAge: " + p.getAge());
			
			btn_back = new Button();
			btn_back.setText("<< Back");
			btn_back.addCallback(new Runnable(){

				@Override
				public void run() {
					switchTo(homescreen);	
				}
				
			});
			
			
			add(name);
			add(stats);
			add(btn_back);
		}
		
		@Override
		protected void layout(){
			name.adjustSize();
			stats.adjustSize();
			btn_back.adjustSize();
			
			btn_back.setPosition(10, 10);
			name.setPosition(btn_back.getX(), btn_back.getBottom() + 10);
			stats.setPosition(btn_back.getX(), name.getBottom() + 10);
		}
		
		@Override
		public void refresh() {
			// TODO Auto-generated method stub
			
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
}
