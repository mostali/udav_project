package mp.jira.api;

import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import lombok.SneakyThrows;
import mp.jira.JiraCli;
import mpc.arr.S_;
import mpc.fs.UFS;
import mpc.net.CON;
import mpc.net.DLD;
import mpc.url.Url0;
import mpe.str.URx;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.str.STR;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ApiAttach extends Api0 {

	public ApiAttach(JiraCli jiraCli) {
		super(jiraCli);
	}


	//
	//
	//

	//https://developer.atlassian.com/cloud/jira/platform/basic-auth-for-rest-apis/
	@SneakyThrows
	public List<String> downloadAttachs(Issue issue, long commentId, Path toDir, boolean skipExisted) {

		Comment comment = S_.findFirst(ARR.toList(issue.getComments()), c -> c.getId().equals(commentId));

		Path toDirComment = toDir.resolve(commentId + "");

		UFS.MKDIR.createDirsOrSingleDirOrCheckExist(toDirComment, true);

		List<Attachment> attachments = ARR.toList(issue.getAttachments());

		List<String> markAttahs = pickAttach(comment.getBody());

		List<Attachment> commentAttachs = S_.filterToAll(attachments, (Attachment i) -> markAttahs.contains(i.getFilename()));
		IT.state(markAttahs.size() == commentAttachs.size());
		if (JiraCli.L.isInfoEnabled()) {
			JiraCli.L.info("Found attach '{}' files for comment '{}'", X.sizeOf(commentAttachs), commentId);
		}
		String[][] headers = CON.HEADERS(jc.getUser().getAuthBasic());

		List<String> downlaoded = new ArrayList<>();
		for (Attachment attachment : commentAttachs) {
			if (JiraCli.L.isInfoEnabled()) {
				JiraCli.L.info("Start download file '{}' by uri '{}' to '{}'", attachment.getContentUri().toURL(), attachment.getFilename(), toDirComment);
			}

			String file = toDirComment.resolve(attachment.getFilename()).toString();
			if (skipExisted && UFS.existFile(file)) {
				if (JiraCli.L.isInfoEnabled()) {
					JiraCli.L.info("SKIP download file '{}' by uri '{}' to '{}'", attachment.getContentUri().toURL(), attachment.getFilename(), toDirComment);
				}
				continue;
			}
			DLD.url2file0(attachment.getContentUri().toURL(), file, headers);
			downlaoded.add(file);
		}
		return downlaoded;
	}

	private static List<String> pickAttach(String commentContent) {
		List<String> all = S_.mapToAll(URx.findAllGroup(commentContent, "(\\[\\^.+?\\])"), (String s) -> STR.substrCount(s, 2, 1));
		return all;
	}


	public List<String> downloadFilesFromComment(String urlWithComment, String dir, boolean skipExisted) {
//		JiraUser user = createUsr_DefaultORG(usrOrgAlias);
//		JiraCli myJiraClient = JiraCliCreator.create().getOrCreateByUsr(user);
		Pare<String, Integer> stringIntegerPare = parseIssueIdAndCommentId(urlWithComment);
		Issue issue = jc._Com().getIssue(stringIntegerPare.key());
		return downloadAttachs(issue, stringIntegerPare.val(), Paths.get(dir), skipExisted);
	}


	//
	//
	//

	public List<Comment> getAllComments(String issueKey) {
		Issue issue = jc._Com().getIssue(issueKey);
		return StreamSupport.stream(issue.getComments().spliterator(), false).collect(Collectors.toList());
	}

	public static Pare<String, Integer> parseIssueIdAndCommentId(String url) {
		Url0 urlInfo = Url0.ofQk(url);
		return Pare.of(urlInfo.pagenameLast(), urlInfo.queryUrl().getFirstAs("focusedCommentId", Integer.class, null));

	}

	public void addComment(Issue issue, String commentBody) {
		rc().getIssueClient().addComment(issue.getCommentsUri(), Comment.valueOf(commentBody));
	}
}
