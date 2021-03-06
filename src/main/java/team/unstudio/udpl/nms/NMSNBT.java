package team.unstudio.udpl.nms;

import team.unstudio.udpl.nbt.NBTBase;
import team.unstudio.udpl.nbt.NBTTagByte;
import team.unstudio.udpl.nbt.NBTTagByteArray;
import team.unstudio.udpl.nbt.NBTTagCompound;
import team.unstudio.udpl.nbt.NBTTagDouble;
import team.unstudio.udpl.nbt.NBTTagFloat;
import team.unstudio.udpl.nbt.NBTTagInt;
import team.unstudio.udpl.nbt.NBTTagIntArray;
import team.unstudio.udpl.nbt.NBTTagList;
import team.unstudio.udpl.nbt.NBTTagLong;
import team.unstudio.udpl.nbt.NBTTagShort;
import team.unstudio.udpl.nbt.NBTTagString;

/**
 * NBT的转换接口
 */
public interface NMSNBT {
	
	/**
	 * 转换为Map
	 */
	public NBTTagCompound toMap(Object nbt) throws Exception;

	/**
	 * 转换为List
	 */
	public NBTTagList toList(Object nbt) throws Exception;

	/**
	 * 转换为Byte
	 */
	public NBTTagByte toByte(Object nbt) throws Exception;

	/**
	 * 转换为Short
	 */
	public NBTTagShort toShort(Object nbt) throws Exception;

	/**
	 * 转换为Int
	 */
	public NBTTagInt toInt(Object nbt) throws Exception;

	/**
	 * 转换为Long
	 */
	public NBTTagLong toLong(Object nbt) throws Exception;

	/**
	 * 转换为Float
	 */
	public NBTTagFloat toFloat(Object nbt) throws Exception;

	/**
	 * 转换为Double
	 */
	public NBTTagDouble toDouble(Object nbt) throws Exception;
	
	/**
	 * 转换为String
	 */
	public NBTTagString toString(Object nbt) throws Exception;
	
	/**
	 * 转换为ByteArray
	 */
	public NBTTagByteArray toByteArray(Object nbt) throws Exception;
	
	/**
	 * 转换为IntArray
	 */
	public NBTTagIntArray toIntArray(Object nbt) throws Exception;
	
	/**
	 * 转换为NBTBase
	 */
	public NBTBase toNBTBase(Object nbt) throws Exception;
	
	/**
	 * 转换为NMSNBT
	 */
	public Object toNBT(NBTBase nbt) throws Exception;
}
