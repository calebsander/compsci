typedef enum {
	DUMB,
	SMART,
	HUMAN
} PlayerType;
typedef struct {
	PlayerType type;
	char *name;
} Player;

PlayerType playerTypeFromString(char *type);

int genNextMove(Player *player, unsigned int sticksLeft);