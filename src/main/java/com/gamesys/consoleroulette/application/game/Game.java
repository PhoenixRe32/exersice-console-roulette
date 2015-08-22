package com.gamesys.consoleroulette.application.game;

import java.math.BigDecimal;

public class Game
{
	// Max bet allowed in games.
	protected static final BigDecimal MAX_BET = new BigDecimal(100);

	// Lowest number allowed to bet on / lowest range of game.
	private int lowerBound;
	
	// Highest number allowed to bet on / highest range of game.
	private int upperBound;

	Game(int lowerBound, int upperBound)
	{
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	/**
	 * @return the upperBound
	 */
	public int getUpperBound()
	{
		return upperBound;
	}

	/**
	 * @return the lowerBound
	 */
	public int getLowerBound()
	{
		return lowerBound;
	}
}
