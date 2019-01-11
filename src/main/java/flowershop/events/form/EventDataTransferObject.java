package flowershop.events.form;

import flowershop.events.form.validation.IsInteger;
import flowershop.events.form.validation.ValidLocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Transfer object for {@link flowershop.events.Event} data.
 *
 * @author Cornelius Kummer
 */
public class EventDataTransferObject {

	@NotNull(message = "{Dto.title.NotEmpty}", groups = {CreateEvent.class, EditEvent.class})
	@NotEmpty(message = "{Dto.title.NotEmpty}", groups = {CreateEvent.class, EditEvent.class})
	private String title;

	@NotNull(message = "{Dto.description.NotEmpty}", groups = {CreateEvent.class, EditEvent.class})
	@NotEmpty(message = "{Dto.description.NotEmpty}", groups = {CreateEvent.class, EditEvent.class})
	private String text;

	@IsInteger(message = "{Dto.days.Invalid}", groups = {CreateEvent.class})
	@NotNull(message = "{Dto.begin.NotEmpty}", groups = {CreateEvent.class})
	@NotEmpty(message = "{Dto.begin.NotEmpty}", groups = {CreateEvent.class})
	private String begin;

	@IsInteger(message = "{Dto.days.Invalid}", groups = {CreateEvent.class})
	@NotNull(message = "{Dto.duration.NotEmpty}", groups = {CreateEvent.class})
	@NotEmpty(message = "{Dto.duration.NotEmpty}", groups = {CreateEvent.class})
	private String duration;

	@ValidLocalDateTime(groups = {EditEvent.class})
	@NotNull(message = "{Dto.begin.NotEmpty}", groups = {EditEvent.class})
	@NotEmpty(message = "{Dto.begin.NotEmpty}", groups = {EditEvent.class})
	private String beginDate;

	@ValidLocalDateTime(groups = {EditEvent.class})
	@NotNull(message = "{Dto.end.NotEmpty}", groups = {EditEvent.class})
	@NotEmpty(message = "{Dto.end.NotEmpty}", groups = {EditEvent.class})
	private String endDate;

	@NotNull(message = "{Dto.private.NotEmpty}", groups = {CreateEvent.class})
	private String priv;

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public String getBegin() {
		return begin;
	}

	public String getDuration() {
		return duration;
	}

	public String getBeginDate() {
		return beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getPriv() {
		return priv;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setBegin(String begin) {
		this.begin = begin;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}

	public interface CreateEvent {
	}

	public interface EditEvent {
	}

}
