package org.dyndns.gametime.esportsManager;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

public class Game {
	Random r = new Random();
	GameCalendar calendar = new GameCalendar();
	
	public enum PlayerAction{
		STREAM,
		LADDER,
		DAYOFF,
		SCRIM,
		OTHERGAME
	}
	
	private Team currentTeam;
	
	public Game(){
		Team.loadConfig();
		Player.loadConfig();
	}

	public Team getCurrentTeam(){
		return currentTeam;
	}

	public void setCurrentTeam(Team currentTeam) {
		this.currentTeam = currentTeam;
	}
	
	public void doPlayerAction(Player p, PlayerAction pa){
		//TODO: finish player actions
		switch(pa){
		case STREAM:
			//Magic numbers: assuming games run from 15-60minutes, the minimum for a 4 hour span is 4 games, the max for 8 hours is 32.
			int gamesplayed = r.nextInt(28)+4;
			int expgain = r.nextInt(10)+5;
			for(int i = 0; i < gamesplayed; i++){
				if(new EmulatedGame().won()){
					p.adjustHappy(1);
					p.adjustExp(expgain);
				}else{
					p.adjustHappy(-1);
					p.adjustExp(expgain/2);
				}
			}
			System.out.println(p);
		case DAYOFF:
			break;
		case LADDER:
			break;
		case OTHERGAME:
			break;
		case SCRIM:
			break;
		default:
			break;
			
		}
	}
	
	public class DailyQueue{
		private HashMap<Player, PlayerAction> playeractions;
		
		public DailyQueue(){
			playeractions = new HashMap<Player, PlayerAction>();
		}
		
		public void addAction(Player p, PlayerAction pa){
			playeractions.put(p, pa);
		}
		
		public void execute(){
			for(Entry<Player, PlayerAction> e : playeractions.entrySet()){
				doPlayerAction(e.getKey(), e.getValue());
			}
			playeractions.clear();
		}
	}
	
	public void doTeamAction(){
		
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Game [r=");
		builder.append(r);
		builder.append(", currentTeam=");
		builder.append(currentTeam);
		builder.append("]");
		return builder.toString();
	}
	
	public GameCalendar getCalender(){
		return calendar;
	}
	
	
}
