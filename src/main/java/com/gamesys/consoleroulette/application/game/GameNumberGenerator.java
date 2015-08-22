package com.gamesys.consoleroulette.application.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gamesys.consoleroulette.application.bet.BetListener;

/**
 * Main class for generating numbers for games. This can be modified to be used as a base class and extend it for
 * different kind of games that might need different number types (double, float, etc) or different amount of
 * parameters. For now keeping it simple, just for roulette, two boundaries, lower number and upper number.
 * 
 * @author Andreas Andreou
 *
 */
public class GameNumberGenerator extends Game implements Runnable
{
	// logger, because system.out and system.err... meh
	private static final Logger log = LoggerFactory.getLogger(GameNumberGenerator.class);

	// Random number generator.
	private final Random randomGenerator;

	// Frequency at which to spit the numbers
	private int frequency;

	// Game id. Will only be accessed for writing from this thread. Might be accessed for reading from more than one.
	private AtomicLong gameId;

	private List<BetListener> betListeners = new ArrayList<BetListener>();

	public GameNumberGenerator(int lowerBound, int upperBound, int frequency)
	{
		super(lowerBound, upperBound);
		this.randomGenerator = new Random(System.currentTimeMillis());
		this.frequency = frequency;
		this.gameId = new AtomicLong(0);
	}

	public void run()
	{
		int winningNumber;

		while (true)
		{
			/*
			 * Wait for a time before spitting out a result. Time defined in the constructor using the frequency. It is
			 * in seconds. After the thread wakes, then a number is generated and all bets that were made are evaluated.
			 */
			try
			{
				Thread.sleep(frequency * 1000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// The game has ended. We generate the winning number.
			log.debug("Generating the winning number of the current game");
			winningNumber = generateNumber();

			// The game has ended so the next game will start. We increase the game id but first we keep a copy of
			// the current game id to use in the evaluation of the bets of the game that just ended.
			long currentGameId = gameId.get();
			gameId.getAndIncrement();

			// Print results header
			printResultsHeader(winningNumber);

			// Notify all players (that may be interested) that the bet is completed.
			for (BetListener bl : betListeners)
			{
				bl.betCompleted(currentGameId, winningNumber);
			}
		}

	}

	private void printResultsHeader(int winningNumber)
	{
		// Print result table title;
		System.out.println("Number: " + winningNumber);
		System.out.printf("%-10s %5s %9s %10s %10s %10s %n", "Player", "Bet", "Outcome", "Winnings", "TotalBet",
				"TotalWin");
		System.out.println("----------");
	}

	// Instances to listen for when a bet is complete.
	public void addBetListener(BetListener toAdd)
	{
		betListeners.add(toAdd);
	}

	/**
	 * Generates an integer number between the two boundaries set in the constructor (inclusive).
	 * 
	 * @return the number generated
	 */
	private int generateNumber()
	{
		return randomGenerator.nextInt(getUpperBound()) + getLowerBound();
	}
	
	/**
	 * Return the current game id (as a long). Will be used from the bet monitor to read the game id variable.
	 * 
	 */
	public long getCurrentGameId()
	{
		return gameId.get();
	}
}
