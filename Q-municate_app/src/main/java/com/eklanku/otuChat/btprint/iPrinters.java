package com.eklanku.otuChat.btprint;

public final class iPrinters
{
	public String s;
	public int i;
	
    private iPrinters(String s, int i)
    {
    	this.s = s;
    	this.i = i;
    	
    }

    public static iPrinters[] values()
    {
        iPrinters aiprinters[];
        int i;
        iPrinters aiprinters1[];
        System.arraycopy(aiprinters = ENUM$VALUES, 0, aiprinters1 = new iPrinters[i = aiprinters.length], 0, i);
        return aiprinters1;
    }

    public static iPrinters valueOf(String s)
    {
    	return null;
        
    }

    public static final iPrinters CITIZEN;
    public static final iPrinters EPSON;
    public static final iPrinters EXTECH;
    public static final iPrinters GENERIC_TEXT;
    public static final iPrinters GENERIC_PCL;
    public static final iPrinters HP;
    public static final iPrinters INTERMEC;
    public static final iPrinters ONEIL;
    public static final iPrinters RICOH;
    public static final iPrinters ZEBRA_CAMEO;
    private static final iPrinters ENUM$VALUES[];

    static 
    {
        CITIZEN = new iPrinters("CITIZEN", 0);
        EPSON = new iPrinters("EPSON", 1);
        EXTECH = new iPrinters("EXTECH", 2);
        GENERIC_TEXT = new iPrinters("GENERIC_TEXT", 3);
        GENERIC_PCL = new iPrinters("GENERIC_PCL", 4);
        HP = new iPrinters("HP", 5);
        INTERMEC = new iPrinters("INTERMEC", 6);
        ONEIL = new iPrinters("ONEIL", 7);
        RICOH = new iPrinters("RICOH", 8);
        ZEBRA_CAMEO = new iPrinters("ZEBRA_CAMEO", 9);
        ENUM$VALUES = (new iPrinters[] {
            CITIZEN, EPSON, EXTECH, GENERIC_TEXT, GENERIC_PCL, HP, INTERMEC, ONEIL, RICOH, ZEBRA_CAMEO
        });
    }
}