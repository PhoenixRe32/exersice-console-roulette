package com.gamesys.consoleroulette.application;

import java.math.BigDecimal;

/**
 * Class representing the result of a bet.
 * 
 * @author Andreas
 *
 */
public class BetResult
{
	private static final String ODD = "ODD";

	private static final String EVEN = "EVEN";

	// Number bet on.
	private int number;
		
	// Amount won.
	private BigDecimal winnings;
	
	// Player user name
	private String userName;
	
	// Outcome of the game.
	private Outcome outcome;
		
	public BetResult(int number, BigDecimal winnings, String userName, Outcome outcome)
	{
		this.userName = userName;
		this.number = number;
		this.winnings = winnings;
		this.outcome = outcome;
	}

	public String getUserName()
	{
		return userName;
	}

	public String getBet()
	{
		if (number < 37 && number > 0)
		{
			return Integer.toString(number);
		}
		else if (number == 37)
		{
			return ODD;
		}
		else if (number == 38)
		{
			return EVEN;
		}
		else
		{
			return "N/A";
		}
	}

	public Outcome getOutcome()
	{
		return outcome;
	}

	public BigDecimal getWinnings()
	{
		return winnings;
	}
}
