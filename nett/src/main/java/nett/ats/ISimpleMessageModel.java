package nett.ats;

import mpe.app.IMem;

import java.util.List;

interface ISimpleMessageModel extends IMem {
	List<String> getPost_media_as_list();

	String getPost_text_newest();
}
