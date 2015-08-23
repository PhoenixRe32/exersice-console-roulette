package com.gamesys.consoleroulette.application.bet;

import java.math.BigDecimal;

/**
 * Class representing a bet.
 * 
 * @author Andreas
 *
 */
public class Bet
{
	// Number or choice to bet on.
	private String choice;
	
	// Amount to bet.
	private BigDecimal amount;
	
	// Game bet was made on.
	private long gameId;
	
	// User that made the bet.
	private String userName;
	
	public Bet(String userName, String choice, BigDecimal amount)
	{
		this.userName = userName;
		this.choice = choice;
		this.amount = amount;
	}
	
	/**
	 * Return the number the player bet on.
	 * 
	 * @return number
	 */
	public String getChoice()
	{
		return choice;
	}

	/**
	 * Return the amount the player bet.
	 * 
	 * @return amount
	 */
	public BigDecimal getAmount()
	{
		return amount;
	}

	/**
	 * @return the gameId
	 */
	public long getGameId()
	{
		return gameId;
	}

	/**
	 * @return the userName
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * @param gameId the gameId to set
	 */
	public void setGameId(long gameId)
	{
		this.gameId = gameId;
	}
}
