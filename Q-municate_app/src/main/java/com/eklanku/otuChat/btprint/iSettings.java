package com.eklanku.otuChat.btprint;

import java.util.ArrayList;

public class iSettings {
	public iSettings() {
		_thermalPrinters = new ArrayList<iPrinters>();
		_thermalPrinters.clear();
		_thermalPrinters.add(iPrinters.ZEBRA_CAMEO);
		_thermalPrinters.add(iPrinters.ONEIL);
		_thermalPrinters.add(iPrinters.CITIZEN);
		_thermalPrinters.add(iPrinters.INTERMEC);
		_thermalPrinters.add(iPrinters.EXTECH);
		_printerType = iPrinters.GENERIC_TEXT;
		_grayScale = 0;
	}

	public void PrinterType(iPrinters printerType) {
		_printerType = printerType;
	}

	protected iPrinters GetPrinter() {
		return _printerType;
	}

	public void SetPaperType(iPaperType paperType) {
		_paperType = paperType;
	}

	public void SetGrayScale(boolean GrayScale) {
		if (GrayScale)
			_grayScale = 1;
		else
			_grayScale = 0;
	}

	protected int GetGrayScale() {
		return _grayScale;
	}

	protected iPaperType GetPaperType() {
		return _paperType;
	}

	private iPrinters _printerType;
	private iPaperType _paperType;
	private int _grayScale;
	protected String _formFeed;
	protected ArrayList<iPrinters> _thermalPrinters;

}
