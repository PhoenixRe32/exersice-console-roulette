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
	
	public Bet(String choice, BigDecimal amount)
	{
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
}
