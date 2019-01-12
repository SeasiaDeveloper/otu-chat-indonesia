package com.eklanku.otuChat.btprint;

public final class iPaperType {
	public String s;
	public int i;

	private iPaperType(String s, int i) {
		this.s = s;
		this.i = i;
	}

	public static iPaperType[] values() {
		iPaperType aipapertype[];
		int i;
		iPaperType aipapertype1[];
		System.arraycopy(aipapertype = ENUM$VALUES, 0, aipapertype1 = new iPaperType[i = aipapertype.length], 0, i);
		return aipapertype1;
	}

	public static iPaperType valueOf(String s) {
		return null;
	}

	public static final iPaperType A3;
	public static final iPaperType A4;
	public static final iPaperType A5;
	public static final iPaperType A6;
	public static final iPaperType B5;
	public static final iPaperType C5;
	public static final iPaperType COMMERCIAL_10;
	public static final iPaperType CUSTOM;
	public static final iPaperType EXECUTIVE;
	public static final iPaperType INTERNATIONAL_C5;
	public static final iPaperType INTERNATIONAL_DL;
	public static final iPaperType LEGAL;
	public static final iPaperType LETTER;
	public static final iPaperType MONARCH;
	public static final iPaperType THERMAL;
	private static final iPaperType ENUM$VALUES[];

	static {
		A3 = new iPaperType("A3", 0);
		A4 = new iPaperType("A4", 1);
		A5 = new iPaperType("A5", 2);
		A6 = new iPaperType("A6", 3);
		B5 = new iPaperType("B5", 4);
		C5 = new iPaperType("C5", 5);
		COMMERCIAL_10 = new iPaperType("COMMERCIAL_10", 6);
		CUSTOM = new iPaperType("CUSTOM", 7);
		EXECUTIVE = new iPaperType("EXECUTIVE", 8);
		INTERNATIONAL_C5 = new iPaperType("INTERNATIONAL_C5", 9);
		INTERNATIONAL_DL = new iPaperType("INTERNATIONAL_DL", 10);
		LEGAL = new iPaperType("LEGAL", 11);
		LETTER = new iPaperType("LETTER", 12);
		MONARCH = new iPaperType("MONARCH", 13);
		THERMAL = new iPaperType("THERMAL", 14);
		ENUM$VALUES = (new iPaperType[] { A3, A4, A5, A6, B5, C5, COMMERCIAL_10, CUSTOM, EXECUTIVE, INTERNATIONAL_C5,
				INTERNATIONAL_DL, LEGAL, LETTER, MONARCH, THERMAL });
	}
}