package org.dyndns.gametime.esportsManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Player {
	String firstname;
	String lastname;
	int age;
	String gamertag;
	String city;
	String countrycode;
	int exp;
	int viewers;
	double happy;	
	
	public Player(String firstname, String lastname, int age, String gamertag,
			String city, String countrycode, int exp, int viewers, double happy) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.age = age;
		this.gamertag = gamertag;
		this.city = city;
		this.countrycode = countrycode;
		this.exp = exp;
		this.viewers = viewers;
		this.happy = happy;
	}
	
	public static Player randomPlayer(){
		String mycountry = randomFrom(Arrays.asList(countrycodes));
		return Player.randomPlayerFromCountry(mycountry);
	}
	
	public static Player randomPlayerFromCountry(String countrycode){
		String mycountry = countrycode;
		String myfirst = randomFrom(firstnamesList.get(mycountry));
		String mylast = randomFrom(lastnamesList.get(mycountry));
		int myage = r.nextInt(35-13)+13;
		String mygamertag = randomGamerTag();
		String mycity = randomFrom(citiesList.get(mycountry));
		int myexp = 0;
		int myviewers = 0;
		double myhappy = 0.0;
		return new Player(myfirst, mylast, myage, mygamertag, mycity, mycountry, myexp, myviewers, myhappy);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Player [firstname=");
		builder.append(firstname);
		builder.append(", lastname=");
		builder.append(lastname);
		builder.append(", gamertag=");
		builder.append(gamertag);
		builder.append(", exp=");
		builder.append(exp);
		builder.append(", viewers=");
		builder.append(viewers);
		builder.append("]\n");
		return builder.toString();
	}

	public void adjustHappy(float value){
		happy += value;
	}
	
	public void adjustExp(int value){
		exp += value;
	}
	
	//keep these in the right order - code and country in the same index.
	private static String[] countrycodes = {"us", "de", "pl", "cn"};
	protected static String[] countries = {"United States", "Germany", "Poland", "China"};
	
	private static HashMap<String, String> countryToCodeMapping = new HashMap<String, String>();
	private static HashMap<String, String> codeToCountryMapping = new HashMap<String, String>();
	
	protected static HashMap<String, ArrayList<String>> firstnamesList = new HashMap<String, ArrayList<String>>();
	protected static HashMap<String, ArrayList<String>> lastnamesList = new HashMap<String, ArrayList<String>>();
	protected static HashMap<String, ArrayList<String>> citiesList = new HashMap<String, ArrayList<String>>();

	private static ArrayList<String> gamertags = new ArrayList<String>();

	
	public static Random r = new Random();
	
	protected static String randomGamerTag(){
		int myrand = r.nextInt(101);
		String tag = "";
		
		//single or compound word
		do{
		if(myrand < 50){
			tag = randomFrom(gamertags);
		}else{
			tag = randomFrom(gamertags) + randomFrom(gamertags);
		}
		}while(tag.length() > 12);
		
		//append numbers?
		myrand = r.nextInt(101);
		if(myrand > 75){
			tag += r.nextInt(2000);
		}
		
		//append XBL letters?
		
		myrand = r.nextInt(101);
		if(myrand > 95){
			tag = "xX" + tag + "Xx";
		}
		
		return tag;
	}
	
	public void print(){
		System.out.println("-- " + firstname + " \"" + gamertag + "\" " + lastname + " | Age:" + age + " | Location: " + city + ", " + getCountryByCode(countrycode));
	}
	
	public String[] tableForm(){
		String[] infos = {firstname, lastname, gamertag, Double.toString(happy), Integer.toString(exp)};
		return infos;
	}
	
	public static String randomFrom(List<String> list){
		return list.get(r.nextInt(list.size()));
	}
	
	private static String getCodeByCountry(String country){
		return countryToCodeMapping.get(country);
	}
	
	private static String getCountryByCode(String code){
		return codeToCountryMapping.get(code);
	}

	public static String[] getCountryCodes(){
		return countrycodes;
	}
	
	public static void loadConfig(){
		String firstnameFile = "firstNames";
		String lastnameFile = "lastNames";
		String citiesFile = "cities";
		String gamertagFile = "gamertags";
		
		//Set up country - code mappings.
		for(int i = 0; i < countries.length; i++){
			countryToCodeMapping.put(countries[i], countrycodes[i]);
			codeToCountryMapping.put(countrycodes[i], countries[i]);
		}
		
		//Prepare names list by country
		for(String c : countrycodes){
			firstnamesList.put(c, new ArrayList<String>());
			lastnamesList.put(c, new ArrayList<String>());
			citiesList.put(c, new ArrayList<String>());
		}
		
		for(String c : countrycodes){
			String fnFile = c+"/"+firstnameFile;
			String lnFile = c+"/"+lastnameFile; 
			String ctFile = c+"/"+citiesFile;
			
				try {
					BufferedReader fnconfiguration = new BufferedReader(new FileReader(fnFile));
					String line;
					while ((line = fnconfiguration.readLine()) != null) {
						line = line.trim();
						firstnamesList.get(c).add(line);
					}
					fnconfiguration.close();
					BufferedReader lnconfiguration = new BufferedReader(new FileReader(lnFile));
					while ((line = lnconfiguration.readLine()) != null) {
						line = line.trim();
						lastnamesList.get(c).add(line);
					}
					lnconfiguration.close();
					BufferedReader ctconfiguration = new BufferedReader(new FileReader(ctFile));
					while ((line = ctconfiguration.readLine()) != null) {
						line = line.trim();
						citiesList.get(c).add(line);
					}
					ctconfiguration.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		try {
			BufferedReader gtconfiguration = new BufferedReader(new FileReader(gamertagFile));
			String line;
			while ((line = gtconfiguration.readLine()) != null) {
				line = line.trim();
				gamertags.add(line);
			}
			gtconfiguration.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
