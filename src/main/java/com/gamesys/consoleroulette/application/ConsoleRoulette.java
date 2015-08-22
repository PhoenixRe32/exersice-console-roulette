package com.gamesys.consoleroulette.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Class that contains entry point. Sets up the threads, reads the file with the player details and starts the main
 * application/game.
 * 
 * @author Andreas
 *
 */
public final class ConsoleRoulette
{
	private final String PLAYER_RECORD = "player_data.txt";
	
	private final int LOWER_BOUND = 1;
	
	private final int UPPER_BOUND = 36;
	
	private final int FRQUENCY = 30;

	public static void main(String[] args)
	{
		ConsoleRoulette cr = new ConsoleRoulette();
		
		ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<String, Player>(2);
		
		try
		{
			// Getting the file that contains the names of the players.
			InputStream inputStream = cr.getClass().getClassLoader().getResourceAsStream(cr.PLAYER_RECORD);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			// Reading the file and creating the players.
			String line;
			String[] playerDetails;
			int tokens = 3;
			Pattern pattern = Pattern.compile(",");
			
			// Variables for the details of the player
			String userName;
			BigDecimal totalWin;
			BigDecimal totalBet;
			
			while ((line = bufferedReader.readLine()) != null)
			{
				playerDetails = pattern.split(line, tokens);
				
				/*
				 * While we limit the number of tokens to the expected number we also check if the tokens are less and
				 * print an error message to the user so he can correct his input.
				 */
				if (playerDetails.length < tokens)
				{
					System.out.println("'" +line + "' was malformed. The format is 'Username TotalWin TotalBet'.");
					continue; // no point continuing, read the next input.
				}
				
				try
				{
					userName = playerDetails[0];
					
					totalWin = new BigDecimal(playerDetails[1]);
					
					totalBet = new BigDecimal(playerDetails[2]);
				}
				catch (NumberFormatException nfe)
				{
					System.err.println(nfe.getMessage());
					System.err.println("The bet and win amount must be a real numbers (i.e. 0.15, 3.50)");
					continue; // no point continuing, read the next input.
				}
				
				players.put(userName, new Player(userName, totalBet, totalWin));
			}
			bufferedReader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Setting the players in the bet monitor.
		GameBetMonitor betMonitor = new GameBetMonitor(cr.LOWER_BOUND, cr.UPPER_BOUND, players);
				
		GameNumberGenerator gameNumberGenerator = new GameNumberGenerator(cr.LOWER_BOUND, cr.UPPER_BOUND, cr.FRQUENCY);
		gameNumberGenerator.setBetMonitor(betMonitor);
		
		System.out.println("************************\n"
						+  "*                      *\n"
						+  "* Starting roulette... *\n"
						+  "*                      *\n"
						+  "************************");
		
		// The thread running the two processes.
		Thread gameThread = new Thread(gameNumberGenerator, "GameThread");
		Thread betMonitorThread = new Thread(betMonitor, "BetMonitor");
		
		gameThread.start();
		betMonitorThread.start();
	}

}
