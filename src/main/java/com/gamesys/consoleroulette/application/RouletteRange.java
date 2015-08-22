package com.gamesys.consoleroulette.application;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Possible outcomes of a game.
 * 
 * @author Andreas
 *
 */
public enum RouletteRange
{
	ONE("1", 36),
	TWO("2", 36),
	THREE("3", 36),
	FOUR("4", 36),
	FIVE("5", 36),
	SIX("6", 36),
	SEVEN("7", 36),
	EIGHT("8", 36),
	NINE("9", 36),
	TEN("10", 36),
	ELEVEN("11", 36),
	TWELVE("12", 36),
	THIRTEEN("13", 36),
	FOURTEEN("14", 36),
	FIFTEEN("15", 36),
	SIXTEEN("16", 36),
	SEVENTEEN("17", 36),
	EIGHTTEEN("18", 36),
	NINETEEN("19", 36),
	TWENTY("20", 36),
	TWENTYONE("21", 36),
	TWENTYTWO("22", 36),
	TWENTYTHREE("23", 36),
	TWENTYFOUR("24", 36),
	TWENTYFIVE("25", 36),
	TWENTYSIX("26", 36),
	TWENTYSEVEN("27", 36),
	TWENTYEIGHT("28", 36),
	TWENTYNINE("29", 36),
	THIRTY("30", 36),
	THIRTYONE("31", 36),
	THIRTYTWO("32", 36),
	THIRTYTHREE("33", 36),
	THIRTYFOUR("34", 36),
	THIRTYFIVE("35", 36),
	THIRTYSIX("36", 36),
	ODD("ODD", 2),
	EVEN("EVEN", 2);

	private final String value;

	private final Integer multiplier;

	private static final Map<String, Integer> map = Collections.unmodifiableMap(initializeMapping());

	private RouletteRange(String value, Integer multiplier)
	{
		this.value = value;
		this.multiplier = multiplier;
	}

	public String getCode()
	{
		return value;
	}

	public Integer getMultiplier()
	{
		return multiplier;
	}

	public static Integer getMultiplierByValue(String value)
	{
		if (map == null)
		{
			initializeMapping();
		}
		if (map.containsKey(value))
		{
			return map.get(value);
		}
		return null;
	}
	
	public static boolean valueExists(String value)
	{
		if (map == null)
		{
			return false;
		}

		return map.containsKey(value);
	}

	private static Map<String, Integer> initializeMapping()
	{
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (RouletteRange r : RouletteRange.values())
		{
			map.put(r.value, r.multiplier);
		}
		
		return map;
	}
}