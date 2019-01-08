package flowershop.events;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static java.time.format.FormatStyle.MEDIUM;

@Entity
public class Event {
	private String title;
	@Lob
	@Column(name="CONTENT", length=4096)
	private String text;
	private LocalDateTime createdTime;
	private LocalDateTime beginTime;
	private LocalDateTime endTime;
	private @Id
	@GeneratedValue
	long id;
	private boolean isPrivate;

	@SuppressWarnings("unused")
	private Event() {
	}

	public Event(String title, String text, LocalDateTime beginTime, int duration) {
		this.isPrivate=false;
		this.title = title;
		this.createdTime = LocalDateTime.now();
		this.text = text;
		this.beginTime = LocalDateTime.of(beginTime.getYear(), beginTime.getMonthValue(), beginTime.getDayOfMonth(),0,0);
		this.endTime = this.beginTime.plusDays(duration);
	}
	public Event(String title, String text, LocalDateTime beginTime, LocalDateTime endTime) {
		this.isPrivate=false;
		this.title = title;
		this.createdTime = LocalDateTime.now();
		this.text = text;
		this.beginTime = LocalDateTime.of(beginTime.getYear(), beginTime.getMonthValue(), beginTime.getDayOfMonth(),0,0);
		this.endTime = LocalDateTime.of(endTime.getYear(), endTime.getMonthValue(), endTime.getDayOfMonth(),23,59);
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return title;
	}

	public String getFormatedCreatedTime() {
		return createdTime.format(DateTimeFormatter.ofLocalizedDateTime(MEDIUM));
	}

	public String getFormatedBeginTime() {
		return beginTime.format(DateTimeFormatter.ofLocalizedDate(MEDIUM));
	}

	public String getFormatedEndTime() {
		return endTime.format(DateTimeFormatter.ofLocalizedDate(MEDIUM));
	}

	public LocalDateTime getBeginTime() {
		return beginTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public long getId() {
		return id;
	}

	public boolean getIsPrivate(){
		return isPrivate;
	}

	public void setTitle(String title){
		this.title = title;
	}
	public void setText(String text){
		this.text = text;
	}
	public void setBeginTime(LocalDateTime beginTime){
		this.beginTime = beginTime;
	}
	public void setEndTime(LocalDateTime endTime){
		this.endTime = endTime;
	}

	public void setPrivate(boolean isPrivate){
		this.isPrivate = isPrivate;
	}

	public ArrayList<String> getTextLines(){
		String[] arrayLines = text.split("\r\n|\r|\n");
		ArrayList<String> lines = new ArrayList<>();
		for(int i=0;i<arrayLines.length;i++){
			lines.add(arrayLines[i]);
		}
		return lines;
	}
}
