package org.dyndns.gametime.esportsManager;

import java.util.Random;

public class EmulatedGame {
	private int time; //3600s = 60min, 900s = 15m
	private int scorehome;
	private int scoreaway;
	
	private Random r = new Random();
	
	public EmulatedGame(){
		time = r.nextInt(2500)+900;
		do{
		scorehome = r.nextInt(50);
		scoreaway = r.nextInt(50);
		}while(scorehome == scoreaway); //no ties yet
	}
	
	public boolean won(){
		if(scorehome > scoreaway){
			return true;
		}else{
			return false;
		}
	}
}
