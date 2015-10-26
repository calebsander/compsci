#include <stdio.h>
#include "playerLearning.h"

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
	for (unsigned int i = 0, j, k; i < PLAYERS; i++) {
		printf("Player %d\n", i + 1);
		for (j = 0; j < MAX_STICKS; j++) {
			printf("%u sticks: ", j + 1);
			for (k = 0; k < STICKS_PER_TURN; k++) printf("%u ", players[i]->strategy[j].frequency[k]);
			printf("\n");
		}
	}
}