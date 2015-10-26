#define STICKS_PER_TURN 3

typedef struct {
	//For each possible number of sticks to take in a turn, the frequency that move should occur with
	unsigned int frequency[STICKS_PER_TURN];
} Strategy;
typedef struct {
	//The number of sticks that each round starts with on the board
	unsigned int maxSticks;
	//The strategy that the player is using
	Strategy *strategy;
	//For each possible number of sticks on the board, how many the player chose to take this round (or 0 if the player never had to pick)
	unsigned char *currentPicks;
} LearningPlayer;

LearningPlayer *newLearningPlayer(unsigned int maxSticks);
void recordWin(LearningPlayer *player);
void recordLoss(LearningPlayer *player);
unsigned char genNextMove(LearningPlayer *player, unsigned int sticksRemaining);