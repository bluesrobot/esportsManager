package org.dyndns.gametime.esportsManager;

import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.Widget;

public class PlayerFrame extends Widget {
	private final Widget playerContainer;
	public final EditField textBox;
	private final ScrollPane textPane;
	
	public PlayerFrame(Player p){
		playerContainer = new Widget();
		playerContainer.setTheme("insetbackground");
		
		textPane = new ScrollPane();
		textPane.setFixed(ScrollPane.Fixed.HORIZONTAL);
		add(textPane);
		
		textBox = new EditField();
		textBox.setTheme("editfield");
		textBox.setMultiLine(true);
		textBox.setText(p.toString());
		textPane.setContent(textBox);
	}
	
	private static void setRelativePosition(int x, int y, Widget self){
		Widget parent = self.getParent();
		self.setPosition(parent.getInnerX() + x, parent.getInnerY() + y);
	}
	
	@Override
	protected void layout() {
		//Table Frame
		textPane.setSize(textPane.getParent().getInnerWidth() - 20, 550);
		setRelativePosition(10, 10, textPane);
		}
}
