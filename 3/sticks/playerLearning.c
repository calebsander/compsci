#include <stdlib.h>
#include <time.h>
#include "playerLearning.h"

void clearCurrentPicks(LearningPlayer *player) {
	unsigned int maxSticks = player->maxSticks;
	for (int i = 0; i < maxSticks; i++) player->currentPicks[i] = 0;
}
unsigned int atLeastOne(unsigned int input) {
	if (input > 1) return input;
	else return 1;
}

LearningPlayer *newLearningPlayer(unsigned int maxSticks) {
	srand(time(NULL));
	LearningPlayer *player = malloc(sizeof(*player));
	player->maxSticks = maxSticks;
	player->strategy = malloc(sizeof(*(player->strategy)) * maxSticks);
	player->currentPicks = malloc(sizeof(*(player->currentPicks)) * maxSticks);
	clearCurrentPicks(player);
	for (unsigned int i = 0, j; i < maxSticks; i++) {
		for (j = 0; j < STICKS_PER_TURN; j++) player->strategy[i].frequency[j] = 1;
	}
	return player;
}
void recordWin(LearningPlayer *player) {
	unsigned char currentPick;
	for (int i = 0; i < player->maxSticks; i++) {
		if ((currentPick = player->currentPicks[i])) player->strategy[i].frequency[currentPick - 1]++;
	}
	clearCurrentPicks(player);
}
void recordLoss(LearningPlayer *player) {
	unsigned char currentPick;
	for (int i = 0; i < player->maxSticks; i++) {
		if ((currentPick = player->currentPicks[i])) player->strategy[i].frequency[currentPick - 1] = atLeastOne(player->strategy[i].frequency[currentPick - 1] - 1);
	}
	clearCurrentPicks(player);
}
unsigned char genNextMove(LearningPlayer *player, unsigned int sticksLeft) {
	unsigned char stickPossibilities = STICKS_PER_TURN;
	if (stickPossibilities > sticksLeft) stickPossibilities = sticksLeft;
	Strategy currentStrategy = player->strategy[sticksLeft - 1];
	unsigned int possibilitySum = 0;
	for (int i = 0; i < stickPossibilities; i++) possibilitySum += currentStrategy.frequency[i];
	unsigned int randomPick = rand() % (possibilitySum);
	possibilitySum = 0;
	for (int i = 0; i < stickPossibilities; i++) {
		possibilitySum += currentStrategy.frequency[i];
		if (randomPick < possibilitySum) return player->currentPicks[sticksLeft - 1] = i + 1;
	}
	return 0; //should never happen
}
void freePlayer(LearningPlayer *player) {
	free(player->strategy);
	free(player->currentPicks);
	free(player);
}