package zk_notes.node_srv;

import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import lombok.SneakyThrows;
import mpe.wthttp.*;
import mpu.IT;
import mpu.X;
import mpu.pare.Pare3;
import zk_notes.node.NodeDir;

public class GroovyCallService {

	@SneakyThrows
	public static Object doGroovyCall_VALUE(NodeDir node, boolean allowNullValue) {
		Pare3<GroovyCallMsg, Object, Throwable> groovyCallRslt = doGroovyCall(node);
		if (groovyCallRslt.getExt() != null) {
			X.throwException(groovyCallRslt.getExt());
		}
		Object value = groovyCallRslt.val();
		return allowNullValue ? value : IT.NN(value, "Except not null value from groovy call node '%s'", node.nodeId());
	}

	//				Path pathFc = nodeDir.getPathFormFc();
	//				try {
	//					String rslt = new GroovyShell().evaluate(pathFc.toFile()) + "";
	//					throw new CleanDataResponseException(rslt);
	//				} catch (CompilationFailedException e) {
	//					L.error("Error compile groovy script: file://" + pathFc.toAbsolutePath(), e);
	//					throw new CleanDataResponseException(400, e.getMessage());
	//				} catch (GroovyRuntimeException e) {
	//					L.error("Error runtime groovy script: file://" + pathFc.toAbsolutePath(), e);
	//					throw new CleanDataResponseException(400, e.getMessage());
	//				} catch (IOException e) {
	//					L.error("Error evaluate groovy script: file://" + pathFc.toAbsolutePath(), e);
	//					throw new CleanDataResponseException(500, e.getMessage());
	//				}
	@SneakyThrows
	public static Pare3<GroovyCallMsg, Object, Throwable> doGroovyCall(NodeDir node) {
		GroovyCallMsg groovyCallMsg = GroovyCallMsg.of(node);
		try {
//			File file = node.getPathFormFc().toFile();
			String nodeData = InjectNode.inject(node).nodeData;
			Object o = new GroovyShell().evaluate(nodeData);
			return Pare3.of(groovyCallMsg, o, null);
		} catch (GroovyRuntimeException e) {
			return Pare3.of(groovyCallMsg, null, e);
		}

	}

}
