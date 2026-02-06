package nett.ats;

import mpe.app_model.IMem;

import java.util.List;

interface ISimplePostModel extends IMem {
	List<String> getPost_media_as_list();

	String getPost_text_newest();
}
