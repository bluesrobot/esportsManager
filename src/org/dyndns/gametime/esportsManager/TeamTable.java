package org.dyndns.gametime.esportsManager;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.Menu;
import de.matthiasmann.twl.MenuElement;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.Table;
import de.matthiasmann.twl.TableRowSelectionManager;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleTableModel;

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
			tablemod.addRow(p.tableForm());
		}
	}
	
	public void setTeam(Team t){
		if(tablemod.getNumRows() > 0){
			tablemod.deleteRows(0, tablemod.getNumRows());
		}
		for(Player p : t.players()){
			tablemod.addRow(p.tableForm());
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
					Menu pop = createPlayerMenu();
					System.out.println(row + " " + column);
					pop.openPopupMenu(playerTable, event.getMouseX(), event.getMouseY());
				}
				break;
			}
			return super.handleMouseEvent(row, column, event);
		}
		
		private Menu createPlayerMenu() {
			Menu menu = new Menu();
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
