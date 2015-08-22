package com.gamesys.consoleroulette.application;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

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
	// Even value
	private static final int EVEN = 38;

	// Odd value
	private static final int ODD = 37;

	// Random number generator.
	private final Random randomGenerator;

	// Frequency at which to spit the numbers
	private int frequency;

	// Reference to the bet monitor so we can evaluate the results and inform when we do using the settlingBets flag.
	private GameBetMonitor betMonitor;

	public GameNumberGenerator(int lowerBound, int upperBound, int frequency)
	{
		super(lowerBound, upperBound);
		this.randomGenerator = new Random(System.currentTimeMillis());
		this.frequency = frequency;
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

			// Winning number.
			winningNumber = generateNumber();

			// Inform the bet monitor that we will evaluate the results of all the bets.
			betMonitor.setSettlingBets(true);

			// Get curent game id.
			long gameId = betMonitor.getGameId();
			List<BetResult> betResults = evaluateResults(winningNumber, gameId);

			// Increase the game id since this game ended and another is going to start.
			betMonitor.setGameId(gameId + 1);

			// We are done, new bets can be made for the next game.
			betMonitor.setSettlingBets(false);

			// Print results
			printResuls(betResults, winningNumber);
		}

	}

	/**
	 * Evaluates the bets against the winning number.
	 * 
	 * @param winningNumber
	 * @param gameId
	 * @return a list of results that contain information used to present the results.
	 */
	private List<BetResult> evaluateResults(int winningNumber, long gameId)
	{
		// The results.
		List<BetResult> betResults = new LinkedList<BetResult>();

		/*
		 * We will get all the players and iterate over them. For each player we will access all his bets and then
		 * evaluate them against the result of this game. When evaluating them we will also prepare the presentation of
		 * the results.
		 */
		ConcurrentHashMap<String, Player> players = betMonitor.getPlayers();
		if (players.isEmpty())
		{
			System.out.println("There are no players in the room!");
		}
		else
		{
			// Loop over the users.
			for (Map.Entry<String, Player> playerEntry : players.entrySet())
			{
				// Get the bet of the player.
				Player player = playerEntry.getValue();
				ConcurrentHashMap<Long, Bet> betHistory = player.getBetHistory();
				Bet currentBet = betHistory.get(gameId);
				if (currentBet == null)
				{
					// This player didn't place a bet in this game, moving on.
					continue;
				}

				// Outcome of bet.
				Outcome outcome;
				// Winnings of bet.
				// TODO perhaps it would be better to use smallest denomination, i.e pence, cents, etc
				BigDecimal winnings;

				int betNumber = currentBet.getNumber();
				if (betNumber == ODD)
				{
					if ((winningNumber % 2) == 1)
					{
						outcome = Outcome.WIN;
						winnings = currentBet.getAmount().multiply(new BigDecimal(2));
					}
					else
					{
						outcome = Outcome.LOSE;
						winnings = BigDecimal.ZERO;
					}
				}
				else if (betNumber == EVEN)
				{
					if ((winningNumber % 2) == 0)
					{
						outcome = Outcome.WIN;
						winnings = currentBet.getAmount().multiply(new BigDecimal(2));
					}
					else
					{
						outcome = Outcome.LOSE;
						winnings = BigDecimal.ZERO;
					}
				}
				else if (betNumber == winningNumber)
				{
					outcome = Outcome.WIN;
					winnings = currentBet.getAmount().multiply(new BigDecimal(36));
				}
				else
				{
					outcome = Outcome.LOSE;
					winnings = BigDecimal.ZERO;
				}
				betResults.add(new BetResult(betNumber, winnings, player.getUserName(), outcome));
				player.updateTotalWin(winnings);
			}
		}
		return betResults;
	}

	private void printResuls(List<BetResult> betResults, int winningNumber)
	{
		// Print result table title;
		System.out.println("Number: " + winningNumber);
		System.out.printf("%-10s %5s %9s %10s %10s %10s %n", 
				"Player", "Bet", "Outcome", "Winnings", "TotalBet", "TotalWin");
		System.out.println("----------");

		for (BetResult br : betResults)
		{
			System.out.printf("%-10s %5s %9s %10s %10s %10s %n",
					br.getUserName(), br.getBet(), br.getOutcome(), br.getWinnings(),  
					(betMonitor.getPlayers().get(br.getUserName())).getTotalBet(), 
					(betMonitor.getPlayers().get(br.getUserName())).getTotalWin());
		}
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

	public void setBetMonitor(GameBetMonitor betMonitor)
	{
		this.betMonitor = betMonitor;
	}

}
