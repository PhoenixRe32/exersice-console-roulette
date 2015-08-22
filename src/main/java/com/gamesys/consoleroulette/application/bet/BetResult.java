package com.gamesys.consoleroulette.application.bet;

import java.math.BigDecimal;

/**
 * Class representing the result of a bet.
 * 
 * @author Andreas
 *
 */
public class BetResult
{
	// Number bet on.
	private String number;
		
	// Amount won.
	private BigDecimal winnings;
	
	// Player user name
	private String userName;
	
	// Outcome of the game.
	private Outcome outcome;
		
	public BetResult(String number, BigDecimal winnings, String userName, Outcome outcome)
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
		return number;
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
