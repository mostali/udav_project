package zk_notes.apiv1.treenode;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import mpc.arr.STREAM;
import mpc.exception.WhatIsTypeException;
import mpc.log.L;
import mpe.core.ERR;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.str.JOIN;
import mpu.str.STR;
import mpu.str.TKN;
import zk_notes.apiv1.client.NoteApi0;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class DockerRestCall {

	public static void main(String[] args) {
		NoteApi0.ofZZNoteRu0();
	}

	final Pare<String, String> path_i_query;

	public DockerRestCall(Pare<String, String> path_i_query) {
		this.path_i_query = path_i_query;
	}

	public Pare<Integer, String> apply() {

		String key = path_i_query.key();

		Path path = Paths.get(key);

		int nameCount = path.getNameCount();

		if (nameCount == 1) {

			// _aci

			return DockerSrv.getVersion();
		}

		String operation = path.getName(1).toString();

		DockerSrv.Oper oper = DockerSrv.Oper.valueOf(operation, null);

		// _aci/nginx/.index/nginx/p7000.nx

		if (nameCount == 2) {

			switch (oper) {
				case ls:

					// _aci/ls

					List<Container> allContainers = DockerSrv.getAllContainers();
					String s = STREAM.mapToList(allContainers, c -> ARR.as(c.getNames()) + " >>> " + c.getId() + STR.NL) + "";

					return Pare.of(200, s);

				case lsi:

					// _aci/lsi

					List<Image> allImages = DockerSrv.getAllImages();

					String si = allImages.stream().map(img -> img.getId() + ":" + ARR.as(img.getRepoTags())).filter(X::NE).collect(Collectors.joining(STR.NL));

//							STREAM.mapToList(allImages, c -> STREAM.filterToAll(ARR.as(c.getRepoTags()), X::NE) + "" + STR.NL) + "";
					return Pare.of(200, si);

				default:
					return Pare.of(400, "unsupported args2");
			}
		}

		if (nameCount == 3) {

			// _aci/start/cid

			String cid_or_imgId = path.getName(2).toString();
			try {
				String apply = oper.apply2(cid_or_imgId);
				return Pare.of(200, apply);
			} catch (Exception ex) {
				return Pare.of(400, ERR.getMessagesAsStringWithHead(ex, "apply operation"));
			}
		}

		//		nameCount => 3

		switch (oper) {

			case build: {

				//http://q.com:8080/_aci/build/ab:3/path/to/Dockerfile

				String image = STR.decodeRSlash(path.getName(2).toString());

				List<String> strings = STREAM.mapToList(ARR.toList(path.iterator()), String::valueOf);
				List pathToDockerFile = ARR.sublist(strings, 3);
				Path pathDockerfile = Paths.get("/" + JOIN.allBy("/", pathToDockerFile));
				L.info("Run build container with Image '{}' & Dockerfile 'file://{}'", image, pathDockerfile);

				IT.isFileExist(pathDockerfile);

				String newImage = DockerSrv.buildImage(image, pathDockerfile.toString());

				return Pare.of(200, "Created:" + newImage);

			}

			case create: {

				//http://q.com:8080/_aci/create/ab1/ab:1/8081:8080/java/-jar/beaapp.jar

				String name = path.getName(2).toString();
				String image = STR.decodeRSlash(path.getName(3).toString());
				String portBinding = path.getName(4).toString();

				TKN.twoAs(portBinding, ":", Integer.class);//check

				List<String> strings = STREAM.mapToList(ARR.toList(path.iterator()), String::valueOf);

				try {

					List cmd = ARR.sublist(strings, 5);
					L.info("Run container with cmd:" + cmd);
					String container = DockerSrv.createContainer(name, image, portBinding, cmd);

					return Pare.of(200, "Created:" + container);

				} catch (Exception ex) {

					return Pare.of(400, ERR.getMessagesAsStringWithHead(ex, "apply operation"));

				}

			}

			default:
				throw new WhatIsTypeException(oper);

		}
	}


}
