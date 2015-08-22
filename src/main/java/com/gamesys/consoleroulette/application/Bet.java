package com.gamesys.consoleroulette.application;

import java.math.BigDecimal;

/**
 * Class representing a bet.
 * 
 * @author Andreas
 *
 */
public class Bet
{
	// Number to bet on.
	private int number;
	
	// Amount to bet.
	private BigDecimal amount;
	
	public Bet(int number, BigDecimal amount)
	{
		this.number = number;
		this.amount = amount;
	}
	
	/**
	 * Return the number the player bet on.
	 * 
	 * @return numeber
	 */
	public int getNumber()
	{
		return number;
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
