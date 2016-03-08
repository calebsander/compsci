#ifndef __POSITION_H__
	#define __POSITION_H__
	typedef struct position Position;

	void setDimensions(unsigned int height, unsigned int width);
	Position *parsePosition(char *positionString);
#endif