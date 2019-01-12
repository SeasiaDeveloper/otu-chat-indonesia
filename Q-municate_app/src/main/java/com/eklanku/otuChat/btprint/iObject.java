package com.eklanku.otuChat.btprint;

public final class iObject {

	public iObject() {
		_line = "";
		_text = null;
		_object = true;
		_rotation = "";
		_coordinates = "";
		_pci = "";
		_fontSize = "";
		_column = "";
		_color = "";
		_data = null;
		_doubleUnderline = false;
		_underline = false;
		_italic = false;
		_bold = false;
		initiateVariables();
	}

	private void initiateVariables() {
		_line = "";
		_text = new StringBuilder();
		_rotation = "";
		_coordinates = "";
		_pci = "";
		_fontSize = "";
		_column = "";
		_color = "";
		_data = new StringBuilder();
		_doubleUnderline = false;
		_underline = false;
		_italic = false;
		_bold = false;
	}

	private void HPVectorGL2() {
		_data.append("\033%0B");
		_data.append("IN;");
		_data.append("SP1;");
	}

	protected String GetColor() {
		return _color;
	}

	protected void SetDefaultColor() {
		_color = "\033*r-3U\033*v7S";
	}

	public void Underline() {
		_underline = true;
	}

	protected boolean GetUnderline() {
		return _underline;
	}

	protected void SetUnderline() {
		_underline = false;
	}

	public void UnderlineDouble() {
		_doubleUnderline = true;
	}

	protected boolean GetUnderlineDouble() {
		return _doubleUnderline;
	}

	protected void SetUnderlineDouble() {
		_doubleUnderline = false;
	}

	public void Italic() {
		_italic = true;
	}

	protected boolean GetItalic() {
		return _italic;
	}

	protected void SetItalic() {
		_italic = false;
	}

	public void Bold() {
		_bold = true;
	}

	protected boolean GetBold() {
		return _bold;
	}

	protected void SetBold() {
		_bold = false;
	}

	public void CharactersPerInch(int CPI) {
		_pci = (new StringBuilder("\033(s")).append(CPI).append("H").toString();
	}

	protected String GetCharacterPerInch() {
		return _pci;
	}

	public void FontSize(double Size) {
		_fontSize = (new StringBuilder("\033(s")).append(Size).append("V").toString();
	}

	protected String GetFontSize() {
		return _fontSize;
	}

	public void Column(int Column) {
		_column = (new StringBuilder("\033&a")).append(Column).append("C").toString();
	}

	protected String GetColumn() {
		return _column;
	}

	public void Coordinates(int X, int Y) {
		_coordinates = (new StringBuilder("\033*p")).append(X).append("x").append(Y).append("Y").toString();
	}

	public String GetCoordinates() {
		return _coordinates;
	}

	public void DrawLine(int Width, int Long) {
		_line = (new StringBuilder("\033*c")).append(Long).append("a").append(Width).append("b5P").toString();
	}

	protected String GetLine() {
		return _line;
	}

	protected void SetLine() {
		_line = "";
	}

	public void Text(String Text) {
		_text.append(Text + "\n");
	}

	protected String GetText() {
		return _text.toString();
	}

	protected void SetText() {
		_text = new StringBuilder();
	}

	public void NextLine() {
		_text.append("\r\n");
	}

	protected String GetRotation() {
		return _rotation;
	}

	public void DrawLine(int X1, int Y1, int X2, int Y2) {
		_data = new StringBuilder();
		HPVectorGL2();
		_data.append((new StringBuilder("PA")).append(X1).append(",").append(Y1).append(";").toString());
		_data.append((new StringBuilder("PD")).append(X1).append(",").append(Y1).append(",").append(X2).append(",")
				.append(Y2).append(";").toString());
		_data.append((new StringBuilder()).append(_text).append("\033%0A").toString());
		_line = _data.toString();
	}

	public void DrawCircle(int X1, int Y1, int Radius) {
		_data = new StringBuilder();
		HPVectorGL2();
		_data.append((new StringBuilder("PA")).append(X1).append(",").append(Y1).append(";").toString());
		_data.append((new StringBuilder("CI")).append(Radius).append(";").toString());
		_data.append("\033%0A");
		_line = _data.toString();
	}

	public void DrawArc(int StartPoint_X, int StartPoint_Y, int PivotPoint_X, int PivotPoint_Y, int Degrees) {
		_data = new StringBuilder();
		HPVectorGL2();
		_data.append(
				(new StringBuilder("PA")).append(StartPoint_X).append(",").append(StartPoint_Y).append(";").toString());
		_data.append((new StringBuilder("AA")).append(PivotPoint_X).append(",").append(PivotPoint_Y).append(",")
				.append(Degrees).append(";").toString());
		_data.append("\033%0A");
		_line = _data.toString();
	}

	public void DrawBeizer(int X1, int Y1, int X2, int Y2, int X3, int Y3, int X4, int Y4) {
		_data = new StringBuilder();
		HPVectorGL2();
		_data.append((new StringBuilder("PA")).append(X1).append(",").append(Y1).append(";PD;").toString());
		_data.append((new StringBuilder("BZ")).append(X2).append(",").append(Y2).append(",").append(X3).append(",")
				.append(Y3).append(",").append(X4).append(",").append(Y4).append(";").toString());
		_data.append("\033%0A");
		_line = _data.toString();
	}

	public void DrawRectangle(int X1, int Y1, int X2, int Y2) {
		_data = new StringBuilder();
		HPVectorGL2();
		_data.append((new StringBuilder("PA")).append(X1).append(",").append(Y1).append(";").toString());
		_data.append((new StringBuilder("EA")).append(X2).append(",").append(Y2).append(";").toString());
		_data.append("\033%0A");
		_line = _data.toString();
	}

	private String _line;
	protected StringBuilder _text;
	protected boolean _object;
	private String _rotation;
	private String _coordinates;
	private String _pci;
	private String _fontSize;
	private String _column;
	private String _color;
	private StringBuilder _data;
	private boolean _doubleUnderline;
	private boolean _underline;
	private boolean _italic;
	private boolean _bold;
}