package udav_gst;

import com.google.gson.JsonElement;
import mpf.zbin.ZBin;
import mpe.cmsg.std.MsvCallMsg;
import mpu.X;
import mpu.core.ARRi;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Posts {

	final List<Post> posts;

	public static Posts loadByGA0(int oid) {
		return loadByGA(oid, "ST-GDATA-ACTOR");
	}

	public static Posts loadByGA(int oid, String gncjActorKey) {
		List<JsonElement> o1 = (List<JsonElement>) ZBin.GSV.invokeMsg(MsvCallMsg.Method.MPOSTSET + " " + oid + " %{{" + gncjActorKey + "}}");
		Posts posts = Posts.of(o1);
		return posts;
	}

	@Override
	public String toString() {
		List<Post> firstLast = getFirstLast();
		return "Posts*" + X.sizeOf(posts) + "|" + firstLast;
	}

	private Posts(List posts) {
		this.posts = (List) posts.stream().map(p -> {
			if (p instanceof JsonElement) {
				return Post.of((JsonElement) p);
			}
			return p;
		}).collect(Collectors.toList());
		sort(true);
	}

	public static Posts of(List posts) {
		return new Posts(posts);
	}

	public int ownerId() {
		return ARRi.first(posts).getOwnerId();
	}

	public Posts sort(boolean ascDesc) {
		Collections.sort(posts, (p1, p2) -> {
			int i = p1.getPostId().compareTo(p2.getPostId());
			if (i == 0) {
				return 0;
			}
			return i < 0 ? (ascDesc ? -1 : 1) : (ascDesc ? 1 : -1);
		});
		return this;
	}

	public List<Post> getFirstLast() {
		return ARRi.getFirstLast(posts);
	}

	public List<Post> getFirstLastMany(int count, boolean firstLast) {
		return firstLast ? ARRi.firstMany(posts, count, posts) : ARRi.lastMany(posts, count, posts);
	}
}
