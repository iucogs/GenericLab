package display;


public class VideoDisplay  extends Display{

	private String videoName;
	
	public VideoDisplay(String newVideoName)
	{
		setVideoName(newVideoName);
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getVideoName() {
		return videoName;
	}
	
}
