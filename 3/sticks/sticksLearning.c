#include <stdbool.h>
#include <stdio.h>
#include "playerLearning.h"

void pluralize(unsigned int count, bool invert) {
	if ((count > 1) ^ invert) putchar('s');
}

#define PLAYERS 2
#define MAX_STICKS 20
#define ROUNDS 1000000
int main() {
	LearningPlayer *players[PLAYERS];
	for (unsigned int i = 0; i < PLAYERS; i++) players[i] = newLearningPlayer(MAX_STICKS);
	unsigned int sticksLeft, turn;
	for (unsigned int i = 0; i < ROUNDS; i++) {
		sticksLeft = MAX_STICKS;
		turn = i % PLAYERS; //alternate between who starts
		while (sticksLeft) {
			sticksLeft -= genNextMove(players[turn % PLAYERS], sticksLeft);
			turn++;
		}
		recordLoss(players[(turn + 1) % PLAYERS]);
		recordWin(players[turn % PLAYERS]);
	}
	/*for (unsigned int i = 0, j, k; i < PLAYERS; i++) {
		printf("Player %d\n", i + 1);
		for (j = 0; j < MAX_STICKS; j++) {
			printf("%u sticks: ", j + 1);
			for (k = 0; k < STICKS_PER_TURN; k++) printf("%u ", players[i]->strategy[j].frequency[k]);
			printf("\n");
		}
	}*/
	sticksLeft = MAX_STICKS;
	bool humanTurn = true;
	unsigned int sticksToRemove;
	while (sticksLeft) {
		printf("%u stick", sticksLeft);
		pluralize(sticksLeft, false);
		printf(" remain");
		pluralize(sticksLeft, true);
		printf(" on the table. ");
		if (humanTurn) {
			printf("How many do you want to take? ");
			while (true) {
				scanf("%u", &sticksToRemove);
				if (sticksToRemove > 0 && sticksToRemove < 4 && sticksLeft >= sticksToRemove) break;
				printf("I'm sorry, but you can't take %u. How many do you want to take? ", sticksToRemove);
			}
		}
		else {
			sticksToRemove = genNextMove(players[0], sticksLeft);
			printf("Computer takes %u stick", sticksToRemove);
			pluralize(sticksToRemove, false);
			puts(".");
		}
		sticksLeft -= sticksToRemove;
		humanTurn = !humanTurn;
	}
	if (humanTurn) puts("You won!"); //human won since computer played last
	else puts("Computer won."); //computer won
}