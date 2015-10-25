#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include "player.h"

PlayerType playerTypeFromString(char *type) {
	if (!strcmp(type, "DUMB")) return DUMB;
	else if (!strcmp(type, "SMART")) return SMART;
	else if (!strcmp(type, "HUMAN")) return HUMAN;
	else {
		fprintf(stderr, "Invalid player type: %s\n", type);
		exit(EXIT_FAILURE);
	}
}

void pluralize(unsigned int count, bool invert) {
	if ((count > 1) ^ invert) putchar('s');
}

#define DUMB_STICKS_REMOVED 1
int genNextMove(Player *player, unsigned int sticksLeft) {
	printf("%u stick", sticksLeft);
	pluralize(sticksLeft, false);
	printf(" remain");
	pluralize(sticksLeft, true);
	printf(" on the table. ");
	unsigned int sticksToRemove;
	switch (player->type) {
		case DUMB:
			sticksToRemove = DUMB_STICKS_REMOVED;
			printf("%s takes %u stick", player->name, sticksToRemove);
			pluralize(sticksToRemove, false);
			putchar('\n');
			break;
		case SMART:
			srand(clock());
			if (sticksLeft == 1) sticksToRemove = 1;
			else if (sticksLeft % 4 == 1) sticksToRemove = rand() % 3 + 1; //if player is in a bad way, it doesn't matter
			else sticksToRemove = (sticksLeft + 3) % 4;
			printf("%s takes %u stick", player->name, sticksToRemove);
			pluralize(sticksToRemove, false);
			putchar('\n');
			break;
		case HUMAN:
			printf("%s", player->name);
			printf(", how many do you want to take? ");
			while (true) {
				scanf("%u", &sticksToRemove);
				if (sticksToRemove > 0 && sticksToRemove < 4 && sticksLeft >= sticksToRemove) break;
				printf("I'm sorry, %s, but you can't take %u. How many do you want to take? ", player->name, sticksToRemove);
			}
	}
	return sticksToRemove;
}