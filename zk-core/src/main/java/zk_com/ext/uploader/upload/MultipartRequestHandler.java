package zk_com.ext.uploader.upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Zoya Novikova
 * @since 11.12.2013
 */
public class MultipartRequestHandler {

	DiskFileItemFactory factory;

	public MultipartRequestHandler(ServletContext context) {
		FileCleaningTracker fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(context);
		factory = new DiskFileItemFactory();
		factory.setFileCleaningTracker(fileCleaningTracker);
	}

	public List<FileItem> uploadFiles(HttpServletRequest request) throws FileUploadException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("UTF-8");
			return upload.parseRequest(request);
		}
		return newArrayList();
	}
}
