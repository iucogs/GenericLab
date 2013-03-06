package display;

public class TextDisplay extends Display {

	private String text;
	
	public TextDisplay(String newText)
	{
		this.text = newText;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
}
