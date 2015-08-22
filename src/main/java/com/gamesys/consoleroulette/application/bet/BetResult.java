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
	// Number or choice bet on.
	private String choice;
	
	// Number that won.
	private int winningNumber;
		
	// Amount won.
	private BigDecimal winnings;
	
	// Player user name
	private String userName;
	
	// Outcome of the game.
	private Outcome outcome;
		
	public BetResult(int winningNumber, String choice, BigDecimal winnings, String userName, Outcome outcome)
	{
		this.winningNumber= winningNumber;
		this.userName = userName;
		this.choice = choice;
		this.winnings = winnings;
		this.outcome = outcome;
	}

	public String getUserName()
	{
		return userName;
	}

	public String getChoice()
	{
		return choice;
	}

	public Outcome getOutcome()
	{
		return outcome;
	}

	public BigDecimal getWinnings()
	{
		return winnings;
	}

	/**
	 * @return the winningNumber
	 */
	public int getWinningNumber()
	{
		return winningNumber;
	}
}
