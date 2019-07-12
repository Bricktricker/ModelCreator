package com.mrcrayfish.modelcreator;

/**
 * Author: MrCrayfish
 */
public enum PropertyIdentifier
{
	SIZE_X(0),
	SIZE_Y(1),
    SIZE_Z(2),
    POS_X(3),
    POS_Y(4),
    POS_Z(5),
    ORIGIN_X(6),
    ORIGIN_Y(7),
    ORIGIN_Z(8),
    START_U(9),
    START_V(10),
    END_U(11),
    END_V(12),
	
	UNDEFINED(-1);
	
	private int id;
	
	private PropertyIdentifier(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
}
