package udav_gst;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.RequiredArgsConstructor;
import mpe.core.UBool;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.QDate;
import mpu.str.Hu;
import mpu.str.JOIN;
import org.jetbrains.annotations.NotNull;
import udav_net.bincall.GsvBin;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Post implements Comparable<Post> {
	final JsonObject el;
	public static final String DATE_ICO = "⏱";//⏱
	public static final String LIKES_ICO = "\uD83E\uDE77";//🩷
	public static final String REPOSTS_ICO = "\uD83D\uDD01";//🔁
	public static final String COMMENTS_ICO = "\uD83D\uDCAC";//💬
	public static final String VIEWS_ICO = "\uD83D\uDC41";//👁
	public static final String PHOTO_ICO = "\uD83D\uDCF8";//📸 📷

	@Override
	public String toString() {
		return X.f("Post#https://vk.com/wall%s_%s %s %s %s%s R%s AD%s", getOwnerId(), getPostId(), //
				getPublishDate().f(QDate.F.MONO15NF_ICO), //
				toStringLRCV(getCountLikes(), getCountReposts(), getCountsComments(), getCountViews()), //
				PHOTO_ICO, X.sizeOf(getAttachsPhoto()), //
				UBool.toPlusMinus(isRepost()),//
				UBool.toPlusMinus(isAd()) //
		);
	}

	public static String toStringLRCV(int likes, int reposts, int comments, int views) {
		return X.f("%s%s %s%s %s%s %s%s", LIKES_ICO, Hu.K1(likes), REPOSTS_ICO, Hu.K1(reposts), COMMENTS_ICO, Hu.K1(comments), VIEWS_ICO, Hu.K1(views));
	}

	public boolean isRepost() {
		return el.get("copy_history") != null;
	}

	public String toStringFull() {
		return X.f("Post#https://vk.com/wall%s_%s %s%s %s%s %s%s %s%s %s%s, \n%s%s", getOwnerId(), getPostId(), //
				DATE_ICO, getPublishDate().f(QDate.F.MONO20NF), //
				LIKES_ICO, getCountLikes(), REPOSTS_ICO, getCountReposts(), COMMENTS_ICO, getCountsComments(), VIEWS_ICO, getCountViews(), //
				PHOTO_ICO, JOIN.allByNL(getAttachsPhoto()) //
		);
	}

	public static Post of(JsonElement json) {
		return new Post((JsonObject) json);
	}

	@Override
	public int compareTo(@NotNull Post post) {
		return post.getPostId().compareTo(post.getPostId());
	}

	@RequiredArgsConstructor
	public static class Photo {
		final JsonElement jel;

		public static Photo of(JsonElement el) {
			return new Photo(el);
		}

		public String getUrl() {
			return jel.getAsJsonObject().getAsJsonPrimitive("url").getAsString();
		}

		@Override
		public String toString() {
			return X.f("PostPhoto(Url=%s)", getUrl());
		}
	}

	public List<Photo> getAttachsPhoto() {
		JsonArray attachments = el.getAsJsonArray("attachments");
		if (attachments == null || attachments.size() == 0) {
			return ARR.EMPTY_LIST;
		}
		List els = new ArrayList(attachments.size());
		for (JsonElement attachment : attachments) {
			JsonObject joAttach = attachment.getAsJsonObject();
			String type = joAttach.getAsJsonPrimitive("type").getAsString();
			switch (type) {
				case "photo": {
					JsonObject photo = joAttach.getAsJsonObject("photo");
					JsonArray sizes = photo.getAsJsonArray("sizes");
//						if (sizes.size() < 1) {
//							return ARR.EMPTY_LIST;
//						}
					JsonElement last = sizes.get(sizes.size() - 1);
					els.add(Photo.of(last));
					continue;
				}

				default:
					continue;
			}

		}
		return els;
	}

	public QDate getPublishDate() {
		return QDate.ofEpoch(getAsInt("date"));
	}

	public boolean isAd() {
		int markedAsAds = getAsInt("marked_as_ads", 0);
		return markedAsAds != 0;
	}

	public int getCountsComments() {
		return getCount("comments");
	}

	public int getCountLikes() {
		return getCount("likes");
	}

	public int getCountViews() {
		return getCount("views");
	}

	public int getCountReposts() {
		return getCount("reposts");
	}

	public Integer getPostId(Integer... defRq) {
		return getAsInt("id", defRq);
	}

	public Integer getFromId(Integer... defRq) {
		return getAsInt("from_id", defRq);
	}

	public Integer getOwnerId(Integer... defRq) {
		return getAsInt("owner_id", defRq);
	}

	public String getText(String... defRq) {
		return getAsString("text", defRq);
	}

	public String getPostType(String... defRq) {
		return getAsString("post_type", defRq);
	}

	//
	//

	public String getAsString(String key, String... defRq) {
		JsonPrimitive je = el.getAsJsonPrimitive(key);
		if (je != null) {
			String asString = je.getAsString();
			if (X.notEmpty(asString)) {
				return asString;
			}
			return ARG.throwMsg(() -> X.f("Except not empty simple type [STRING] by key '%s'", key), defRq);
		}
		return ARG.throwMsg(() -> X.f("Except simple type [STRING] by key '%s'", key), defRq);
	}

	public Integer getAsInt(String key, Integer... defRq) {
		JsonPrimitive je = el.getAsJsonPrimitive(key);
		if (je != null) {
			Integer asint = je.getAsInt();
			if (asint != null) {
				return asint;
			}
			return ARG.throwMsg(() -> X.f("Except not empty simple type [INT] by key '%s'", key), defRq);
		}
		return ARG.throwMsg(() -> X.f("Except simple type [INT] by key '%s'", key), defRq);
	}

	public int getCount(String key) {
		JsonObject views = el.getAsJsonObject(key);
		if (views == null) {
			return 0;
		}
		JsonPrimitive count = views.getAsJsonPrimitive("count");
		return count == null ? 0 : count.getAsInt();
	}
}
