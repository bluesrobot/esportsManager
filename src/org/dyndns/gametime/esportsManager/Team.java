package org.dyndns.gametime.esportsManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Team {

	private String teamname;
	private String teamnamelong;
	private String teamnameabbr;
	private String countrycode;
	private int cash;

	private static Random r = new Random();
	
	private ArrayList<Player> players = new ArrayList<Player>();

	private static ArrayList<String> teamnames = new ArrayList<String>();

	public static void loadConfig(){
		String teamnamesFile = "teamnames";
		
		try {
			String line;
			BufferedReader gtconfiguration = new BufferedReader(new FileReader(teamnamesFile));
			while ((line = gtconfiguration.readLine()) != null) {
				line = line.trim();
				teamnames.add(line);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Team [teamname=");
		builder.append(teamname);
		builder.append("\n teamnamelong=");
		builder.append(teamnamelong);
		builder.append("\n teamnameabbr=");
		builder.append(teamnameabbr);
		builder.append("\n countrycode=");
		builder.append(countrycode);
		builder.append("\n cash=");
		builder.append(cash);
		builder.append("\n players=");
		builder.append(players);
		builder.append("]");
		return builder.toString();
	}



	public Team(String teamname, String teamnamelong, String teamnameabbr,
			String countrycode, int cash) {
		super();
		this.teamname = teamname;
		this.teamnamelong = teamnamelong;
		this.teamnameabbr = teamnameabbr;
		this.countrycode = countrycode;
		this.cash = cash;
	}

	public Team(){
		
	}
	
	public static Team randomTeam(){
		String mycountrycode = Player.randomFrom(Arrays.asList(Player.getCountryCodes()));
		return randomTeamFromCountry(mycountrycode);
	}
	
	public static Team randomTeamFromCountry(String countrycode){
		String myteamname = randomTeamName();
		String myteamnamelong = randomTeamNameLong(myteamname);
		String myteamnameabbr = teamAbbr(myteamname, myteamnamelong);
		String mycountry = countrycode;
		
		Team theTeam = new Team(myteamname, myteamnamelong, myteamnameabbr, mycountry, 0);
		
		for(int i = 0; i < 4; i++){
			theTeam.addPlayer(Player.randomPlayerFromCountry(mycountry));
		}
		
		return theTeam;
	}

	public String getTeamname() {
		return teamname;
	}

	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}

	public String getTeamnamelong() {
		return teamnamelong;
	}

	public void setTeamnamelong(String teamnamelong) {
		this.teamnamelong = teamnamelong;
	}

	public String getTeamnameabbr() {
		return teamnameabbr;
	}

	public void setTeamnameabbr(String teamnameabbr) {
		this.teamnameabbr = teamnameabbr;
	}

	public String getCountrycode() {
		return countrycode;
	}

	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}

	private static String randomTeamName(){
		String teamname = "";

		teamname += Player.randomFrom(teamnames);

		String firstletter = teamname.substring(0, 1);
		String rest = teamname.substring(1);

		teamname = firstletter.toUpperCase() + rest;

		return teamname;
	}

	private static String randomTeamNameLong(String base){

		int myrand = r.nextInt(133);
		if(myrand < 45){
			base = "Team " + base;
		}else if(myrand < 75){
			base += " Gaming";
		}else if(myrand < 120){
			base += " eSports";
		}

		return base;
	}
	
	private static String teamAbbr(String base, String baselong){
		String[] components = baselong.split(" ");
		String abbr = "";
		
		if(!base.equals(baselong)){
			//has team or esports or gaming, user
			if(base.length() <= 4){
				abbr = base.toUpperCase();
			}else{
				for(String part : components){
					abbr += part.substring(0, 1);
				}
			}
		}else{
			if(base.length()>4){
				//should be first 3 non-vowels
				abbr = removeVowels(base).substring(0,3).toUpperCase();
			}else{
				abbr = base.toUpperCase();
			}
		}
				
		return abbr;
	}
	
	private static String removeVowels(String word){
		ArrayList<String> vowels = new ArrayList<String>();
		vowels.add("a");
		vowels.add("e");
		vowels.add("i");
		vowels.add("o");
		vowels.add("u");
		
		String novword = "";
		
		for(int i = 0; i < word.length(); i++){
			if(!vowels.contains(Character.toString(word.charAt(i)))){
				novword += word.charAt(i);
			}
		}
		
		return novword;
	}
	
	public void addPlayer(Player player){
		players.add(player);
	}
	
	public void print(){
		System.out.println("[" + teamnameabbr + "] " + teamnamelong + " (" + countrycode.toUpperCase() + ")");
		System.out.println("$" + cash);
	}
	
	public void printAll(){
		System.out.println("[" + teamnameabbr + "] " + teamnamelong + " (" + countrycode.toUpperCase() + ")");
		System.out.println("$" + cash);
		for(Player p : players){
			p.print();
		}
	}

	public int getCash() {
		return cash;
	}

	public void setCash(int cash) {
		this.cash = cash;
	}



	public ArrayList<Player> players() {
		return players;
	}
}
