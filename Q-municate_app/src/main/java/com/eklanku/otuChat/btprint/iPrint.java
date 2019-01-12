package com.eklanku.otuChat.btprint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.io.DataOutputStream;

public final class iPrint {
	private class btPrinting {
		public String ErrorMessage() {
			return _errorMessage;
		}

		private String _errorMessage;
		private StringBuilder _printData;

		public btPrinting(iConnection Connection, StringBuilder Data, int Copies) {
			super();
			_errorMessage = "";
			try {
				btConnection btc = (btConnection) Connection;
				if (!btc.isConnected())
					btc.Connect();
				if (btc.isConnected()) {
					DataOutputStream _out = null;
					_printData = new StringBuilder();
					_printData.append(Data);
					for (int i = 0; i <= Copies - 1; i++) {
						_out = new DataOutputStream(btc._printerSocket.getOutputStream());
						_out.writeBytes(_printData.toString());
						_out.flush();
					}

					_out = null;
				} else {
					_errorMessage = "Can't connect to the Printer.";
				}
			} catch (Exception e) {
				e.printStackTrace();
				_errorMessage = e.getMessage();
			}
		}
	}

	public iPrint(Context AppContext) {
		_data = null;
		_errorMessage = "";
		_color = "";
		_pageColor = "";
		_settings = null;
		_copies = 1;
		_status = 0;
		_bold = false;
		_italic = false;
		_underline = false;
		_doubleUnderline = false;
		_start = false;
		_end = false;
		_isDemo = true;
		_isDemo = true;
		_data = new StringBuilder();
	}

	private void setVariables() {
		if (_end && _data.length() > 0)
			_data = new StringBuilder();
		_errorMessage = "";
		_bold = false;
		_italic = false;
		_underline = false;
		_doubleUnderline = false;
		_start = false;
		_end = false;
		if (_isDemo) {
		}
	}

	public String ErrorMessage() {
		return _errorMessage;
	}

	public void Settings(iSettings Settings) {
		_settings = Settings;
		PaperType(Settings.GetPaperType());
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter()))
			_settings._formFeed = "\f";
		else
			_settings._formFeed = "";
	}

	public int Status() {
		return _status;
	}

	public void Copies(int Copies) {
		_copies = Copies;
	}

	public void Connection(iConnection Connection) {
		_connection = Connection;
	}

	private void PaperType(iPaperType Type) {
		String _type = "";
		if (Type == iPaperType.A3 && _settings.GetPrinter() == iPrinters.HP)
			_type = "\033&l27A";
		if (Type == iPaperType.A4 && _settings.GetPrinter() == iPrinters.HP)
			_type = "\033&l26A";
		if (Type == iPaperType.A5 && _settings.GetPrinter() == iPrinters.HP)
			_type = "\033&l4A";
		if (Type == iPaperType.A6 && _settings.GetPrinter() == iPrinters.HP)
			_type = "\033&27A";
		if (Type == iPaperType.B5 && _settings.GetPrinter() == iPrinters.HP)
			_type = "\033&l100A";
		if (Type == iPaperType.C5 && _settings.GetPrinter() == iPrinters.HP)
			_type = "\033&l91A";
		if (Type == iPaperType.COMMERCIAL_10 && _settings.GetPrinter() == iPrinters.HP)
			_type = "\033&l81A";
		if (Type == iPaperType.CUSTOM && _settings.GetPrinter() == iPrinters.HP)
			_type = "\033&l101A";
		if (Type == iPaperType.EXECUTIVE && _settings.GetPrinter() == iPrinters.HP)
			_type = "\033&l1A";
		if (Type == iPaperType.INTERNATIONAL_C5 && _settings.GetPrinter() == iPrinters.HP)
			_type = "\033&l91A";
		if (Type == iPaperType.INTERNATIONAL_DL && _settings.GetPrinter() == iPrinters.HP)
			_type = "\033&l90A";
		if (Type == iPaperType.LEGAL && _settings.GetPrinter() == iPrinters.HP)
			_type = "\033&l3A";
		if (Type == iPaperType.LETTER && _settings.GetPrinter() == iPrinters.HP)
			_type = "\033&l2A";
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter()))
			_data.append(_type);
	}

	public void NextLine() {
		_data.append("\r\n");
	}

	public void NewPage() {
		_data.append(_settings._formFeed);
	}

	public void Start(String strNamaPrinter) {
		_status = 0;
		if (!_start) {
			setVariables();
			_start = true;
			if (!_settings._thermalPrinters.contains(_settings.GetPrinter())) {
				_settings._formFeed = "\f";
				if (_settings.GetPrinter() == iPrinters.HP)
					_data.append("\033%-12345X");
				ResetPrinter();
			} else {
				_settings._formFeed = "";
			}

			if (strNamaPrinter.equals("Zonerich") || strNamaPrinter.equals("RNF/BKT58")) {
				byte[] CMD_LOGO_AB = { 0x1C, 0x70, 0x01, 0x30 };
				_data.append((char) CMD_LOGO_AB[0]);
				_data.append((char) CMD_LOGO_AB[1]);
				_data.append((char) CMD_LOGO_AB[2]);
				_data.append((char) CMD_LOGO_AB[3]);
			} else if (strNamaPrinter.equals("Sunphor")) {
				byte[] CMD_LOGO_SP58 = { 0x1F, 0x43, 0x02, 0x00, 0x0D };
				_data.append((char) CMD_LOGO_SP58[0]);
				_data.append((char) CMD_LOGO_SP58[1]);
				_data.append((char) CMD_LOGO_SP58[2]);
				_data.append((char) CMD_LOGO_SP58[3]);
				_data.append((char) CMD_LOGO_SP58[4]);
			} else if (strNamaPrinter.equals("Blue Bamboo P25")) {
				byte[] CMD_LOGO_P25 = { 0x1B, 0x66, 0x00 };
				_data.append((char) CMD_LOGO_P25[0]);
				_data.append((char) CMD_LOGO_P25[1]);
				_data.append((char) CMD_LOGO_P25[2]);
			}
		}
	}

	public void End() {
		if (!_end) {
			if (!_settings._thermalPrinters.contains(_settings.GetPrinter())) {
				ResetPrinter();
				if (_settings.GetPrinter() == iPrinters.HP || _settings.GetPrinter() == iPrinters.GENERIC_PCL)
					_data.append("\033%-12345X");
				else
					NewPage();
			}
			_end = true;
		}
	}

	public void Line(int Line) {
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter()) && _settings.GetPrinter() == iPrinters.HP)
			_data.append((new StringBuilder("\033&a")).append(Line).append("R").toString());
	}

	public void Column(int Column) {
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter()) && _settings.GetPrinter() == iPrinters.HP)
			_data.append((new StringBuilder("\033&a")).append(Column).append("C").toString());
	}

	public void BottonMargin(int Lines) {
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter()) && _settings.GetPrinter() == iPrinters.HP)
			_data.append((new StringBuilder("\033&l")).append(Lines).append("F").toString());
	}

	public void TopMargin(int Lines) {
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter()) && _settings.GetPrinter() == iPrinters.HP)
			_data.append((new StringBuilder("\033&l")).append(Lines).append("E").toString());
	}

	public void ResetPrinter() {
		try {
			if (_settings.GetPrinter() == iPrinters.HP)
				_data.append("\033E");
			if (_settings.GetPrinter() == iPrinters.EPSON)
				_data.append("\033@");
		} catch (Exception exception) {
		}
	}

	public void UnderlineDouble() {
		_doubleUnderline = !_doubleUnderline;
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter())) {
			if (_doubleUnderline && _settings.GetPrinter() == iPrinters.HP)
				_data.append("\033&d2D");
			if (!_doubleUnderline && _settings.GetPrinter() == iPrinters.HP)
				_data.append("\033&d@");
		}
	}

	public void Underline() {
		_underline = !_underline;
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter())) {
			if (_underline && _settings.GetPrinter() == iPrinters.HP)
				_data.append("\033&d1D");
			if (!_underline && _settings.GetPrinter() == iPrinters.HP)
				_data.append("\033&d@");
		}
	}

	public void Italic() {
		_italic = !_italic;
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter())) {
			if (_italic && _settings.GetPrinter() == iPrinters.HP)
				_data.append("\033(s1S");
			if (!_italic && _settings.GetPrinter() == iPrinters.HP)
				_data.append("\033(s0S");
		}
	}

	public void Bold() {
		_bold = !_bold;
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter())) {
			if (_bold && _settings.GetPrinter() == iPrinters.HP)
				_data.append("\033(s3B");
			if (!_bold && _settings.GetPrinter() == iPrinters.HP)
				_data.append("\033(s0B");
		}
	}

	public void LeftMargin(int Columns) {
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter()) && _settings.GetPrinter() == iPrinters.HP)
			_data.append((new StringBuilder("\033&a")).append(Columns).append("F").toString());
	}

	public void RightMargin(int Columns) {
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter()) && _settings.GetPrinter() == iPrinters.HP)
			_data.append((new StringBuilder("\033&a")).append(Columns).append("M").toString());
	}

	public void CharactersPerInch(int CPI) {
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter()) && _settings.GetPrinter() == iPrinters.HP)
			_data.append((new StringBuilder("\033(s")).append(CPI).append("H").toString());
	}

	public void FontSize(double Size) {
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter()) && _settings.GetPrinter() == iPrinters.HP)
			_data.append((new StringBuilder("\033(s")).append(Size).append("V").toString());
	}

	public void Clear() {
		_data = new StringBuilder();
		_end = false;
		_start = false;
	}

	public void Add(iObject Object) {
		_data.append(Object.GetCoordinates());
		_data.append(Object.GetColumn());
		_data.append(Object.GetFontSize());
		_data.append(Object.GetCharacterPerInch());
		if (Object.GetBold() && !_bold)
			Bold();
		if (Object.GetItalic() && !_italic)
			Italic();
		if (Object.GetUnderline() && !_underline)
			Underline();
		if (Object.GetUnderlineDouble() && !_doubleUnderline)
			UnderlineDouble();
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter()) && Object.GetColor() != _color) {
			_pageColor = _color;
			if (_settings.GetPrinter() == iPrinters.HP)
				_data.append(Object.GetColor());
			_color = _pageColor;
		}
		if (Object.GetText().length() > 0) {
			_data.append(Object.GetText());
			Object.SetText();
			NextLine();
		}
		if (Object.GetLine().length() > 0) {
			_data.append(Object.GetLine());
			Object.SetLine();
			NextLine();
		}
		if (Object.GetBold()) {
			Bold();
			Object.SetBold();
		}
		if (Object.GetItalic()) {
			Italic();
			Object.SetItalic();
		}
		if (Object.GetUnderline()) {
			Underline();
			Object.SetUnderline();
		}
		if (Object.GetUnderlineDouble()) {
			UnderlineDouble();
			Object.SetUnderlineDouble();
		}
		Object.SetDefaultColor();
		if (!_settings._thermalPrinters.contains(_settings.GetPrinter()) && _settings.GetPrinter() == iPrinters.HP)
			_data.append(_color);
	}

	protected void ShowAlertDialog(Context AppContext, String Message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(AppContext);
		builder.setMessage(Message).setCancelable(false).setNegativeButton("OK",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}

					{
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void Print() {
		print(_connection, _data, _copies);
		Clear();
	}

	private void print(iConnection Connection, StringBuilder Data, int Copies) {
		if (Connection instanceof btConnection) {
			_status = 1;
			btPrinting btp = new btPrinting(_connection, Data, Copies);
			_errorMessage = btp.ErrorMessage();
			_status = 2;
		}
	}

	private StringBuilder _data;
	private String _errorMessage;
	private String _color;
	private String _pageColor;
	private iSettings _settings;
	private iConnection _connection;
	private int _copies;
	private int _status;
	private boolean _bold;
	private boolean _italic;
	private boolean _underline;
	private boolean _doubleUnderline;
	private boolean _start;
	private boolean _end;
	private boolean _isDemo;
}