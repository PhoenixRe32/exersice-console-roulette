package com.gamesys.consoleroulette.application.bet;

public interface BetListener
{
	void betCompleted(long gameId, int winningNumber);
}
