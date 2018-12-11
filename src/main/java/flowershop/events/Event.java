package flowershop.events;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

public class Event {
	private String title;
	private String text;
	private LocalDateTime createdTime;
	private LocalDateTime beginTime;
	private LocalDateTime endTime;
	private String id;

	public Event(String title, String text, LocalDateTime beginTime, LocalDateTime endTime) {
		this.title = title;
		this.createdTime = LocalDateTime.now();
		this.text = text;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.id = String.valueOf(this.hashCode()) +
				String.valueOf(createdTime.getNano()) + String.valueOf(createdTime.getSecond()) + String.valueOf(createdTime.getMinute())
				+ String.valueOf(createdTime.getHour()) + String.valueOf(createdTime.getDayOfMonth()) + String.valueOf(createdTime.getMonthValue())
				+ String.valueOf(createdTime.getYear());
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
		return createdTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
	}

	public String getFormatedBeginTime() {
		return beginTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
	}

	public String getFormatedEndTime() {
		return endTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
	}

	public LocalDateTime getBeginTime() {
		return beginTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public String getId() {
		return id;
	}
}
